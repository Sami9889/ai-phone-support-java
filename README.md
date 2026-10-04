# AI Phone Support Java

A Java-owned AI phone support core for client onboarding, number ownership, call lifecycle, AI orchestration, and auditing. Inbound webhooks are provider-neutral; outbound PSTN calls can be originated through Asterisk ARI connected to a Telstra SIP trunk.

This project models the core behavior of a telecom platform:
- client registration
- automatic phone number assignment
- inbound call routing
- outbound call control
- call session logging
- AI support response generation

## Features

- Java 21 + Spring Boot 3
- H2 in-memory database for local development
- client-based phone number assignment
- inbound phone call webhook flow
- outbound call initiation
- call history tracking
- AI support response logic

## Core flow

1. Register a client with company info
2. System creates a dedicated support number for that client
3. A carrier webhook is represented through custom endpoints
4. Incoming call is looked up by the called number
5. AI support responds to the caller speech
6. Calls are logged and stored in the database

## Architecture

- `domain`: clients, phone-number assignments, call sessions, and audit records
- `service`: registration, assignment ownership lookup, call lifecycle, AI response/escalation decisions, audit writes, and call-event publication
- `controller`: client registration, inbound/speech webhooks, outbound call initiation, and call history
- `model`: persistence is owned only by the canonical `domain` package

Speech handling persists the transcript and AI response, records whether human escalation was requested, and writes audit events without copying transcript text into audit summaries or Kafka events. The escalation decision is available to a future routing adapter; this prototype does not yet bridge calls to a live agent.

## API endpoints

- `POST /api/clients/register`
- `POST /api/calls/inbound`
- `POST /api/calls/handle-speech`
- `POST /api/calls/outbound`
- `POST /api/calls/outbound/answer`
- `POST /api/calls/outbound/complete`
- `GET /api/calls/history/{clientId}`
- `GET /api/health`

## Example registration payload

```json
{
  "companyName": "Acme Corp",
  "contactName": "Jane Smith",
  "email": "jane@acme.com",
  "countryCode": "US",
  "useCase": "Customer support",
  "phoneNumber": "+14155550123"
}
```

`phoneNumber` is optional in local development, where the app generates a placeholder. When outbound dialing is enabled, provide a Telstra-provisioned E.164 number authorized for caller ID.

## Example response

```json
{
  "clientCode": "...",
  "companyName": "Acme Corp",
  "phoneNumber": "+14155000001",
  "status": "ACTIVE",
  "provider": "custom-telephony"
}
```

## Local startup

```bash
mvn spring-boot:run
```

Then call the app with your browser or a local HTTP client.

The default profile uses H2, in-memory caching, and a disabled Kafka publisher. It does not require infrastructure services.

## Infrastructure

The `prod` profile configures PostgreSQL with Flyway migrations, Redis-backed call-session lookup caching, Kafka call events, Prometheus metrics, OAuth2 JWT validation, and an optional Asterisk ARI outbound gateway. Schema changes belong in `src/main/resources/db/migration`; Hibernate validates the production schema instead of modifying it.

Start the local production-like stack with Docker Compose:

```bash
docker compose up --build
```

The app is available at `http://localhost:8080`, Prometheus at `http://localhost:9090`, and Grafana at `http://localhost:3000` (local-only default credentials: `admin` / `local_dev_only`). Compose defaults are for development only. Set `DB_PASSWORD` and `GRAFANA_ADMIN_PASSWORD` before using the stack beyond a local machine. To exercise JWT protection, set `APP_SECURITY_ENABLED=true`, configure `JWT_ISSUER_URI`, and send a valid bearer token to protected API routes. In secured mode, inbound and speech webhooks require `X-Webhook-Token` matching `WEBHOOK_SHARED_SECRET`.

Call events are JSON records containing `eventType`, `callId`, `clientId`, `status`, and `occurredAt`. They are dispatched after the database transaction commits. Transcript text and AI response text are intentionally excluded.

To enable actual outbound dialing, configure an Asterisk ARI endpoint connected to your Telstra SIP trunk, then set `ASTERISK_ARI_ENABLED=true`, `ASTERISK_ARI_BASE_URL`, `ASTERISK_ARI_USERNAME`, and `ASTERISK_ARI_PASSWORD`. Configure the Asterisk PJSIP endpoint name to match `TELSTRA_SIP_ENDPOINT_SUFFIX` (default `@telstra`) and provide the `ai-outbound` dialplan context. Telstra trunk credentials and routing belong in Asterisk, not in this application. Register each client with its Telstra-provisioned caller ID. Without these settings the outbound endpoint returns `503`; it does not report a simulated call as ringing. An accepted originate request returns `202` with an Asterisk channel ID; answer/hangup event synchronization still requires an ARI event consumer.

The Kubernetes manifest in `k8s/deployment.yaml` deploys the app and expects PostgreSQL, Redis, Kafka, and Asterisk ARI connected to a Telstra trunk to be provided by the cluster or managed services. Update the image, ARI URL, and JWT issuer, then create the referenced secret using your cluster's secret-management process with `DB_PASSWORD`, `WEBHOOK_SHARED_SECRET`, `ASTERISK_ARI_USERNAME`, and `ASTERISK_ARI_PASSWORD` keys before applying the manifest.

## Notes

This is a custom telecom implementation for learning and prototyping. PSTN connectivity requires a Telstra SIP trunk and an Asterisk gateway configured for that account; those services and credentials are not bundled with the app.
