package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.dto.OutboundCallRequest;
import com.sami9889.aiphonesupport.domain.CallSession;
import com.sami9889.aiphonesupport.service.AuditLogService;
import com.sami9889.aiphonesupport.service.CallSessionService;
import com.sami9889.aiphonesupport.service.OutboundCallGateway;
import com.sami9889.aiphonesupport.service.OutboundTelephonyUnavailableException;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calls")
public class OutboundCallController {

    private final CallSessionService callSessionService;
    private final AuditLogService auditLogService;
    private final OutboundCallGateway outboundCallGateway;

    public OutboundCallController(
            CallSessionService callSessionService,
            AuditLogService auditLogService,
            OutboundCallGateway outboundCallGateway) {
        this.callSessionService = callSessionService;
        this.auditLogService = auditLogService;
        this.outboundCallGateway = outboundCallGateway;
    }

    @PostMapping(value = "/outbound", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> initiateOutboundCall(@Valid @RequestBody OutboundCallRequest request) {
        CallSession session;
        try {
            session = callSessionService.initializeOutboundCall(request.clientId(), request.toNumber());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        try {
            String gatewayChannelId = outboundCallGateway.originate(
                    session.getCallId(), session.getFromNumber(), session.getToNumber());
            session = callSessionService.recordGatewayChannelId(session.getCallId(), gatewayChannelId);
            auditLogService.record("CALL_OUTBOUND", "Outbound call initiated", request.clientId(), session.getCallId());

                return ResponseEntity.accepted().body(Map.of(
                    "callId", session.getCallId(),
                    "gatewayChannelId", gatewayChannelId,
                    "from", session.getFromNumber(),
                    "to", session.getToNumber(),
                    "status", "initiated"
            ));
        } catch (OutboundTelephonyUnavailableException e) {
            callSessionService.updateCallStatus(session.getCallId(), "failed");
            auditLogService.record("CALL_OUTBOUND_FAILED", "Outbound gateway unavailable", request.clientId(), session.getCallId());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "callId", session.getCallId(),
                    "status", "failed",
                    "error", e.getMessage()));
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
