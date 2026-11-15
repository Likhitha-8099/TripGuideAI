package com.tripmind.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmind.backend.dto.NearbyPlaceResponse;
import com.tripmind.backend.service.NearbyPlacesService;

@RestController
@RequestMapping("/api/locations/nearby")
public class NearbyPlacesController {

    private final NearbyPlacesService nearbyPlacesService;

    public NearbyPlacesController(
            NearbyPlacesService nearbyPlacesService) {

        this.nearbyPlacesService = nearbyPlacesService;
    }
    

@GetMapping
public List<NearbyPlaceResponse> getNearbyPlaces(
        @RequestParam double latitude,
        @RequestParam double longitude,
        @RequestParam(defaultValue = "10000") int radius,
        @RequestParam(required = false) String category) {

    return nearbyPlacesService.findNearbyPlaces(
            latitude, longitude, radius, category
    );
}

}