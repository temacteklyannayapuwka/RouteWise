package dev.bakanov.routewise.road;

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
@RequestMapping("/api/roads")
public class RoadController {

    private final RoadService roads;

    public RoadController(RoadService roads) {
        this.roads = roads;
    }

    @PostMapping
    public ResponseEntity<RoadResponse> create(@Valid @RequestBody CreateRoadRequest request) {
        var road = roads.create(request);
        return ResponseEntity.created(URI.create("/api/roads/" + road.id())).body(road);
    }

    @GetMapping
    public List<RoadResponse> findAll() {
        return roads.findAll();
    }
}
