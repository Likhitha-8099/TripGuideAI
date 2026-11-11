
package com.tripmind.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tripmind.backend.dto.MyTripResponse;
import com.tripmind.backend.dto.TripRequest;
import com.tripmind.backend.service.TripService;

import jakarta.validation.Valid;

@RestController
public class TripController {

	private final TripService tripService;

	public TripController(TripService tripService) {
		this.tripService = tripService;
	}

	@GetMapping("/api/trips/hello")
	public String hello() {
		return "Welcome to TripMind AI!";
	}

	@PostMapping("/api/trips/generate")
	public ResponseEntity<String> generate(@Valid @RequestBody TripRequest tripRequest, Authentication authentication) {

		// Get logged-in user's ID from JWT authentication
		Long userId = (Long) authentication.getPrincipal();

		// Generate trip for this particular user
		String response = tripService.generate(tripRequest, userId);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/api/trips/my-trips")
	public ResponseEntity<List<MyTripResponse>> getMyTrips(Authentication authentication) {

		// Get logged-in user's ID from JWT
		Long userId = (Long) authentication.getPrincipal();

		// Fetch only this user's trips
		List<MyTripResponse> trips = tripService.getMyTrips(userId);

		return ResponseEntity.ok(trips);
	}

	@GetMapping("/api/trips/{id}")
	public ResponseEntity<MyTripResponse> getTripById(@PathVariable Long id, Authentication authentication) {

		// Get logged-in user's ID from JWT
		Long userId = (Long) authentication.getPrincipal();

		// Fetch trip only if it belongs to this user
		MyTripResponse trip = tripService.getTripById(id, userId);

		return ResponseEntity.ok(trip);
	}

	@PutMapping("/api/trips/{id}")
	public ResponseEntity<MyTripResponse> updateTrip(@PathVariable Long id, @Valid @RequestBody TripRequest tripRequest,
			Authentication authentication) {

		Long userId = (Long) authentication.getPrincipal();

		MyTripResponse updatedTrip = tripService.updateTrip(id, userId, tripRequest);

		return ResponseEntity.ok(updatedTrip);
	}

	@DeleteMapping("/api/trips/{id}")
	public ResponseEntity<String> deleteTrip(@PathVariable Long id, Authentication authentication) {

		Long userId = (Long) authentication.getPrincipal();

		tripService.deleteTrip(id, userId);

		return ResponseEntity.ok("Trip deleted successfully");
	}

}
