
package com.tripmind.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tripmind.backend.dto.AiItineraryResponse;
import com.tripmind.backend.dto.TripRequest;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import tools.jackson.databind.json.JsonMapper;

class AiServiceTest {

    private ChatModel chatModel;
    private AiService aiService;

    @BeforeEach
    void setUp() {

        chatModel = mock(ChatModel.class);

        JsonMapper objectMapper = JsonMapper.builder().build();

        aiService = new AiService(chatModel, objectMapper);
    }

    // Helper method: Create valid trip request
    private TripRequest createTripRequest() {

        TripRequest request = new TripRequest();

        request.setDestination("Hyderabad");
        request.setNumberOfDays(3);
        request.setBudget(15000);
        request.setTravelStyle("Adventure");

        return request;
    }

    // Helper method: Create AI response
    private ChatResponse createChatResponse(String json) {

        return ChatResponse.builder()
                .aiMessage(AiMessage.from(json))
                .build();
    }

    // TEST 1: Valid JSON should be converted into DTO
    @Test
    void shouldGenerateValidItinerary() {

        String json = """
                {
                  "destination": "Hyderabad",
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
                  "itinerary": "Day 1: Explore Hyderabad"
                }
                """;

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenReturn(createChatResponse(json));

        AiItineraryResponse result =
                aiService.generateTripResponse(createTripRequest());

        assertNotNull(result);
        assertEquals("Hyderabad", result.getDestination());
        assertEquals(3, result.getNumberOfDays());
        assertNotNull(result.getExpenses());
        assertEquals("Day 1: Explore Hyderabad", result.getItinerary());
    }

    // TEST 2: Null AI response
    @Test
    void shouldRejectNullAiResponse() {

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenReturn(null);

        assertThrows(
                IllegalStateException.class,
                () -> aiService.generateTripResponse(createTripRequest())
        );
    }

    // TEST 3: Null AI message
    @Test
    void shouldRejectNullAiMessage() {

        ChatResponse response = mock(ChatResponse.class);

        when(response.aiMessage()).thenReturn(null);

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenReturn(response);

        assertThrows(
                IllegalStateException.class,
                () -> aiService.generateTripResponse(createTripRequest())
        );
    }

    // TEST 4: Empty AI response
    @Test
    void shouldRejectEmptyAiResponse() {

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenReturn(createChatResponse("   "));

        assertThrows(
                IllegalStateException.class,
                () -> aiService.generateTripResponse(createTripRequest())
        );
    }

    // TEST 5: Invalid JSON response
    @Test
    void shouldRejectInvalidJson() {

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenReturn(createChatResponse("This is not valid JSON"));

        assertThrows(
                IllegalStateException.class,
                () -> aiService.generateTripResponse(createTripRequest())
        );
    }

    // TEST 6: Missing expense breakdown
    @Test
    void shouldRejectMissingExpenseBreakdown() {

        String json = """
                {
                  "destination": "Hyderabad",
                  "numberOfDays": 3,
                  "budget": 15000,
                  "itinerary": "Day 1: Explore Hyderabad"
                }
                """;

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenReturn(createChatResponse(json));

        assertThrows(
                IllegalStateException.class,
                () -> aiService.generateTripResponse(createTripRequest())
        );
    }

    // TEST 7: Missing itinerary
    @Test
    void shouldRejectMissingItinerary() {

        String json = """
                {
                  "destination": "Hyderabad",
                  "numberOfDays": 3,
                  "budget": 15000,
                  "expenses": {
                    "accommodation": 3000,
                    "food": 1500,
                    "transport": 2200,
                    "activities": 4200,
                    "miscellaneous": 600
                  }
                }
                """;

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenReturn(createChatResponse(json));

        assertThrows(
                IllegalStateException.class,
                () -> aiService.generateTripResponse(createTripRequest())
        );
    }

    // TEST 8: Groq API failure
    @Test
    void shouldHandleAiApiFailure() {

        when(chatModel.chat(any(ChatMessage[].class)))
                .thenThrow(new RuntimeException("Groq API unavailable"));

        assertThrows(
                IllegalStateException.class,
                () -> aiService.generateTripResponse(createTripRequest())
        );
    }
}
