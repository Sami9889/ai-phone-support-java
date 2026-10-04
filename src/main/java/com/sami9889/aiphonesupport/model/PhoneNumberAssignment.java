package com.sami9889.aiphonesupport.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "phone_numbers")
@Getter
@Setter
public class PhoneNumberAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String status; // ASSIGNED, INACTIVE, RELEASED

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    private LocalDateTime releasedAt;
}
