
package com.smartparking.service;

import com.smartparking.model.ParkingSpot;
import com.smartparking.model.SpotType;
import com.smartparking.model.VehicleType;
import com.smartparking.repository.ParkingSpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ParkingAllocationService {

    private final ParkingSpotRepository parkingSpotRepository;

    public ParkingAllocationService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @Transactional
    public ParkingSpot allocateSpot(VehicleType vehicleType) {
        List<ParkingSpot> availableSpots = parkingSpotRepository
                .findByOccupiedFalseOrderByFloor_FloorNumberAscSpotNumberAsc();

        for (ParkingSpot candidate : availableSpots.stream()
                .filter(spot -> isCompatible(vehicleType, spot.getSpotType()))
                .sorted(Comparator.comparingInt(
                        spot -> spotSize(spot.getSpotType())))
                .toList()) {

            ParkingSpot lockedSpot = parkingSpotRepository
                    .findByIdForUpdate(candidate.getId())
                    .orElse(null);

            if (lockedSpot == null || lockedSpot.isOccupied()
                    || !isCompatible(vehicleType, lockedSpot.getSpotType())) {
                continue;
            }

            lockedSpot.setOccupied(true);
            return parkingSpotRepository.save(lockedSpot);
        }

        throw new IllegalStateException("No compatible parking spot available");
    }

    private boolean isCompatible(VehicleType vehicleType, SpotType spotType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> true;
            case CAR -> spotType == SpotType.COMPACT || spotType == SpotType.LARGE;
            case BUS -> spotType == SpotType.LARGE;
        };
    }

    private int spotSize(SpotType spotType) {
        return switch (spotType) {
            case MOTORCYCLE -> 1;
            case COMPACT -> 2;
            case LARGE -> 3;
        };
    }
}
