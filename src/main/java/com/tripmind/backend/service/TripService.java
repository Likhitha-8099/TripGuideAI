
package com.tripmind.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import com.tripmind.backend.dto.AiItineraryResponse;
import com.tripmind.backend.dto.MyTripResponse;
import com.tripmind.backend.dto.TripRequest;
import com.tripmind.backend.entity.Trip;
import com.tripmind.backend.entity.Users;
import com.tripmind.backend.repository.TripRepository;
import com.tripmind.backend.repository.UsersRepository;

@Service
public class TripService {

    private final TripRepository tripRepo;
    private final UsersRepository usersRepo;
    private final AiService aiService;
    private final BudgetValidator budgetValidator;
    private final JsonMapper objectMapper;

    public TripService(
            TripRepository tripRepository,
            UsersRepository usersRepository,
            AiService aiService,
            BudgetValidator budgetValidator,
            JsonMapper objectMapper) {

        this.tripRepo = tripRepository;
        this.usersRepo = usersRepository;
        this.aiService = aiService;
        this.budgetValidator = budgetValidator;
        this.objectMapper = objectMapper;
    }

    // CREATE TRIP + AI ITINERARY
    public String generate(TripRequest tripRequest, Long userId) {

        // 1. Find logged-in user
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // 2. Generate structured itinerary using AI
        AiItineraryResponse aiResponse =
                aiService.generateTripResponse(tripRequest);

        // 3. Validate expenses and calculate actual totals
        AiItineraryResponse validatedResponse =
                budgetValidator.validateAndCalculate(
                        aiResponse,
                        tripRequest.getBudget()
                );

        // 4. Convert validated response into JSON
        String itineraryJson;

        try {
            itineraryJson = objectMapper.writeValueAsString(
                    validatedResponse
            );
        } catch (JacksonException e) {
            throw new IllegalStateException(
                    "Failed to convert itinerary into JSON",
                    e
            );
        }

        // 5. Create Trip entity
        Trip trip = new Trip();

        trip.setUser(user);
        trip.setDestination(tripRequest.getDestination());
        trip.setNumberOfDays(tripRequest.getNumberOfDays());

        trip.setBudget(
                java.math.BigDecimal.valueOf(tripRequest.getBudget())
        );

        trip.setTravelStyle(tripRequest.getTravelStyle());
        trip.setGeneratedItinerary(itineraryJson);

        // 6. Save trip
        Trip savedTrip = tripRepo.save(trip);

        return "Your " + savedTrip.getDestination()
                + " trip is being planned! Trip ID: "
                + savedTrip.getId();
    }

    // GET ALL TRIPS OF LOGGED-IN USER
    public List<MyTripResponse> getMyTrips(Long userId) {

        List<Trip> trips = tripRepo.findByUser_Id(userId);

        return trips.stream()
                .map(trip -> new MyTripResponse(
                        trip.getId(),
                        trip.getDestination(),
                        trip.getNumberOfDays(),
                        trip.getBudget(),
                        trip.getTravelStyle(),
                        trip.getGeneratedItinerary(),
                        trip.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    // GET SINGLE TRIP
    public MyTripResponse getTripById(Long tripId, Long userId) {

        Trip trip = tripRepo.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trip not found"
                ));

        return new MyTripResponse(
                trip.getId(),
                trip.getDestination(),
                trip.getNumberOfDays(),
                trip.getBudget(),
                trip.getTravelStyle(),
                trip.getGeneratedItinerary(),
                trip.getCreatedAt()
        );
    }

    // UPDATE TRIP
    public MyTripResponse updateTrip(
            Long tripId,
            Long userId,
            TripRequest request) {

        Trip trip = tripRepo.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trip not found"
                ));

        trip.setDestination(request.getDestination());
        trip.setNumberOfDays(request.getNumberOfDays());

        trip.setBudget(
                java.math.BigDecimal.valueOf(request.getBudget())
        );

        trip.setTravelStyle(request.getTravelStyle());

        Trip updatedTrip = tripRepo.save(trip);

        return new MyTripResponse(
                updatedTrip.getId(),
                updatedTrip.getDestination(),
                updatedTrip.getNumberOfDays(),
                updatedTrip.getBudget(),
                updatedTrip.getTravelStyle(),
                updatedTrip.getGeneratedItinerary(),
                updatedTrip.getCreatedAt()
        );
    }

    // DELETE TRIP
    public void deleteTrip(Long tripId, Long userId) {

        Trip trip = tripRepo.findByIdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trip not found"
                ));

        tripRepo.delete(trip);
    }
}
