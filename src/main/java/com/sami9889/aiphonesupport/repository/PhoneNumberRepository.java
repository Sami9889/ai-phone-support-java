package com.sami9889.aiphonesupport.repository;

import com.sami9889.aiphonesupport.model.PhoneNumberAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumberAssignment, Long> {
    Optional<PhoneNumberAssignment> findByNumber(String number);
    List<PhoneNumberAssignment> findByClientId(Long clientId);
    List<PhoneNumberAssignment> findByStatus(String status);
}
