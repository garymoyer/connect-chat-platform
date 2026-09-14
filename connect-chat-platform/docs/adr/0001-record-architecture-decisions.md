# ADR 0001: Record architecture decisions

**Status**: Accepted
**Date**: 2026-09-11

## Context
The hybrid pod (2-3 humans + agents) needs a durable, reviewable record of
irreversible or architecturally significant decisions that outlives chat
history and individual PR descriptions — especially important given
Implementation/Review/Security/Reliability agents rotate context frequently
and must be able to recover *why* a decision was made, not just *what* it
was.

## Decision
We record architecture decisions as numbered ADRs under `docs/adr/`, one per
decision, using this file as the template shape (Status / Date / Context /
Decision / Consequences). Any change to `.specify/memory/constitution.md`,
and any decision the constitution reserves for the Tech Lead, gets an ADR.

## Consequences
- Agents (especially Docs Agent) must check `docs/adr/` before assuming the
  rationale behind an existing architectural choice.
- The Tech Lead owns ADR approval; Spec Agent may draft a stub but must mark
  it `[NEEDS CLARIFICATION]` rather than deciding architectural rationale.
