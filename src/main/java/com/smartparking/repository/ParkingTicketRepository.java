
package com.smartparking.repository;

import com.smartparking.model.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {
    Optional<ParkingTicket> findByVehicle_RegistrationNumberAndActiveTrue(
            String registrationNumber);

    List<ParkingTicket> findByActiveTrue();
}
