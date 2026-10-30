package dev.bakanov.routewise.route;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routes;

    public RouteController(RouteService routes) {
        this.routes = routes;
    }

    @GetMapping("/shortest")
    public RouteResponse findShortestPath(@RequestParam UUID fromCityId, @RequestParam UUID toCityId) {
        return routes.findShortestPath(fromCityId, toCityId);
    }
}
