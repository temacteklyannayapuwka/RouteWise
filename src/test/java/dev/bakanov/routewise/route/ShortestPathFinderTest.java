package dev.bakanov.routewise.route;

import dev.bakanov.routewise.city.City;
import dev.bakanov.routewise.road.Road;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShortestPathFinderTest {

    private final ShortestPathFinder finder = new ShortestPathFinder();

    @Test
    void findsLowestDistanceAcrossMultipleRoads() {
        City alpha = city();
        City beta = city();
        City gamma = city();
        City delta = city();
        List<Road> roads = List.of(
                new Road(alpha, beta, 5),
                new Road(beta, delta, 5),
                new Road(alpha, gamma, 3),
                new Road(gamma, delta, 2)
        );

        var result = finder.find(roads, alpha.getId(), delta.getId());

        assertThat(result).contains(new RoutePath(List.of(alpha.getId(), gamma.getId(), delta.getId()), 5));
    }

    @Test
    void returnsEmptyWhenDestinationCannotBeReached() {
        City alpha = city();
        City beta = city();

        var result = finder.find(List.of(), alpha.getId(), beta.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void returnsZeroDistanceForSameCity() {
        City alpha = city();

        var result = finder.find(List.of(), alpha.getId(), alpha.getId());

        assertThat(result).contains(new RoutePath(List.of(alpha.getId()), 0));
    }

    @Test
    void respectsRoadDirection() {
        City alpha = city();
        City beta = city();

        var result = finder.find(List.of(new Road(alpha, beta, 7)), beta.getId(), alpha.getId());

        assertThat(result).isEmpty();
    }

    private City city() {
        City city = mock(City.class);
        when(city.getId()).thenReturn(UUID.randomUUID());
        return city;
    }
}
