package dev.bakanov.routewise.city;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cities;

    public CityService(CityRepository cities) {
        this.cities = cities;
    }

    @Transactional
    public CityResponse create(CreateCityRequest request) {
        var city = cities.save(new City(request.name().strip()));
        return CityResponse.from(city);
    }

    @Transactional(readOnly = true)
    public List<CityResponse> findAll() {
        return cities.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(CityResponse::from)
                .toList();
    }
}
