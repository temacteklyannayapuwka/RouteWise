package dev.bakanov.routewise.city;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCityRequest(
        @NotBlank @Size(max = 120) String name
) {
}
