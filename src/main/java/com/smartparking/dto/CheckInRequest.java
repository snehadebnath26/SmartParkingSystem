
package com.smartparking.dto;

import com.smartparking.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotBlank String registrationNumber,
        @NotNull VehicleType vehicleType
) {
}
