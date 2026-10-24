
package com.smartparking.repository;

import com.smartparking.model.ParkingSpot;
import com.smartparking.model.SpotType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    List<ParkingSpot> findByOccupiedFalseOrderByFloor_FloorNumberAscSpotNumberAsc();

    List<ParkingSpot> findByOccupiedFalseAndSpotTypeOrderByFloor_FloorNumberAscSpotNumberAsc(
            SpotType spotType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSpot s WHERE s.id = :id")
    Optional<ParkingSpot> findByIdForUpdate(@Param("id") Long id);
}
