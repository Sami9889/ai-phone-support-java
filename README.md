# AI Phone Support Java

A custom Java AI phone support system built without any third-party telephony provider.

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
3. Telnyx/Twilio-like webhook is simulated through custom endpoints
4. Incoming call is looked up by the called number
5. AI support responds to the caller speech
6. Calls are logged and stored in the database

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
  "useCase": "Customer support"
}
```

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

## Notes

This is a custom telecom scratch implementation for learning and prototyping. It is not a real PSTN carrier, but it mirrors the product structure of a telephony platform.
