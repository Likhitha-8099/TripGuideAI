
package com.tripmind.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmind.backend.dto.RouteResponse;
import com.tripmind.backend.service.RoutingService;

@RestController
@RequestMapping("/api/routes")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping
    public RouteResponse getRoute(
            @RequestParam double sourceLatitude,
            @RequestParam double sourceLongitude,
            @RequestParam double destinationLatitude,
            @RequestParam double destinationLongitude) {

        return routingService.getRoute(
                sourceLatitude,
                sourceLongitude,
                destinationLatitude,
                destinationLongitude
        );
    }
}
