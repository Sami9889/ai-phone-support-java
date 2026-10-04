package com.sami9889.aiphonesupport.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_profiles")
@Getter
@Setter
public class ClientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String clientCode;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String contactName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String useCase;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String telnyxStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
