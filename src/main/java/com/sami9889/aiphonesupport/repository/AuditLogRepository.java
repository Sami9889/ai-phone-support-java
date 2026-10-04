package com.sami9889.aiphonesupport.repository;

import com.sami9889.aiphonesupport.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
