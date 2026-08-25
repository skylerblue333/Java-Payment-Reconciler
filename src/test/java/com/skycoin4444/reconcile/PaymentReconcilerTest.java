package com.skycoin4444.reconcile;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class PaymentReconcilerTest {
    private static PaymentReconciler.Payment p(String id, long amount, PaymentReconciler.Status status) {
        return new PaymentReconciler.Payment(id, amount, "USD", status);
    }

    @Test
    void identifiesMatchAndAmountMismatch() {
        PaymentReconciler r = new PaymentReconciler();
        var results = r.reconcile(List.of(p("a", 100, PaymentReconciler.Status.SETTLED), p("b", 200, PaymentReconciler.Status.SETTLED)),
            List.of(p("a", 100, PaymentReconciler.Status.SETTLED), p("b", 250, PaymentReconciler.Status.SETTLED)));
        assertEquals(PaymentReconciler.Outcome.MATCHED, results.get(0).outcome());
        assertEquals(PaymentReconciler.Outcome.AMOUNT_MISMATCH, results.get(1).outcome());
    }

    @Test
    void identifiesMissingSidesAndStatusMismatch() {
        PaymentReconciler r = new PaymentReconciler();
        var results = r.reconcile(List.of(p("ledger-only", 100, PaymentReconciler.Status.SETTLED), p("status", 100, PaymentReconciler.Status.SETTLED)),
            List.of(p("status", 100, PaymentReconciler.Status.REFUNDED), p("provider-only", 100, PaymentReconciler.Status.SETTLED)));
        assertEquals(PaymentReconciler.Outcome.MISSING_PROVIDER, results.get(0).outcome());
        assertEquals(PaymentReconciler.Outcome.STATUS_MISMATCH, results.get(1).outcome());
        assertEquals(PaymentReconciler.Outcome.MISSING_LEDGER, results.get(2).outcome());
    }

    @Test
    void rejectsDuplicateIds() {
        PaymentReconciler r = new PaymentReconciler();
        var payment = p("dup", 100, PaymentReconciler.Status.SETTLED);
        assertThrows(IllegalArgumentException.class, () -> r.reconcile(List.of(payment, payment), List.of()));
    }
}
