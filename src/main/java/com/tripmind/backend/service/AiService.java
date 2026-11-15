
package com.tripmind.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tripmind.backend.dto.AiItineraryResponse;
import com.tripmind.backend.dto.DayItinerary;
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

                Generate a practical, realistic and personalized travel itinerary
                using the following details:

                Destination: {{destination}}
                Duration: {{numberOfDays}} days
                Total Budget: {{budget}} rupees
                Travel Style: {{travelStyle}}

                Return ONLY valid JSON.
                Do not include Markdown code fences.
                Do not include explanations outside the JSON.

                Follow this exact JSON structure:

                {
                  "destination": "{{destination}}",
                  "numberOfDays": {{numberOfDays}},
                  "budget": {{budget}},
                  "expenses": {
                    "accommodation": 3000,
                    "food": 1500,
                    "transport": 2200,
                    "activities": 4200,
                    "miscellaneous": 600
                  },
                  "estimatedCost": 11500,
                  "remainingBudget": 3500,
                  "itinerary": [
                    {
                      "day": 1,
                      "morning": "Morning activity details",
                      "afternoon": "Afternoon activity details",
                      "evening": "Evening activity details"
                    },
                    {
                      "day": 2,
                      "morning": "Morning activity details",
                      "afternoon": "Afternoon activity details",
                      "evening": "Evening activity details"
                    }
                  ]
                }

                IMPORTANT RULES:

                1. Generate exactly {{numberOfDays}} day objects in the itinerary array.
                2. Day numbering must start from 1 and continue sequentially.
                3. Every day must contain morning, afternoon and evening activities.
                4. Activities must be relevant to the requested destination.
                5. Consider the user's travel style when selecting activities.
                6. Avoid repeating the same activities across different days.
                7. Include all five expense categories.
                8. All expense values must be numeric and non-negative.
                9. estimatedCost must equal the sum of all five expense categories.
                10. estimatedCost must not exceed the user's total budget.
                11. remainingBudget must equal budget minus estimatedCost.
                12. Use realistic approximate costs in Indian Rupees.
                13. Do not claim unverified opening hours, prices or availability
                    as confirmed facts.
                14. The itinerary must be a JSON array, NOT a string.
                15. Do not add extra JSON properties.
                16. Return only valid JSON.
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
                || aiResponse.getItinerary().isEmpty()) {

            throw new IllegalStateException(
                    "AI response does not contain itinerary details"
            );
        }

        // 7. Validate requested number of days

        if (aiResponse.getItinerary().size()
                != tripRequest.getNumberOfDays()) {

            throw new IllegalStateException(
                    "AI generated an incorrect number of itinerary days"
            );
        }

        // 8. Validate each day's activities

        for (int i = 0; i < aiResponse.getItinerary().size(); i++) {

            DayItinerary day = aiResponse.getItinerary().get(i);

            if (day == null) {

                throw new IllegalStateException(
                        "AI generated an invalid day entry"
                );
            }

            if (day.getDay() != i + 1) {

                throw new IllegalStateException(
                        "AI generated incorrect day numbering"
                );
            }

            if (day.getMorning() == null
                    || day.getMorning().isBlank()
                    || day.getAfternoon() == null
                    || day.getAfternoon().isBlank()
                    || day.getEvening() == null
                    || day.getEvening().isBlank()) {

                throw new IllegalStateException(
                        "AI response contains missing daily activities"
                );
            }
        }

        return aiResponse;
    }
}
