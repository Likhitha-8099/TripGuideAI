
package com.tripmind.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tripmind.backend.dto.AiItineraryResponse;
import com.tripmind.backend.dto.TripRequest;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.input.PromptTemplate;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
public class AiService {

    private final ChatModel chatModel;
    private final JsonMapper objectMapper;

    public AiService(
            ChatModel chatModel,
            JsonMapper objectMapper) {

        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    public AiItineraryResponse generateTripResponse(
            TripRequest tripRequest) {

        // 1. Prepare AI prompt
        PromptTemplate template = PromptTemplate.from("""
                You are TripMind AI, an expert travel planner.

                Generate a practical travel itinerary using:

                Destination: {{destination}}
                Duration: {{numberOfDays}} days
                Budget: {{budget}} rupees
                Travel Style: {{travelStyle}}

                Return ONLY valid JSON.
                Do not use Markdown code fences.

                Follow this exact JSON structure:

                {
                  "destination": "Destination name",
                  "numberOfDays": 3,
                  "budget": 15000,
                  "expenses": {
                    "accommodation": 3000,
                    "food": 1500,
                    "transport": 2200,
                    "activities": 4200,
                    "miscellaneous": 600
                  },
                  "estimatedCost": 11500,
                  "remainingBudget": 3500,
                  "itinerary": "Day-wise detailed travel itinerary"
                }

                Rules:
                1. Generate exactly the requested number of days.
                2. Include all five expense categories.
                3. Keep estimated expenses within the given budget.
                4. Use numeric values for all expense amounts.
                5. Provide day-wise activities and approximate costs.
                6. Do not include additional text outside the JSON.
                """);

        // 2. Prepare prompt variables
        Map<String, Object> variables = new HashMap<>();

        variables.put("destination", tripRequest.getDestination());
        variables.put("numberOfDays", tripRequest.getNumberOfDays());
        variables.put("budget", tripRequest.getBudget());
        variables.put("travelStyle", tripRequest.getTravelStyle());

        String finalPrompt = template.apply(variables).text();

        // 3. Call Groq AI
        ChatResponse chatResponse;

        try {

            chatResponse = chatModel.chat(
                    SystemMessage.from(finalPrompt),
                    UserMessage.from(
                            "Generate my travel itinerary in the requested JSON format."
                    )
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Failed to generate itinerary using AI",
                    e
            );
        }

        // 4. Validate AI response
        if (chatResponse == null || chatResponse.aiMessage() == null) {

            throw new IllegalStateException(
                    "AI returned an empty response"
            );
        }

        String jsonResponse = chatResponse.aiMessage().text();

        if (jsonResponse == null || jsonResponse.isBlank()) {

            throw new IllegalStateException(
                    "AI failed to generate the itinerary"
            );
        }

        // 5. Convert JSON into Java DTO
        AiItineraryResponse aiResponse;

        try {

            aiResponse = objectMapper.readValue(
                    jsonResponse,
                    AiItineraryResponse.class
            );

        } catch (JacksonException e) {

            throw new IllegalStateException(
                    "Failed to parse AI itinerary JSON",
                    e
            );
        }

        // 6. Validate parsed response
        if (aiResponse == null) {

            throw new IllegalStateException(
                    "AI returned an invalid itinerary"
            );
        }

        if (aiResponse.getExpenses() == null) {

            throw new IllegalStateException(
                    "AI response does not contain expense breakdown"
            );
        }

        if (aiResponse.getItinerary() == null
                || aiResponse.getItinerary().isBlank()) {

            throw new IllegalStateException(
                    "AI response does not contain itinerary details"
            );
        }

        return aiResponse;
    }
}
