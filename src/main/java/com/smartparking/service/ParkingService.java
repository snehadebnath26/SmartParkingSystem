
package com.smartparking.service;

import com.smartparking.model.*;
import com.smartparking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ParkingService {

    private final VehicleRepository vehicleRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSpotRepository spotRepository;
    private final ParkingTicketRepository ticketRepository;
    private final FeeCalculationService feeCalculationService;

    public ParkingService(
            VehicleRepository vehicleRepository,
            ParkingFloorRepository floorRepository,
            ParkingSpotRepository spotRepository,
            ParkingTicketRepository ticketRepository,
            FeeCalculationService feeCalculationService) {
        this.vehicleRepository = vehicleRepository;
        this.floorRepository = floorRepository;
        this.spotRepository = spotRepository;
        this.ticketRepository = ticketRepository;
        this.feeCalculationService = feeCalculationService;
    }

    @Transactional
    public ParkingTicket checkIn(String registrationNumber, VehicleType vehicleType) {
        String registration = registrationNumber.trim().toUpperCase();

        if (vehicleRepository.findByRegistrationNumber(registration).isEmpty()) {
            vehicleRepository.saveAndFlush(new Vehicle(registration, vehicleType));
        } else {
            Vehicle existing = vehicleRepository.findByRegistrationNumber(registration)
                    .orElseThrow();

            if (existing.getVehicleType() != vehicleType) {
                throw new IllegalArgumentException(
                        "Vehicle registration already exists with a different type");
            }
        }

        if (ticketRepository
                .findByVehicle_RegistrationNumberAndActiveTrue(registration)
                .isPresent()) {
            throw new IllegalStateException("Vehicle is already parked");
        }

        Vehicle vehicle = vehicleRepository.findByRegistrationNumber(registration)
                .orElseThrow();

        List<ParkingSpot> availableSpots = spotRepository
                .findByOccupiedFalseOrderByFloor_FloorNumberAscSpotNumberAsc();

        ParkingSpot selectedSpot = availableSpots.stream()
                .filter(spot -> isCompatible(vehicleType, spot.getSpotType()))
                .min(Comparator.comparingInt(spot -> spotSize(spot.getSpotType())))
                .orElseThrow(() -> new IllegalStateException(
                        "No compatible parking spot available"));

        ParkingSpot spot = spotRepository.findByIdForUpdate(selectedSpot.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Parking spot no longer exists"));

        if (spot.isOccupied()) {
            throw new IllegalStateException(
                    "Selected spot was just occupied. Please retry.");
        }

        spot.setOccupied(true);
        spotRepository.save(spot);

        ParkingTicket ticket = new ParkingTicket(
                vehicle, spot, LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    @Transactional
    public ParkingTicket checkOut(Long ticketId) {
        ParkingTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Parking ticket not found"));

        if (!ticket.isActive()) {
            throw new IllegalStateException("Ticket has already been closed");
        }

        ParkingSpot spot = spotRepository.findByIdForUpdate(
                        ticket.getSpot().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Parking spot not found"));

        LocalDateTime exitTime = LocalDateTime.now();

        ticket.setExitTime(exitTime);
        ticket.setFee(feeCalculationService.calculateFee(
                ticket.getVehicle().getVehicleType(),
                ticket.getEntryTime(),
                exitTime));
        ticket.setActive(false);

        spot.setOccupied(false);
        spotRepository.save(spot);

        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<ParkingSpot> getAllSpots() {
        return spotRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ParkingTicket> getActiveTickets() {
        return ticketRepository.findByActiveTrue();
    }

    @Transactional
    public ParkingFloor createFloor(Integer floorNumber) {
        if (floorRepository.findByFloorNumber(floorNumber).isPresent()) {
            throw new IllegalArgumentException("Floor number already exists");
        }

        return floorRepository.save(new ParkingFloor(floorNumber));
    }

    @Transactional
    public ParkingSpot createSpot(
            Integer floorNumber, String spotNumber, SpotType spotType) {

        ParkingFloor floor = floorRepository.findByFloorNumber(floorNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Floor not found"));

        boolean duplicate = spotRepository.findAll().stream()
                .anyMatch(spot -> spot.getFloor().getId().equals(floor.getId())
                        && spot.getSpotNumber().equalsIgnoreCase(spotNumber));

        if (duplicate) {
            throw new IllegalArgumentException(
                    "Spot number already exists on this floor");
        }

        return spotRepository.save(
                new ParkingSpot(spotNumber, spotType, floor));
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
