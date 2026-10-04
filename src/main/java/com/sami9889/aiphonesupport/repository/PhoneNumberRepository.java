package com.sami9889.aiphonesupport.repository;

import com.sami9889.aiphonesupport.domain.PhoneNumberAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumberAssignment, Long> {
    Optional<PhoneNumberAssignment> findByNumber(String number);
    Optional<PhoneNumberAssignment> findByNumberAndStatus(String number, String status);
    Optional<PhoneNumberAssignment> findFirstByClientIdAndStatusOrderByAssignedAtAsc(Long clientId, String status);
    List<PhoneNumberAssignment> findByClientId(Long clientId);
    List<PhoneNumberAssignment> findByStatus(String status);
}
