package dev.bakanov.routewise.route;

import java.util.List;
import java.util.UUID;

public record RouteResponse(List<UUID> cityIds, long totalDistanceKm) {

    public RouteResponse {
        cityIds = List.copyOf(cityIds);
    }
}
