package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.domain.CallSession;
import com.sami9889.aiphonesupport.domain.Client;
import com.sami9889.aiphonesupport.repository.CallSessionRepository;
import com.sami9889.aiphonesupport.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CallSessionService {

    private final CallSessionRepository callSessionRepository;
    private final CallSessionLookupService callSessionLookupService;
    private final ClientRepository clientRepository;
    private final PhoneNumberProvisioningService phoneNumberProvisioningService;
    private final AiPhoneSupportService aiPhoneSupportService;
    private final AuditLogService auditLogService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CallSession initializeInboundCall(String calledNumber, String callerNumber) {
        Optional<Client> client = phoneNumberProvisioningService.findClientByPhoneNumber(calledNumber);
        if (client.isEmpty()) {
            throw new IllegalArgumentException("No client found for phone number: " + calledNumber);
        }

        CallSession session = new CallSession();
        session.setCallId(UUID.randomUUID().toString());
        session.setClientId(client.get().getId());
        session.setFromNumber(callerNumber);
        session.setToNumber(calledNumber);
        session.setDirection("inbound");
        session.setStatus("initiated");
        session.setCreatedAt(LocalDateTime.now());

        CallSession savedSession = callSessionRepository.save(session);
        publishEvent("CALL_CREATED", savedSession);
        return savedSession;
    }

    public CallSession initializeOutboundCall(Long clientId, String toNumber) {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new IllegalArgumentException("Client not found: " + clientId);
        }

        CallSession session = new CallSession();
        session.setCallId(UUID.randomUUID().toString());
        session.setClientId(clientId);
        session.setFromNumber(phoneNumberProvisioningService.getPrimaryAssignedNumber(clientId));
        session.setToNumber(toNumber);
        session.setDirection("outbound");
        session.setStatus("initiated");
        session.setCreatedAt(LocalDateTime.now());

        CallSession savedSession = callSessionRepository.save(session);
        publishEvent("CALL_CREATED", savedSession);
        return savedSession;
    }

    public CallSession updateCallStatus(String callId, String status) {
        CallSession session = callSessionLookupService.findByCallId(callId);
        session.setStatus(status);
        if ("answered".equals(status)) {
            session.setAnsweredAt(LocalDateTime.now());
        }
        if ("completed".equals(status) || "failed".equals(status)) {
            session.setEndedAt(LocalDateTime.now());
        }

        CallSession savedSession = callSessionRepository.save(session);
        callSessionLookupService.evict(callId);
        publishEvent("CALL_STATUS_UPDATED", savedSession);
        return savedSession;
    }

    public CallSession recordGatewayChannelId(String callId, String gatewayChannelId) {
        CallSession session = callSessionLookupService.findByCallId(callId);
        session.setGatewayChannelId(gatewayChannelId);
        CallSession savedSession = callSessionRepository.save(session);
        callSessionLookupService.evict(callId);
        return savedSession;
    }

    public CallSession recordTranscript(String callId, String transcript, String aiResponse) {
        CallSession session = callSessionLookupService.findByCallId(callId);
        session.setTranscript(transcript);
        session.setAiResponse(aiResponse);
        CallSession savedSession = callSessionRepository.save(session);
        callSessionLookupService.evict(callId);
        publishEvent("CALL_SPEECH_HANDLED", savedSession);
        return savedSession;
    }

    @Transactional
    public SpeechHandlingResult handleSpeech(String callId, String transcript) {
        CallSession session = callSessionLookupService.findByCallId(callId);

        boolean escalationRequested = aiPhoneSupportService.requiresHumanEscalation(transcript);
        String response = aiPhoneSupportService.generateReply(transcript);
        session.setTranscript(transcript);
        session.setAiResponse(response);
        session.setEscalationRequested(escalationRequested);
        callSessionRepository.save(session);
        callSessionLookupService.evict(callId);

        auditLogService.record("CALL_SPEECH_HANDLED", "Call speech processed", session.getClientId(), callId);
        if (escalationRequested) {
            auditLogService.record("CALL_ESCALATION_REQUESTED", "Caller requested human assistance", session.getClientId(), callId);
            publishEvent("CALL_ESCALATION_REQUESTED", session);
        }

        return new SpeechHandlingResult(response, escalationRequested);
    }

    public List<CallSession> getCallHistory(Long clientId) {
        return callSessionRepository.findByClientId(clientId);
    }

    public record SpeechHandlingResult(String response, boolean escalationRequested) {
    }

    private void publishEvent(String eventType, CallSession session) {
        applicationEventPublisher.publishEvent(new CallEvent(
                eventType,
                session.getCallId(),
                session.getClientId(),
                session.getStatus(),
                LocalDateTime.now()));
    }
}
