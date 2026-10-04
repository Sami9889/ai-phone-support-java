package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.domain.CallSession;
import com.sami9889.aiphonesupport.domain.Client;
import com.sami9889.aiphonesupport.domain.PhoneNumberAssignment;
import com.sami9889.aiphonesupport.repository.AuditLogRepository;
import com.sami9889.aiphonesupport.repository.CallSessionRepository;
import com.sami9889.aiphonesupport.repository.ClientRepository;
import com.sami9889.aiphonesupport.repository.PhoneNumberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CallSessionServiceTest {

    @Autowired
    private CallSessionRepository callSessionRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CallSessionService callSessionService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Test
    void handleSpeechPersistsAndAuditsHumanEscalation() {
        CallSession session = new CallSession();
        session.setCallId("call-123");
        session.setClientId(42L);
        session.setFromNumber("+14155550100");
        session.setToNumber("+14155550200");
        session.setDirection("inbound");
        session.setStatus("answered");
        session.setCreatedAt(LocalDateTime.now());
        callSessionRepository.save(session);

        CallSessionService.SpeechHandlingResult result = callSessionService.handleSpeech(
                "call-123", "Please connect me to an agent");

        assertTrue(result.escalationRequested());
        CallSession savedSession = callSessionRepository.findByCallId("call-123").orElseThrow();
        assertEquals("Please connect me to an agent", savedSession.getTranscript());
        assertTrue(savedSession.isEscalationRequested());

        assertEquals(2, auditLogRepository.findAll().size());
        assertTrue(auditLogRepository.findAll().stream()
                .anyMatch(log -> "CALL_ESCALATION_REQUESTED".equals(log.getEventType())));
    }

    @Test
    void initializeOutboundCallUsesClientsActiveAssignedNumber() {
        Client client = new Client();
        client.setClientCode(UUID.randomUUID().toString());
        client.setCompanyName("Example Company");
        client.setContactName("Support Contact");
        client.setEmail(UUID.randomUUID() + "@example.com");
        client.setCountryCode("US");
        client.setUseCase("Customer support");
        client.setStatus("ACTIVE");
        client.setCreatedAt(LocalDateTime.now());
        Client savedClient = clientRepository.save(client);

        PhoneNumberAssignment assignment = new PhoneNumberAssignment();
        assignment.setNumber("+14155550123");
        assignment.setClientId(savedClient.getId());
        assignment.setRegion("US");
        assignment.setStatus("ASSIGNED");
        assignment.setAssignedAt(LocalDateTime.now());
        phoneNumberRepository.save(assignment);

        CallSession session = callSessionService.initializeOutboundCall(savedClient.getId(), "+14155550999");

        assertEquals("+14155550123", session.getFromNumber());
        assertEquals("+14155550999", session.getToNumber());
    }
}