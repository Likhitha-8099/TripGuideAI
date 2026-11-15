
package com.tripmind.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.tripmind.backend.dto.NearbyPlaceResponse;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
public class NearbyPlacesService {

    private final RestClient restClient;
    private final JsonMapper jsonMapper;

    public NearbyPlacesService(
            RestClient restClient,
            JsonMapper jsonMapper) {
        this.restClient = restClient;
        this.jsonMapper = jsonMapper;
    }

    public List<NearbyPlaceResponse> findNearbyPlaces(
            double latitude,
            double longitude,
            int radius,
            String category) {

        // 1. Validate latitude
        if (!Double.isFinite(latitude)
                || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        // 2. Validate longitude
        if (!Double.isFinite(longitude)
                || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        // 3. Validate radius
        if (radius < 100 || radius > 50000) {
            throw new IllegalArgumentException(
                    "Radius must be between 100 and 50000 meters");
        }

        // 4. Normalize and validate category
        String selectedCategory = category == null
                ? null
                : category.trim().toLowerCase(Locale.ROOT);

        List<String> allowedCategories = List.of(
                "attraction",
                "museum",
                "viewpoint",
                "park",
                "monument",
                "castle"
        );

        if (selectedCategory != null
                && !allowedCategories.contains(selectedCategory)) {
            throw new IllegalArgumentException("Invalid category");
        }

        // 5. Build dynamic Overpass query
        String query = buildOverpassQuery(
                latitude,
                longitude,
                radius,
                selectedCategory
        );

        // 6. Prepare form data
        MultiValueMap<String, String> formData =
                new LinkedMultiValueMap<>();

        formData.add("data", query);

        // 7. Call Overpass API
        String jsonResponse = restClient.post()
                .uri("https://overpass-api.de/api/interpreter")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(String.class);

        // 8. Parse response
        try {
            JsonNode root = jsonMapper.readTree(jsonResponse);
            JsonNode elements = root.path("elements");

            List<NearbyPlaceResponse> places = new ArrayList<>();
            Set<String> seenPlaces = new HashSet<>();

            for (JsonNode element : elements) {

                JsonNode tags = element.path("tags");

                // Skip unnamed places
                String name = tags.path("name").asText("").trim();

                if (name.isBlank()) {
                    continue;
                }

                // 9. Extract coordinates
                double placeLatitude;
                double placeLongitude;

                String type = element.path("type").asText();

                if ("node".equals(type)) {

                    if (!element.path("lat").isNumber()
                            || !element.path("lon").isNumber()) {
                        continue;
                    }

                    placeLatitude = element.path("lat").asDouble();
                    placeLongitude = element.path("lon").asDouble();

                } else if ("way".equals(type)) {

                    JsonNode center = element.path("center");

                    if (!center.path("lat").isNumber()
                            || !center.path("lon").isNumber()) {
                        continue;
                    }

                    placeLatitude = center.path("lat").asDouble();
                    placeLongitude = center.path("lon").asDouble();

                } else {
                    continue;
                }

                // 10. Calculate distance
                double distance = calculateDistance(
                        latitude,
                        longitude,
                        placeLatitude,
                        placeLongitude
                );

                // Exclude results outside requested radius
                if (distance > radius) {
                    continue;
                }

                // 11. Remove approximate duplicates
                String uniqueKey = name.toLowerCase(Locale.ROOT)
                        + "|" + Math.round(placeLatitude * 100000)
                        + "|" + Math.round(placeLongitude * 100000);

                if (!seenPlaces.add(uniqueKey)) {
                    continue;
                }

                // 12. Determine category
                String placeCategory = getPlaceCategory(tags);

                // 13. Extract address
                String address = tags.path("addr:full").asText("");

                if (address.isBlank()) {
                    String street = tags.path("addr:street").asText("");
                    String city = tags.path("addr:city").asText("");

                    address = (street + ", " + city)
                            .replaceAll("^, |, $", "");
                }

                // 14. Create response DTO
                NearbyPlaceResponse place = new NearbyPlaceResponse();

                place.setName(name);
                place.setCategory(placeCategory);
                place.setLatitude(placeLatitude);
                place.setLongitude(placeLongitude);
                place.setAddress(address);
                place.setDistanceInMeters(distance);

                places.add(place);
            }

            // 15. Sort nearest places first
            places.sort(Comparator.comparingDouble(
                    NearbyPlaceResponse::getDistanceInMeters
            ));

            return places;

        } catch (JacksonException e) {
            throw new IllegalStateException(
                    "Failed to parse Overpass API response", e);
        }
    }

    // Build query based on selected category
    private String buildOverpassQuery(
            double latitude,
            double longitude,
            int radius,
            String category) {

        String[] filters;

        if (category == null) {

            filters = new String[] {
                    "node[\"tourism\"~\"^(attraction|museum|viewpoint)$\"]",
                    "way[\"tourism\"~\"^(attraction|museum|viewpoint)$\"]",
                    "node[\"leisure\"=\"park\"]",
                    "way[\"leisure\"=\"park\"]",
                    "node[\"historic\"~\"^(monument|castle)$\"]",
                    "way[\"historic\"~\"^(monument|castle)$\"]"
            };

        } else {

            switch (category) {
                case "attraction" ->
                    filters = new String[] {
                            "node[\"tourism\"=\"attraction\"]",
                            "way[\"tourism\"=\"attraction\"]"
                    };

                case "museum" ->
                    filters = new String[] {
                            "node[\"tourism\"=\"museum\"]",
                            "way[\"tourism\"=\"museum\"]"
                    };

                case "viewpoint" ->
                    filters = new String[] {
                            "node[\"tourism\"=\"viewpoint\"]",
                            "way[\"tourism\"=\"viewpoint\"]"
                    };

                case "park" ->
                    filters = new String[] {
                            "node[\"leisure\"=\"park\"]",
                            "way[\"leisure\"=\"park\"]"
                    };

                case "monument" ->
                    filters = new String[] {
                            "node[\"historic\"=\"monument\"]",
                            "way[\"historic\"=\"monument\"]"
                    };

                case "castle" ->
                    filters = new String[] {
                            "node[\"historic\"=\"castle\"]",
                            "way[\"historic\"=\"castle\"]"
                    };

                default -> throw new IllegalArgumentException(
                        "Invalid category");
            }
        }

        StringBuilder query = new StringBuilder();
        query.append("[out:json][timeout:25];\n(\n");

        for (String filter : filters) {
            query.append(filter)
                    .append("(around:")
                    .append(radius).append(",")
                    .append(latitude).append(",")
                    .append(longitude)
                    .append(");\n");
        }

        query.append(");\nout center tags;");

        return query.toString();
    }

    // Determine actual category from OSM tags
    private String getPlaceCategory(JsonNode tags) {

        String tourism = tags.path("tourism").asText("");

        if (!tourism.isBlank()) {
            return tourism;
        }

        String leisure = tags.path("leisure").asText("");

        if (!leisure.isBlank()) {
            return leisure;
        }

        String historic = tags.path("historic").asText("");

        if (!historic.isBlank()) {
            return historic;
        }

        return "other";
    }

    // Haversine distance calculation
    private double calculateDistance(
            double userLat,
            double userLon,
            double placeLat,
            double placeLon) {

        double latDistance = Math.toRadians(placeLat - userLat);
        double lonDistance = Math.toRadians(placeLon - userLon);

        double a = Math.sin(latDistance / 2)
                * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat))
                * Math.cos(Math.toRadians(placeLat))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return 6_371_000 * c;
    }
}
