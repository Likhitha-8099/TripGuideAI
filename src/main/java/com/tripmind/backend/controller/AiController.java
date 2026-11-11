package com.tripmind.backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tripmind.backend.dto.AiItineraryResponse;
import com.tripmind.backend.dto.TripRequest;
import com.tripmind.backend.service.AiService;

@RestController
public class AiController {
	
	private final AiService aiService;
	
	public AiController(AiService aiService) {
		this.aiService=aiService;
	}
	
	@PostMapping("/api/ai/trip-plan")
	public AiItineraryResponse request(@RequestBody TripRequest tripRequest) {
		return aiService.generateTripResponse(tripRequest);
	}

}
