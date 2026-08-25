# Sky Payment Reconciler (Java)

**Status: engineering beta.** This repository implements a focused Java 21 batch reconciliation engine for comparing an internal payment ledger with an external provider snapshot.

## Implemented

- exact payment-ID matching
- integer minor-unit amount comparison (no floating-point money)
- three-letter uppercase currency validation
- explicit payment statuses: `SETTLED`, `REFUNDED`, `FAILED`
- outcomes for matched, amount/currency mismatch, status mismatch, missing provider record, and missing ledger record
- duplicate payment-ID rejection
- bounded batches up to 100,000 records per side
- deterministic result ordering
- JUnit tests, Maven verification, dependency scanning
- non-root container packaging and smoke execution

## Build and test

```bash
mvn clean verify
```

## Container

```bash
docker build -t sky-payment-reconciler .
docker run --rm sky-payment-reconciler
```

The container smoke command runs a deterministic sample reconciliation; it is not a payment processor.

## Boundaries

This repository does **not** move money, connect to Stripe/banks/card networks, perform settlement, store PCI data, verify chargebacks, provide accounting approval workflows, or constitute a production financial control. It has no durable database, external-provider adapter, authentication/RBAC, tenant isolation, audit-log persistence, scheduled jobs, or deployment evidence.

A production integration should feed normalized provider and ledger records through stable adapters, persist reconciliation runs and approvals, protect access with RBAC, and include financial-control review before automated actions are permitted.

## SKYCOIN4444 role

`SKYCOIN4444 → Finance/Payments → reconciliation boundary`

## License

See `LICENSE`.
