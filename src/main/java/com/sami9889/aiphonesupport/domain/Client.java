package com.sami9889.aiphonesupport.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client {

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
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
