# AGENTS.md — Agent Roster & Operating Manual

This repository is built by a **hybrid pod**: 2-3 human engineers plus the
coding agents below. This file is the index; each agent's full configuration
(system prompt, tools, model, guardrails) is the authoritative source at
`.claude/agents/<name>.md` — those are real Claude Code subagent definitions,
runnable locally via the `Agent` tool and in CI (see "Running agents in CI"
below).

Everything here operates under `.specify/memory/constitution.md`. If
anything in this file ever conflicts with the constitution, the constitution
wins.

## Roster

| Agent | Config | Model | Primary input | Primary output | Can it merge? |
|---|---|---|---|---|---|
| Spec Agent | [`.claude/agents/spec-agent.md`](.claude/agents/spec-agent.md) | sonnet | Human intent, existing specs | `/speckit-specify` + `/speckit-plan` drafts | No |
| Implementation Agent | [`.claude/agents/implementation-agent.md`](.claude/agents/implementation-agent.md) | opus | Approved `/speckit-tasks` output | Code + tests on a feature branch | No |
| Review Agent | [`.claude/agents/review-agent.md`](.claude/agents/review-agent.md) | sonnet | Open PR diff | Inline review comments + pass/fail gate | No |
| Security Agent | [`.claude/agents/security-agent.md`](.claude/agents/security-agent.md) | opus | PR diff touching ingress/egress/auth/deps | SAST/dependency/secret findings, threat-model checklist | No |
| Reliability Agent | [`.claude/agents/reliability-agent.md`](.claude/agents/reliability-agent.md) | sonnet | PR diff + perf results | SLO/latency-budget conformance report | No |
| Docs Agent | [`.claude/agents/docs-agent.md`](.claude/agents/docs-agent.md) | haiku | Merged PRs | Updated README/API docs/ADR index | No |

**No agent has merge rights to `main`.** Agents open PRs, run in CI, and
comment. Only the Tech Lead or a delegated Senior Engineer approves and
merges — see `.specify/memory/constitution.md` § Operating Model & Agent
Governance.

## Guardrails common to every agent

1. **Scoped, logged identity.** Each agent runs under its own CI service
   account/token when running in GitHub Actions — never a shared "agents"
   credential, never a human's personal credentials.
2. **Traceable output.** Every commit/PR an agent produces references the
   spec-kit task ID that authorized it.
3. **Ask, don't assume.** Any agent hitting ambiguity, a missing NFR number,
   or a possible constitution conflict stops and asks a human rather than
   guessing.
4. **Least privilege file access.** Each agent's `.claude/agents/<name>.md`
   states exactly which directories it may write to. Treat that as a hard
   boundary, not a suggestion.

## Change flow (who does what, in order)

See `CONTRIBUTING.md` for the full diagram. Short version:

1. Human has an idea → checks it fits the constitution.
2. **Spec Agent** drafts the spec; a **Senior Engineer** approves it.
3. **Spec Agent** drafts the plan; the **Tech Lead** approves it (this is
   where contract/schema/NFR decisions get locked in).
4. `/speckit-tasks` generates the task list.
5. **Implementation Agent(s)** work tasks on a feature branch, open a PR.
6. **Review Agent** does a first pass; **Security Agent** and **Reliability
   Agent** gate anything touching ingress/egress/auth/deps or the request
   path.
7. A **human** reviews and merges. No agent merges to `main`.
8. **Docs Agent** updates docs/ADR index after merge.

## Running agents locally

Each agent is a standard Claude Code project subagent. From a Claude Code
session in this repo:

```
Use the implementation-agent to work task T-014 from specs/001-platform-foundation/tasks.md
```

or invoke explicitly via the `Agent` tool with `subagent_type` matching the
`name:` field in the relevant `.claude/agents/<name>.md` file.

## Running agents in CI

`.github/workflows/agent-gates.yml` runs Review/Security/Reliability agents
on every PR via the Claude Code GitHub Action, each with its own
`ANTHROPIC_API_KEY`-backed, least-privilege GitHub token (`contents: read`,
`pull-requests: write` only — no `contents: write`, so a CI agent
structurally cannot push to the repo, only comment). See that workflow file
for the exact wiring, and update it if you add or retire an agent role.
