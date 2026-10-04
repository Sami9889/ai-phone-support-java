package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.model.ClientProfile;
import com.sami9889.aiphonesupport.repository.ClientRepository;
import com.sami9889.aiphonesupport.service.AiPhoneSupportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/calls")
public class CallController {

    private final AiPhoneSupportService aiPhoneSupportService;
    private final ClientRepository clientRepository;

    public CallController(AiPhoneSupportService aiPhoneSupportService, ClientRepository clientRepository) {
        this.aiPhoneSupportService = aiPhoneSupportService;
        this.clientRepository = clientRepository;
    }

    @PostMapping(value = "/incoming", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> incomingCall(@RequestBody(required = false) Map<String, Object> payload,
                                            @RequestParam Map<String, String> params) {
        String calledNumber = payload != null && payload.get("to") != null ? payload.get("to").toString() : params.get("To");
        String caller = payload != null && payload.get("from") != null ? payload.get("from").toString() : params.getOrDefault("From", "Unknown caller");

        Optional<ClientProfile> client = calledNumber == null ? Optional.empty() : clientRepository.findByPhoneNumber(calledNumber);
        String companyName = client.map(ClientProfile::getCompanyName).orElse("support");
        String message = aiPhoneSupportService.buildInitialGreeting(caller);

        String xml = """
            <Response>
              <Say voice="woman" language="en-US">%s</Say>
              <Say>Welcome to %s support. Please tell us how we can help you today.</Say>
              <Gather input="speech dtmf" action="/api/calls/handle-speech" method="POST" timeout="5" speechTimeout="auto">
                <Say>Please tell us how we can help you, or press 0 for a human agent.</Say>
              </Gather>
            </Response>
            """.formatted(message, companyName);

        return ResponseEntity.ok(xml);
    }

    @PostMapping(value = "/handle-speech", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> handleSpeech(@RequestBody(required = false) Map<String, Object> payload,
                                             @RequestParam Map<String, String> params) {
        String transcript = payload != null && payload.get("transcript") != null
                ? payload.get("transcript").toString()
                : params.getOrDefault("SpeechResult", "");

        String responseText = aiPhoneSupportService.generateReply(transcript);

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
