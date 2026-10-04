package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.domain.CallSession;
import com.sami9889.aiphonesupport.domain.Client;
import com.sami9889.aiphonesupport.repository.CallSessionRepository;
import com.sami9889.aiphonesupport.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CallSessionService {

    private final CallSessionRepository callSessionRepository;
    private final ClientRepository clientRepository;

    public CallSession initializeInboundCall(String calledNumber, String callerNumber) {
        Optional<Client> client = clientRepository.findByPhoneNumber(calledNumber);
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

        return callSessionRepository.save(session);
    }

    public CallSession initializeOutboundCall(Long clientId, String toNumber) {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new IllegalArgumentException("Client not found: " + clientId);
        }

        CallSession session = new CallSession();
        session.setCallId(UUID.randomUUID().toString());
        session.setClientId(clientId);
        session.setFromNumber(client.get().getId() != null ? "CUSTOM:" + clientId : "CUSTOM");
        session.setToNumber(toNumber);
        session.setDirection("outbound");
        session.setStatus("initiated");
        session.setCreatedAt(LocalDateTime.now());

        return callSessionRepository.save(session);
    }

    public CallSession updateCallStatus(String callId, String status) {
        Optional<CallSession> optional = callSessionRepository.findByCallId(callId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Call not found: " + callId);
        }

        CallSession session = optional.get();
        session.setStatus(status);
        if ("answered".equals(status)) {
            session.setAnsweredAt(LocalDateTime.now());
        }
        if ("completed".equals(status) || "failed".equals(status)) {
            session.setEndedAt(LocalDateTime.now());
        }

        return callSessionRepository.save(session);
    }

    public CallSession recordTranscript(String callId, String transcript, String aiResponse) {
        Optional<CallSession> optional = callSessionRepository.findByCallId(callId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Call not found: " + callId);
        }

        CallSession session = optional.get();
        session.setTranscript(transcript);
        session.setAiResponse(aiResponse);
        return callSessionRepository.save(session);
    }

    public List<CallSession> getCallHistory(Long clientId) {
        return callSessionRepository.findByClientId(clientId);
    }
}
