package dev.bakanov.routewise.route;

import dev.bakanov.routewise.road.Road;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;

public class ShortestPathFinder {

    public Optional<RoutePath> find(List<Road> roads, UUID fromCityId, UUID toCityId) {
        if (fromCityId.equals(toCityId)) {
            return Optional.of(new RoutePath(List.of(fromCityId), 0));
        }

        Map<UUID, List<Edge>> graph = new HashMap<>();
        for (Road road : roads) {
            graph.computeIfAbsent(road.getFromCity().getId(), ignored -> new ArrayList<>())
                    .add(new Edge(road.getToCity().getId(), road.getDistanceKm()));
        }

        Map<UUID, Long> distances = new HashMap<>();
        Map<UUID, UUID> previousCities = new HashMap<>();
        PriorityQueue<QueueEntry> queue = new PriorityQueue<>(Comparator.comparingLong(QueueEntry::distanceKm));
        distances.put(fromCityId, 0L);
        queue.add(new QueueEntry(fromCityId, 0));

        while (!queue.isEmpty()) {
            QueueEntry current = queue.poll();
            if (current.distanceKm() != distances.get(current.cityId())) {
                continue;
            }
            if (current.cityId().equals(toCityId)) {
                break;
            }

            for (Edge edge : graph.getOrDefault(current.cityId(), List.of())) {
                long candidateDistance = current.distanceKm() + edge.distanceKm();
                if (candidateDistance < distances.getOrDefault(edge.toCityId(), Long.MAX_VALUE)) {
                    distances.put(edge.toCityId(), candidateDistance);
                    previousCities.put(edge.toCityId(), current.cityId());
                    queue.add(new QueueEntry(edge.toCityId(), candidateDistance));
                }
            }
        }

        Long totalDistance = distances.get(toCityId);
        if (totalDistance == null) {
            return Optional.empty();
        }

        List<UUID> cityIds = new ArrayList<>();
        UUID currentCityId = toCityId;
        while (currentCityId != null) {
            cityIds.add(currentCityId);
            currentCityId = previousCities.get(currentCityId);
        }
        Collections.reverse(cityIds);
        return Optional.of(new RoutePath(cityIds, totalDistance));
    }

    private record Edge(UUID toCityId, long distanceKm) {
    }

    private record QueueEntry(UUID cityId, long distanceKm) {
    }
}
