
package com.tripmind.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.tripmind.backend.dto.RouteResponse;

@Service
public class RoutingService {

    private final RestClient restClient;
    private final JsonMapper objectMapper;

    public RoutingService(
            RestClient restClient,
            JsonMapper objectMapper) {

        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public RouteResponse getRoute(
            double sourceLatitude,
            double sourceLongitude,
            double destinationLatitude,
            double destinationLongitude) {

        // 1. Validate coordinates
        if (!isValidCoordinates(sourceLatitude, sourceLongitude)
                || !isValidCoordinates(
                        destinationLatitude, destinationLongitude)) {

            throw new IllegalArgumentException(
                    "Invalid source or destination coordinates");
        }

        // 2. Build OSRM URL
        String uri = "https://router.project-osrm.org/route/v1/driving/"
                + sourceLongitude + "," + sourceLatitude
                + ";"
                + destinationLongitude + "," + destinationLatitude
                + "?overview=full&geometries=geojson&steps=false";

        try {

            // 3. Call OSRM API
            String jsonResponse = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            if (jsonResponse == null || jsonResponse.isBlank()) {
                throw new IllegalStateException(
                        "Empty response received from OSRM");
            }

            // 4. Parse JSON
            JsonNode root = objectMapper.readTree(jsonResponse);

            String responseCode = root.path("code").asText("");

            if (!"Ok".equals(responseCode)) {
                throw new IllegalStateException(
                        "OSRM could not find a route. Response: "
                                + responseCode);
            }

            // 5. Validate routes array
            JsonNode routes = root.path("routes");

            if (!routes.isArray() || routes.isEmpty()) {
                throw new IllegalStateException(
                        "No routes found between the locations");
            }

            JsonNode route = routes.get(0);

            // 6. Extract distance and duration
            double distance = route.path("distance").asDouble(-1);
            double duration = route.path("duration").asDouble(-1);

            if (distance < 0 || duration < 0) {
                throw new IllegalStateException(
                        "Invalid distance or duration in OSRM response");
            }

            double distanceInKm = distance / 1000.0;
            double durationInMinutes = duration / 60.0;

            // 7. Extract route coordinates
            JsonNode coordinateNodes = route.path("geometry")
                    .path("coordinates");

            if (!coordinateNodes.isArray()
                    || coordinateNodes.isEmpty()) {
                throw new IllegalStateException(
                        "Route geometry is missing");
            }

            List<List<Double>> coordinates = new ArrayList<>();

            for (JsonNode coordinate : coordinateNodes) {

                if (!coordinate.isArray() || coordinate.size() < 2) {
                    continue;
                }

                double longitude = coordinate.get(0).asDouble();
                double latitude = coordinate.get(1).asDouble();

                List<Double> point = new ArrayList<>();
                point.add(longitude);
                point.add(latitude);

                coordinates.add(point);
            }

            if (coordinates.isEmpty()) {
                throw new IllegalStateException(
                        "No valid route coordinates received");
            }

            // 8. Return response
            return new RouteResponse(
                    distanceInKm,
                    durationInMinutes,
                    coordinates
            );

        } catch (JacksonException e) {

            throw new IllegalStateException(
                    "Failed to parse OSRM response", e);

        } catch (RestClientException e) {

            throw new IllegalStateException(
                    "Unable to communicate with OSRM service", e);
        }
    }

    private boolean isValidCoordinates(
            double latitude,
            double longitude) {

        return Double.isFinite(latitude)
                && Double.isFinite(longitude)
                && latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180;
    }
}
