package dev.bakanov.routewise.road;

import java.util.UUID;

public record RoadResponse(UUID id, UUID fromCityId, UUID toCityId, long distanceKm) {

    static RoadResponse from(Road road) {
        return new RoadResponse(road.getId(), road.getFromCity().getId(),
                road.getToCity().getId(), road.getDistanceKm());
    }
}
