package com.sami9889.aiphonesupport.repository;

import com.sami9889.aiphonesupport.domain.CallSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CallSessionRepository extends JpaRepository<CallSession, Long> {
    Optional<CallSession> findByCallId(String callId);
    List<CallSession> findByClientId(Long clientId);
}
