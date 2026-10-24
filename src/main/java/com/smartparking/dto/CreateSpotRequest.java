
package com.smartparking.dto;

import com.smartparking.model.SpotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateSpotRequest(
        @NotNull @PositiveOrZero Integer floorNumber,
        @NotBlank String spotNumber,
        @NotNull SpotType spotType
) {
}
