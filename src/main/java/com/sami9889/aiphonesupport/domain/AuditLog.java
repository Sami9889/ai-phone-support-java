package com.sami9889.aiphonesupport.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String eventSummary;

    @Column(nullable = false)
    private Long clientId;

    private String callId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
