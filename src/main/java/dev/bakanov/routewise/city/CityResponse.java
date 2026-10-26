package dev.bakanov.routewise.city;

import java.util.UUID;

public record CityResponse(UUID id, String name) {

    static CityResponse from(City city) {
        return new CityResponse(city.getId(), city.getName());
    }
}
