
package com.smartparking.controller;

import com.smartparking.dto.*;
import com.smartparking.model.*;
import com.smartparking.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/floors")
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingFloor createFloor(
            @Valid @RequestBody CreateFloorRequest request) {
        return parkingService.createFloor(request.floorNumber());
    }

    @PostMapping("/spots")
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingSpot createSpot(
            @Valid @RequestBody CreateSpotRequest request) {
        return parkingService.createSpot(
                request.floorNumber(),
                request.spotNumber(),
                request.spotType());
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingTicket checkIn(
            @Valid @RequestBody CheckInRequest request) {
        return parkingService.checkIn(
                request.registrationNumber(),
                request.vehicleType());
    }

    @PostMapping("/check-out/{ticketId}")
    public ParkingTicket checkOut(@PathVariable Long ticketId) {
        return parkingService.checkOut(ticketId);
    }

    @GetMapping("/spots")
    public List<ParkingSpot> getSpots() {
        return parkingService.getAllSpots();
    }

    @GetMapping("/active-tickets")
    public List<ParkingTicket> getActiveTickets() {
        return parkingService.getActiveTickets();
    }
}
