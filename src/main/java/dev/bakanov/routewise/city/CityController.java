package dev.bakanov.routewise.city;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cities;

    public CityController(CityService cities) {
        this.cities = cities;
    }

    @PostMapping
    public ResponseEntity<CityResponse> create(@Valid @RequestBody CreateCityRequest request) {
        var city = cities.create(request);
        return ResponseEntity.created(URI.create("/api/cities/" + city.id())).body(city);
    }

    @GetMapping
    public List<CityResponse> findAll() {
        return cities.findAll();
    }
}
