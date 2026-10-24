package com.smartparking.service;

import com.smartparking.model.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeeCalculationServiceTest {

    private FeeCalculationService feeCalculationService;
    private LocalDateTime entryTime;

    @BeforeEach
    void setUp() {
        feeCalculationService = new FeeCalculationService();
        entryTime = LocalDateTime.of(2026, 10, 24, 10, 0);
    }

    @Test
    void shouldCalculateMotorcycleFee() {
        BigDecimal fee = feeCalculationService.calculateFee(
                VehicleType.MOTORCYCLE,
                entryTime,
                entryTime.plusMinutes(30)
        );

        assertEquals(BigDecimal.valueOf(20), fee);
    }

    @Test
    void shouldCalculateCarFee() {
        BigDecimal fee = feeCalculationService.calculateFee(
                VehicleType.CAR,
                entryTime,
                entryTime.plusMinutes(30)
        );

        assertEquals(BigDecimal.valueOf(50), fee);
    }

    @Test
    void shouldCalculateBusFee() {
        BigDecimal fee = feeCalculationService.calculateFee(
                VehicleType.BUS,
                entryTime,
                entryTime.plusMinutes(30)
        );

        assertEquals(BigDecimal.valueOf(100), fee);
    }

    @Test
    void shouldRoundUpToNextStartedHour() {
        BigDecimal fee = feeCalculationService.calculateFee(
                VehicleType.CAR,
                entryTime,
                entryTime.plusMinutes(61)
        );

        assertEquals(BigDecimal.valueOf(100), fee);
    }

    @Test
    void shouldChargeForAtLeastOneHour() {
        BigDecimal fee = feeCalculationService.calculateFee(
                VehicleType.CAR,
                entryTime,
                entryTime
        );

        assertEquals(BigDecimal.valueOf(50), fee);
    }

    @Test
    void shouldRejectNullEntryTime() {
        assertThrows(IllegalArgumentException.class, () ->
                feeCalculationService.calculateFee(
                        VehicleType.CAR,
                        null,
                        entryTime
                )
        );
    }

    @Test
    void shouldRejectNullExitTime() {
        assertThrows(IllegalArgumentException.class, () ->
                feeCalculationService.calculateFee(
                        VehicleType.CAR,
                        entryTime,
                        null
                )
        );
    }

    @Test
    void shouldRejectExitTimeBeforeEntryTime() {
        assertThrows(IllegalArgumentException.class, () ->
                feeCalculationService.calculateFee(
                        VehicleType.CAR,
                        entryTime,
                        entryTime.minusMinutes(1)
                )
        );
    }
}