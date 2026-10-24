
package com.smartparking.service;

import com.smartparking.model.ParkingSpot;
import com.smartparking.model.ParkingTicket;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;
import com.smartparking.repository.ParkingFloorRepository;
import com.smartparking.repository.ParkingSpotRepository;
import com.smartparking.repository.ParkingTicketRepository;
import com.smartparking.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingFloorRepository floorRepository;

    @Mock
    private ParkingSpotRepository spotRepository;

    @Mock
    private ParkingTicketRepository ticketRepository;

    @Mock
    private FeeCalculationService feeCalculationService;

    @InjectMocks
    private ParkingService parkingService;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setRegistrationNumber("KA01AB1234");
        vehicle.setVehicleType(VehicleType.CAR);
    }

    @Test
    void shouldRejectCheckInWhenVehicleAlreadyParked() {
        ParkingTicket ticket = new ParkingTicket();

        when(vehicleRepository.findByRegistrationNumber("KA01AB1234"))
                .thenReturn(Optional.of(vehicle));

        when(ticketRepository.findByVehicle_RegistrationNumberAndActiveTrue("KA01AB1234"))
                .thenReturn(Optional.of(ticket));

        assertThrows(IllegalStateException.class,
                () -> parkingService.checkIn("KA01AB1234", VehicleType.CAR));

        verify(spotRepository, never())
                .findByOccupiedFalseOrderByFloor_FloorNumberAscSpotNumberAsc();
    }

    @Test
    void shouldRejectCheckInWhenNoCompatibleSpotExists() {
        when(vehicleRepository.findByRegistrationNumber("KA01AB1234"))
                .thenReturn(Optional.of(vehicle));

        when(ticketRepository.findByVehicle_RegistrationNumberAndActiveTrue("KA01AB1234"))
                .thenReturn(Optional.empty());

        when(spotRepository.findByOccupiedFalseOrderByFloor_FloorNumberAscSpotNumberAsc())
                .thenReturn(List.of());

        assertThrows(IllegalStateException.class,
                () -> parkingService.checkIn("KA01AB1234", VehicleType.CAR));
    }

    @Test
    void shouldRejectVehicleTypeMismatch() {
        when(vehicleRepository.findByRegistrationNumber("KA01AB1234"))
                .thenReturn(Optional.of(vehicle));

        assertThrows(IllegalArgumentException.class,
                () -> parkingService.checkIn("KA01AB1234", VehicleType.MOTORCYCLE));
    }
}
