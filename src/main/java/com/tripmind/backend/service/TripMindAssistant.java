package com.tripmind.backend.service;
import com.tripmind.backend.dto.TripResponse;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface TripMindAssistant {
	
	@SystemMessage("""
	        You are TripMind AI, an expert travel planner.
	        Create practical, realistic and budget-friendly travel itineraries.
	        """)
	@UserMessage("""
	        Plan a trip using the following details:

	        Destination: {{destination}}
	        Duration: {{numberOfDays}} days
	        Budget: {{budget}} rupees
	        Travel Style: {{travelStyle}}

	        Instructions:
	        1. Generate exactly the requested number of days.
	        2. Include accommodation, food, transport and activities.
	        3. Provide an expense breakdown.
	        4. Keep estimated expenses within the given budget.
	        5. Mention assumptions and approximate prices.
	        """)
	TripResponse generateTrip(
	        @V("destination") String destination,
	        @V("numberOfDays") long l,
	        @V("budget") double budget,
	        @V("travelStyle") String travelStyle
	);
}
