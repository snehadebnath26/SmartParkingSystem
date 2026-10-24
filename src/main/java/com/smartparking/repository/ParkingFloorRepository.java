
package com.smartparking.repository;

import com.smartparking.model.ParkingFloor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {
    Optional<ParkingFloor> findByFloorNumber(Integer floorNumber);
}
