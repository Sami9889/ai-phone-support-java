package com.sami9889.aiphonesupport.repository;

import com.sami9889.aiphonesupport.model.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientProfile, Long> {
    Optional<ClientProfile> findByPhoneNumber(String phoneNumber);
    Optional<ClientProfile> findByEmail(String email);
}
