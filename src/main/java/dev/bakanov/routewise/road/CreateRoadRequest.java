package dev.bakanov.routewise.road;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateRoadRequest(
        @NotNull UUID fromCityId,
        @NotNull UUID toCityId,
        @NotNull @Positive Long distanceKm
) {
}
