Connect Chat Integration Platform
A Java, container/orchestrator-agnostic integration platform: omni-channel ingress (web, mobile, partner API, batch/file, messaging/event) bridged to legacy back-end egress (SOAP, mainframe/MQ, legacy REST/DB), targeting 99.999% availability. Built by a hybrid pod of 2-3 human engineers plus a roster of coding agents.

Start here:

.specify/memory/constitution.md — the non-negotiable engineering principles every spec, plan, and PR is checked against.
AGENTS.md — the agent roster, guardrails, and how they're invoked locally and in CI.
CONTRIBUTING.md — the change process, spec-kit commands, and local dev instructions.
docs/adr/ — architecture decisions, including one open decision the Tech Lead still needs to make (ADR 0002: the RPO/RTO=0 target is in tension with the 100ms P99 latency budget).
Architecture
[Omni-channel callers]                                   [Legacy back ends]
  Web/Mobile clients  \                                  / Mainframe (MQ/fixed-width)
  Partner REST/webhook -> [Ingress layer] -> [Core domain] -> [Egress layer] -> SOAP service
  Batch/file drop      /        |                              \ Legacy REST/DB
  Kafka event consumer         v
                        [Config + Persistence Harness]
                        (Spring Cloud Config, H2 + Redis locally)
Ingress adapters (platform-service/.../ingress/<channel>) normalize every channel into a single IngressCommand. Only a REST adapter exists today; webhook, Kafka-consumer, and batch/file adapters are additive — each gets its own /speckit-specify run.
Domain core (platform-service/.../domain) is channel- and legacy- protocol-agnostic by construction (constitution principle V).
Egress adapters (platform-service/.../egress/<target>) implement the common LegacyGatewayPort. Only a SOAP adapter stub exists today (unimplemented — see the TODO in SoapLegacyGatewayAdapter); mainframe/MQ and legacy REST/DB adapters are additive.
Persistence harness (platform-service/.../persistence) — StatePort (idempotency claims + outbox, JPA/H2 locally) and CachePort (Redis) keep the app nodes stateless (constitution principle X).
Config harness — config-server module (Spring Cloud Config Server, native profile) serves config-repo/; platform-service also carries offline fallback defaults so a single engineer or agent sandbox never needs a running config-server to boot.
Resilience — every egress call is wrapped in a named resilience4j circuit breaker/retry/bulkhead (constitution principle VII); instances and thresholds live in config-repo/platform-service.yml.
Quickstart
# Full local stack (config-server + Kafka + Redis + the app), fully offline
docker compose -f deploy/docker/docker-compose.yml up --build

# Or just the app, offline, no other services required
mvn -pl platform-service spring-boot:run -Dspring-boot.run.profiles=local

# Build + test everything
mvn -B verify
Once running: POST http://localhost:8080/api/v1/ingress with {"payload": "..."} (optionally set X-Correlation-Id for idempotent retries). The SOAP egress adapter is currently a stub, so requests will settle into ACCEPTED_QUEUED_FOR_EGRESS (the outbox) rather than ACCEPTED_SETTLED — that's expected until the SOAP client is implemented.

If you run platform-service standalone (not via docker-compose) without a local Redis, /actuator/health reports DOWN because the Redis health indicator can't connect — the app still serves ingress requests correctly (Redis only backs the optional cache tier). Run the full docker-compose stack, or run a local Redis, if you need /actuator/health to report UP.

Status
This is the platform-foundation skeleton: the ingress → domain → egress → persistence/config wiring exists and compiles/tests, proving the pattern end to end, but most adapters are stubs. The next step is running /speckit-specify for the first real feature (see CONTRIBUTING.md).

Nine open decisions from the initial brief were resolved (Maven, Kafka, the 5-nines/latency numbers, H2 + Redis, GitHub Actions, Claude Code agents); one remains genuinely open — see ADR 0002.
