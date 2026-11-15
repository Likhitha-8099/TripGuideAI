
package com.tripmind.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.tripmind.backend.dto.LocationResponse;

@Service
public class LocationService {

    private final RestClient restClient;
    private final JsonMapper objectMapper;

    public LocationService(
            RestClient restClient,
            JsonMapper objectMapper) {

        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public List<LocationResponse> searchPlaces(
            String query,
            boolean worldwide) {

        // Validate search query
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be empty"
            );
        }

        // Select search URL based on search mode
        String uriTemplate;

        if (worldwide) {
            uriTemplate =
                    "https://photon.komoot.io/api/?q={query}&limit=5";
        } else {
            uriTemplate =
                    "https://photon.komoot.io/api/?q={query}&limit=5&countrycode=IN";
        }

        // Call Photon API
        String jsonResponse = restClient.get()
                .uri(uriTemplate, query.trim())
                .retrieve()
                .body(String.class);

        try {

            // Parse JSON response
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode features = root.path("features");

            List<LocationResponse> locations = new ArrayList<>();

            // Extract location details
            for (JsonNode feature : features) {

                JsonNode properties = feature.path("properties");

                JsonNode coordinates = feature.path("geometry")
                        .path("coordinates");

                if (!coordinates.isArray() || coordinates.size() < 2) {
                    continue;
                }

                String name = properties.path("name").asText(null);
                String state = properties.path("state").asText(null);
                String country = properties.path("country").asText(null);

                // Photon coordinates: longitude, latitude
                double longitude = coordinates.get(0).asDouble();
                double latitude = coordinates.get(1).asDouble();

                locations.add(new LocationResponse(
                        name,
                        state,
                        country,
                        latitude,
                        longitude
                ));
            }

            return locations;

        } catch (JacksonException e) {

            throw new IllegalStateException(
                    "Failed to parse Photon API response",
                    e
            );
        }
    }

public String reverseGeocode(double latitude, double longitude) {

    if (!Double.isFinite(latitude)
            || latitude < -90 || latitude > 90) {
        throw new IllegalArgumentException("Invalid latitude");
    }

    if (!Double.isFinite(longitude)
            || longitude < -180 || longitude > 180) {
        throw new IllegalArgumentException("Invalid longitude");
    }

    String jsonResponse = restClient.get()
            .uri(
                "https://photon.komoot.io/reverse?lat={lat}&lon={lon}",
                latitude,
                longitude
            )
            .retrieve()
            .body(String.class);

    try {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode features = root.path("features");

        if (!features.isArray() || features.isEmpty()) {
            return "Address not found";
        }

        JsonNode properties = features.get(0).path("properties");

        String name = properties.path("name").asText("");
        String street = properties.path("street").asText("");
        String district = properties.path("district").asText("");
        String city = properties.path("city").asText("");
        String state = properties.path("state").asText("");
        String country = properties.path("country").asText("");

        return java.util.stream.Stream.of(
                    name, street, district, city, state, country
                )
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));

    } catch (JacksonException e) {
        throw new IllegalStateException(
                "Failed to parse Photon reverse response", e
        );
    }
}

}
