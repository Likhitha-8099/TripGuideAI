
package com.tripmind.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tripmind.backend.dto.LocationResponse;
import com.tripmind.backend.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/search")
    public List<LocationResponse> searchPlaces(
            @RequestParam String query,
            @RequestParam(defaultValue = "false") boolean worldwide) {

        return locationService.searchPlaces(query, worldwide);
    }

@GetMapping("/reverse")
public String reverseGeocode(
        @RequestParam double latitude,
        @RequestParam double longitude) {

    return locationService.reverseGeocode(latitude, longitude);
}

}
