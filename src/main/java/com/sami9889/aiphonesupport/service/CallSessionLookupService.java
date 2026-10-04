package com.sami9889.aiphonesupport.service;

import com.sami9889.aiphonesupport.domain.CallSession;
import com.sami9889.aiphonesupport.repository.CallSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CallSessionLookupService {

    private final CallSessionRepository callSessionRepository;

    @Cacheable(cacheNames = "call-sessions", key = "#callId")
    public CallSession findByCallId(String callId) {
        return callSessionRepository.findByCallId(callId)
                .orElseThrow(() -> new IllegalArgumentException("Call not found: " + callId));
    }

    @CacheEvict(cacheNames = "call-sessions", key = "#callId")
    public void evict(String callId) {
    }
}