package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.dto.OutboundCallRequest;
import com.sami9889.aiphonesupport.domain.CallSession;
import com.sami9889.aiphonesupport.service.AuditLogService;
import com.sami9889.aiphonesupport.service.CallSessionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calls")
public class OutboundCallController {

    private final CallSessionService callSessionService;
    private final AuditLogService auditLogService;

    public OutboundCallController(CallSessionService callSessionService, AuditLogService auditLogService) {
        this.callSessionService = callSessionService;
        this.auditLogService = auditLogService;
    }

    @PostMapping(value = "/outbound", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> initiateOutboundCall(@RequestBody OutboundCallRequest request) {
        try {
            CallSession session = callSessionService.initializeOutboundCall(request.clientId(), request.toNumber());
            callSessionService.updateCallStatus(session.getCallId(), "ringing");
            auditLogService.record("CALL_OUTBOUND", "Outbound call initiated", request.clientId(), session.getCallId());

            return ResponseEntity.ok(Map.of(
                    "callId", session.getCallId(),
                    "from", session.getFromNumber(),
                    "to", session.getToNumber(),
                    "status", "ringing"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/outbound/answer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> answerOutboundCall(@RequestParam String callId) {
        try {
            CallSession session = callSessionService.updateCallStatus(callId, "answered");
            return ResponseEntity.ok(Map.of(
                    "callId", session.getCallId(),
                    "status", "answered"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/outbound/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> completeOutboundCall(@RequestParam String callId) {
        try {
            CallSession session = callSessionService.updateCallStatus(callId, "completed");
            return ResponseEntity.ok(Map.of(
                    "callId", session.getCallId(),
                    "status", "completed"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping(value = "/history/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CallSession>> getCallHistory(@PathVariable Long clientId) {
        return ResponseEntity.ok(callSessionService.getCallHistory(clientId));
    }
}
