# AI Phone Support Java

A Spring Boot starter project for an AI-powered phone support system that can answer incoming calls, capture speech, and route to a human agent when needed.

Features:
- Twilio phone number integration
- Webhook-based call handling
- Voice call greeting via TwiML
- Speech transcription placeholder flow
- AI support routing based on customer intent
- Support number assignment service
- Spring Boot app structure for extension

## Tech stack
- Java 21
- Spring Boot 3.3.x
- Twilio Java SDK
- Maven

## Quick start

1. Clone the repository
2. Set these environment variables:
   - `TWILIO_ACCOUNT_SID`
   - `TWILIO_AUTH_TOKEN`
   - `TWILIO_PHONE_NUMBER`
   - `OPENAI_API_KEY` (optional, for real AI responses)
3. Run:

```bash
mvn spring-boot:run
```

4. Expose the app through a public URL using ngrok or a deployment platform.
5. Configure your Twilio phone number webhook to:
   - `https://your-domain/api/calls/incoming`

## Endpoints

- `GET /actuator/health` — health check
- `POST /api/calls/incoming` — Twilio webhook for new call
- `POST /api/calls/handle-speech` — receives speech input from caller
- `POST /api/phone/assign` — returns a configured support number for use

## Example flow

1. Customer calls Twilio number
2. Backend returns TwiML greeting and `Gather`
3. Caller speaks issue
4. Backend interprets request
5. AI replies or transfers to human support

## Notes

This is a starter project intended to be expanded with:
- real speech-to-text integration
- LLM-powered responses
- CRM or ticketing system integration
- call logging and analytics
- escalation workflow

## Production considerations

- Keep secrets in environment variables or a secret manager
- Validate inbound call signatures from Twilio
- Add call recording and transcript storage
- Add retry and queueing for external AI APIs
- Use a robust DB for call sessions and customer interactions
