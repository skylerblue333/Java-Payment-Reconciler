package com.skycoin4444.reconcile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PaymentReconciler {
    public enum Status { SETTLED, REFUNDED, FAILED }
    public enum Outcome { MATCHED, AMOUNT_MISMATCH, STATUS_MISMATCH, MISSING_PROVIDER, MISSING_LEDGER }

    public record Payment(String id, long amountMinor, String currency, Status status) {
        public Payment {
            if (id == null || !id.matches("[A-Za-z0-9_.-]{1,80}")) throw new IllegalArgumentException("invalid payment id");
            if (amountMinor < 0) throw new IllegalArgumentException("amountMinor must be non-negative");
            if (currency == null || !currency.matches("[A-Z]{3}")) throw new IllegalArgumentException("currency must be ISO-like 3 uppercase letters");
            Objects.requireNonNull(status, "status");
        }
    }

    public record Result(String id, Outcome outcome, Payment ledger, Payment provider) {}

    public List<Result> reconcile(List<Payment> ledger, List<Payment> provider) {
        Map<String, Payment> left = index(ledger, "ledger");
        Map<String, Payment> right = index(provider, "provider");
        List<Result> results = new ArrayList<>();

        for (Payment payment : left.values()) {
            Payment external = right.remove(payment.id());
            if (external == null) {
                results.add(new Result(payment.id(), Outcome.MISSING_PROVIDER, payment, null));
            } else if (payment.amountMinor() != external.amountMinor() || !payment.currency().equals(external.currency())) {
                results.add(new Result(payment.id(), Outcome.AMOUNT_MISMATCH, payment, external));
            } else if (payment.status() != external.status()) {
                results.add(new Result(payment.id(), Outcome.STATUS_MISMATCH, payment, external));
            } else {
                results.add(new Result(payment.id(), Outcome.MATCHED, payment, external));
            }
        }
        for (Payment external : right.values()) {
            results.add(new Result(external.id(), Outcome.MISSING_LEDGER, null, external));
        }
        return List.copyOf(results);
    }

    private static Map<String, Payment> index(List<Payment> payments, String source) {
        if (payments == null || payments.size() > 100_000) throw new IllegalArgumentException(source + " batch invalid or too large");
        Map<String, Payment> indexed = new LinkedHashMap<>();
        for (Payment payment : payments) {
            if (indexed.putIfAbsent(payment.id(), payment) != null) throw new IllegalArgumentException("duplicate " + source + " payment id: " + payment.id());
        }
        return indexed;
    }
}
