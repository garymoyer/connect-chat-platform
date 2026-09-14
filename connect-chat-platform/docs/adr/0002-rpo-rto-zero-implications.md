# ADR 0002: RPO/RTO = 0 target vs. the ingress latency budget

**Status**: Proposed — needs Tech Lead decision before implementation
proceeds past the platform-foundation skeleton
**Date**: 2026-09-11

## Context
The platform brief sets:
- Availability target: 99.999% for the whole ingress→egress path.
- Ingress gateway latency budget: P50 10-30ms, P99 target <100ms,
  operational ceiling 150ms (circuit-breaker trip boundary).
- **RPO = 0, RTO = 0.**

RPO = 0 means no data loss is acceptable under any failure, which in
practice requires **synchronous** replication of durable writes (idempotency
claims, outbox events) across at least two failure domains before an ingress
request is acknowledged as accepted. RTO = 0 means no downtime is acceptable
during a failover, which in practice requires an active-active topology with
no human- or even automation-triggered cutover step — the moment one
instance/AZ/region fails, another is already serving with no gap.

These two targets are in tension with the 100ms P99 / 150ms ceiling budget:
synchronous cross-AZ (and especially cross-region) replication adds real
network latency to every write on the ingress-accept path. Depending on
topology, that can be single-digit ms (same-region multi-AZ, same metro) up
to tens of ms (cross-region), which eats directly into the 100ms budget
before any business logic or legacy-egress call runs.

## Options for the Tech Lead to weigh
1. **Multi-AZ synchronous replication, single region.** Achieves RPO=0 and
   RTO≈0 (sub-second automatic failover, not literally zero) for AZ-level
   failure, at low added latency (typically low single-digit ms same-
   region). Does not protect against a full-region outage.
2. **Multi-region active-active with synchronous cross-region writes.**
   Closer to literal RTO=0 including region failure, but adds tens of ms of
   latency to every write — likely incompatible with the 100ms P99 budget
   as currently stated.
3. **Redefine RPO/RTO=0 as "no *acknowledged* data loss, near-zero
   failover"** — i.e., synchronous multi-AZ (option 1) plus asynchronous
   cross-region replication as a documented, bounded residual-risk window
   (a real RPO in seconds, not zero, for the region-loss case specifically).
   This is the most common real-world interpretation of "RPO/RTO 0" SLAs and
   is compatible with the stated latency budget.

## Decision
**Not yet made.** Recorded here so no agent or engineer quietly picks one of
the above during implementation. The Reliability Agent is instructed
(`.claude/agents/reliability-agent.md`) to escalate to the Tech Lead rather
than approve a design that resolves this silently.

## Consequences (once decided)
Whichever option is chosen determines: the persistence harness's replication
topology (beyond the local H2/Redis dev harness), whether "container/
orchestrator agnostic" (principle II) needs to explicitly include "region-
topology agnostic," and whether the 100ms P99 number needs revisiting for
paths that touch cross-region writes.
