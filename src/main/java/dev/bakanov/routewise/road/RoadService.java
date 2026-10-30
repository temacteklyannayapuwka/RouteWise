package dev.bakanov.routewise.road;

import dev.bakanov.routewise.city.City;
import dev.bakanov.routewise.city.CityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RoadService {

    private final CityRepository cities;
    private final RoadRepository roads;

    public RoadService(CityRepository cities, RoadRepository roads) {
        this.cities = cities;
        this.roads = roads;
    }

    @Transactional
    public RoadResponse create(CreateRoadRequest request) {
        if (request.fromCityId().equals(request.toCityId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A road must connect two different cities");
        }
        City fromCity = findCity(request.fromCityId());
        City toCity = findCity(request.toCityId());
        if (roads.existsByFromCity_IdAndToCity_Id(request.fromCityId(), request.toCityId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Road already exists");
        }
        return RoadResponse.from(roads.save(new Road(fromCity, toCity, request.distanceKm())));
    }

    @Transactional(readOnly = true)
    public List<RoadResponse> findAll() {
        return roads.findAllWithCities().stream()
                .map(RoadResponse::from)
                .toList();
    }

    private City findCity(UUID id) {
        return cities.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
    }
}
