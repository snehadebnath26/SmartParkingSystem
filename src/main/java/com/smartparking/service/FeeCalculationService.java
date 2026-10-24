
package com.smartparking.service;

import com.smartparking.model.VehicleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class FeeCalculationService {

    public BigDecimal calculateFee(
            VehicleType vehicleType,
            LocalDateTime entryTime,
            LocalDateTime exitTime) {

        if (entryTime == null || exitTime == null || exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Invalid parking timestamps");
        }

        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60);

        BigDecimal hourlyRate = switch (vehicleType) {
            case MOTORCYCLE -> BigDecimal.valueOf(20);
            case CAR -> BigDecimal.valueOf(50);
            case BUS -> BigDecimal.valueOf(100);
        };

        return hourlyRate.multiply(BigDecimal.valueOf(hours));
    }
}
