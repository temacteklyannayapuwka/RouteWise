package dev.bakanov.routewise.route;

import dev.bakanov.routewise.city.CityRepository;
import dev.bakanov.routewise.road.RoadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class RouteService {

    private final CityRepository cities;
    private final RoadRepository roads;
    private final ShortestPathFinder pathFinder;

    public RouteService(CityRepository cities, RoadRepository roads, ShortestPathFinder pathFinder) {
        this.cities = cities;
        this.roads = roads;
        this.pathFinder = pathFinder;
    }

    @Transactional(readOnly = true)
    public RouteResponse findShortestPath(UUID fromCityId, UUID toCityId) {
        if (!cities.existsById(fromCityId) || !cities.existsById(toCityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found");
        }
        RoutePath path = pathFinder.find(roads.findAllWithCities(), fromCityId, toCityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No route found"));
        return new RouteResponse(path.cityIds(), path.distanceKm());
    }
}
