package dev.bakanov.routewise.route;

import java.util.List;
import java.util.UUID;

public record RoutePath(List<UUID> cityIds, long distanceKm) {

    public RoutePath {
        cityIds = List.copyOf(cityIds);
    }
}
