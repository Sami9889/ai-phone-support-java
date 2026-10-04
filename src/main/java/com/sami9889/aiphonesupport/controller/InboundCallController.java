package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.domain.CallSession;
import com.sami9889.aiphonesupport.domain.Client;
import com.sami9889.aiphonesupport.repository.ClientRepository;
import com.sami9889.aiphonesupport.service.AiPhoneSupportService;
import com.sami9889.aiphonesupport.service.AuditLogService;
import com.sami9889.aiphonesupport.service.CallSessionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/calls")
public class InboundCallController {

    private final AiPhoneSupportService aiPhoneSupportService;
    private final ClientRepository clientRepository;
    private final CallSessionService callSessionService;
    private final AuditLogService auditLogService;

    public InboundCallController(AiPhoneSupportService aiPhoneSupportService,
                                ClientRepository clientRepository,
                                CallSessionService callSessionService,
                                AuditLogService auditLogService) {
        this.aiPhoneSupportService = aiPhoneSupportService;
        this.clientRepository = clientRepository;
        this.callSessionService = callSessionService;
        this.auditLogService = auditLogService;
    }

    @PostMapping(value = "/inbound", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> inboundCall(@RequestBody(required = false) Map<String, Object> payload,
                                            @RequestParam Map<String, String> params) {
        String calledNumber = payload != null && payload.get("to") != null ? payload.get("to").toString() : params.get("To");
        String callerNumber = payload != null && payload.get("from") != null ? payload.get("from").toString() : params.getOrDefault("From", "Unknown caller");

        Optional<Client> client = clientRepository.findByPhoneNumber(calledNumber);
        if (client.isEmpty()) {
            return ResponseEntity.ok("<Response><Say>Sorry, this number is not recognized.</Say><Hangup/></Response>");
        }

        try {
            CallSession session = callSessionService.initializeInboundCall(calledNumber, callerNumber);
            String companyName = client.get().getCompanyName();
            String greeting = aiPhoneSupportService.buildGreeting(callerNumber);

            String xml = """
                <Response>
                  <Say voice="woman" language="en-US">%s</Say>
                  <Say>Welcome to %s support. Please tell us how we can help you today.</Say>
                  <Gather input="speech dtmf" action="/api/calls/handle-speech?callId=%s" method="POST" timeout="5" speechTimeout="auto">
                    <Say>Please tell us how we can help you, or press 0 for a human agent.</Say>
                  </Gather>
                </Response>
                """.formatted(greeting, companyName, session.getCallId());

            callSessionService.updateCallStatus(session.getCallId(), "ringing");
            auditLogService.record("CALL_INBOUND", "Inbound call accepted", client.get().getId(), session.getCallId());
            return ResponseEntity.ok(xml);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok("<Response><Say>Sorry, this number is not recognized.</Say><Hangup/></Response>");
        }
    }

    @PostMapping(value = "/handle-speech", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> handleSpeech(@RequestParam String callId,
                                             @RequestBody(required = false) Map<String, Object> payload,
                                             @RequestParam Map<String, String> params) {
        String transcript = payload != null && payload.get("transcript") != null
                ? payload.get("transcript").toString()
                : params.getOrDefault("SpeechResult", "");

        String responseText = aiPhoneSupportService.generateReply(transcript);

        try {
            callSessionService.recordTranscript(callId, transcript, responseText);
        } catch (IllegalArgumentException ignored) {
        }

        String xml = """
            <Response>
              <Say voice="woman" language="en-US">%s</Say>
              <Pause length="1"/>
              <Say>Would you like to hear our support options again or speak with a human agent?</Say>
            </Response>
            """.formatted(responseText);

        return ResponseEntity.ok(xml);
    }
}
