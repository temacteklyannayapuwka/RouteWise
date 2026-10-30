package dev.bakanov.routewise.road;

import dev.bakanov.routewise.city.City;
import dev.bakanov.routewise.city.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoadApiTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    TestRestTemplate http;

    @Autowired
    CityRepository cities;

    @Autowired
    RoadRepository roads;

    @BeforeEach
    void clearData() {
        roads.deleteAll();
        cities.deleteAll();
    }

    @Test
    void createdRoadIsPersistedAndReturnedInRoadList() {
        City from = cities.saveAndFlush(new City("Alpha"));
        City to = cities.saveAndFlush(new City("Beta"));
        var request = Map.of(
                "fromCityId", from.getId().toString(),
                "toCityId", to.getId().toString(),
                "distanceKm", 120
        );

        var response = http.postForEntity("/api/roads", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("fromCityId", from.getId().toString());
        assertThat(response.getBody()).containsEntry("toCityId", to.getId().toString());
        assertThat(response.getHeaders().getLocation()).hasPath("/api/roads/" + response.getBody().get("id"));
        assertThat(http.getForObject("/api/roads", List.class)).contains(response.getBody());
        assertThat(roads.count()).isEqualTo(1);
    }

    @Test
    void roadWithUnknownCityIsRejected() {
        City city = cities.saveAndFlush(new City("Alpha"));
        var request = Map.of(
                "fromCityId", city.getId().toString(),
                "toCityId", UUID.randomUUID().toString(),
                "distanceKm", 120
        );

        var response = http.postForEntity("/api/roads", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(roads.count()).isZero();
    }

    @Test
    void invalidRoadDistanceAndSameCityAreRejected() {
        City city = cities.saveAndFlush(new City("Alpha"));
        City other = cities.saveAndFlush(new City("Beta"));

        var zeroDistance = http.postForEntity("/api/roads", Map.of(
                "fromCityId", city.getId().toString(),
                "toCityId", other.getId().toString(),
                "distanceKm", 0
        ), Map.class);
        var sameCity = http.postForEntity("/api/roads", Map.of(
                "fromCityId", city.getId().toString(),
                "toCityId", city.getId().toString(),
                "distanceKm", 10
        ), Map.class);

        assertThat(zeroDistance.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(sameCity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(roads.count()).isZero();
    }

    @Test
    void duplicateDirectedRoadReturnsConflict() {
        City from = cities.saveAndFlush(new City("Alpha"));
        City to = cities.saveAndFlush(new City("Beta"));
        var request = Map.of(
                "fromCityId", from.getId().toString(),
                "toCityId", to.getId().toString(),
                "distanceKm", 120
        );

        assertThat(http.postForEntity("/api/roads", request, Map.class).getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(http.postForEntity("/api/roads", request, Map.class).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(roads.count()).isEqualTo(1);
    }
}
