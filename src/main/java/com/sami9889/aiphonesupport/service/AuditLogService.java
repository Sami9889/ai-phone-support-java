package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.domain.AuditLog;
import com.sami9889.aiphonesupport.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void record(String eventType, String eventSummary, Long clientId, String callId) {
        AuditLog entry = new AuditLog();
        entry.setEventType(eventType);
        entry.setEventSummary(eventSummary);
        entry.setClientId(clientId);
        entry.setCallId(callId);
        entry.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(entry);
    }
}
