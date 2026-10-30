package dev.bakanov.routewise.route;

import dev.bakanov.routewise.city.City;
import dev.bakanov.routewise.city.CityRepository;
import dev.bakanov.routewise.road.Road;
import dev.bakanov.routewise.road.RoadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouteApiTest {

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
    void returnsShortestRouteAndTotalDistance() {
        City alpha = cities.saveAndFlush(new City("Alpha"));
        City beta = cities.saveAndFlush(new City("Beta"));
        City gamma = cities.saveAndFlush(new City("Gamma"));
        City delta = cities.saveAndFlush(new City("Delta"));
        roads.saveAllAndFlush(List.of(
                new Road(alpha, delta, 20),
                new Road(alpha, beta, 5),
                new Road(beta, delta, 5),
                new Road(alpha, gamma, 3),
                new Road(gamma, delta, 4)
        ));

        var response = http.getForEntity(path(alpha.getId(), delta.getId()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("cityIds",
                List.of(alpha.getId().toString(), gamma.getId().toString(), delta.getId().toString()));
        assertThat(response.getBody()).containsEntry("totalDistanceKm", 7);
    }

    @Test
    void unknownCityAndUnreachableDestinationReturnNotFound() {
        City alpha = cities.saveAndFlush(new City("Alpha"));
        City beta = cities.saveAndFlush(new City("Beta"));

        var unknownCity = http.getForEntity(path(UUID.randomUUID(), beta.getId()), Map.class);
        var unreachable = http.getForEntity(path(alpha.getId(), beta.getId()), Map.class);

        assertThat(unknownCity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(unreachable.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void routeToSameCityHasZeroDistance() {
        City alpha = cities.saveAndFlush(new City("Alpha"));

        var response = http.getForEntity(path(alpha.getId(), alpha.getId()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("cityIds", List.of(alpha.getId().toString()));
        assertThat(response.getBody()).containsEntry("totalDistanceKm", 0);
    }

    private String path(UUID fromCityId, UUID toCityId) {
        return UriComponentsBuilder.fromPath("/api/routes/shortest")
                .queryParam("fromCityId", fromCityId)
                .queryParam("toCityId", toCityId)
                .build()
                .toUriString();
    }
}
