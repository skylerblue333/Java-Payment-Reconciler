package com.skycoin4444.reconcile;

import java.util.List;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        PaymentReconciler reconciler = new PaymentReconciler();
        var sample = new PaymentReconciler.Payment("sample-1", 1000, "USD", PaymentReconciler.Status.SETTLED);
        var results = reconciler.reconcile(List.of(sample), List.of(sample));
        System.out.println("{\"service\":\"sky-payment-reconciler\",\"status\":\"ready\",\"sampleOutcome\":\"" + results.getFirst().outcome() + "\"}");
    }
}
