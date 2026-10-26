package dev.bakanov.routewise;

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

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouteWiseApplicationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    TestRestTemplate http;

    @Autowired
    CityRepository cities;

    @BeforeEach
    void clearCities() {
        cities.deleteAll();
    }

    @Test
    void healthEndpointReportsApplicationAsUp() {
        var response = http.getForEntity("/actuator/health", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "UP");
    }

    @Test
    void createdCityIsPersistedAndReturnedInCityList() {
        var response = http.postForEntity("/api/cities", Map.of("name", " Saint Petersburg "), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("name", "Saint Petersburg");
        assertThat(response.getHeaders().getLocation()).hasPath("/api/cities/" + response.getBody().get("id"));

        var cityList = http.getForObject("/api/cities", List.class);

        assertThat(cityList).contains(response.getBody());
        assertThat(cities.count()).isEqualTo(1);
    }

    @Test
    void invalidCityNameIsRejected() {
        var response = http.postForEntity("/api/cities", Map.of("name", "   "), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(cities.count()).isZero();
    }
}
