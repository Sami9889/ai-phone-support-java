package com.sami9889.aiphonesupport.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "call_sessions")
@Getter
@Setter
public class CallSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String callId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String fromNumber;

    @Column(nullable = false)
    private String toNumber;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Column(columnDefinition = "TEXT")
    private String aiResponse;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime answeredAt;
    private LocalDateTime endedAt;
}
