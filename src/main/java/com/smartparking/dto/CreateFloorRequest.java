
package com.smartparking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateFloorRequest(
        @NotNull @PositiveOrZero Integer floorNumber
) {
}
