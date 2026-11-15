
package com.tripmind.backend.dto;

public class TripResponse {

    private Long tripId;

    private AiItineraryResponse itinerary;

    public TripResponse() {
    }

    public TripResponse(
            Long tripId,
            AiItineraryResponse itinerary) {
        this.tripId = tripId;
        this.itinerary = itinerary;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public AiItineraryResponse getItinerary() {
        return itinerary;
    }

    public void setItinerary(AiItineraryResponse itinerary) {
        this.itinerary = itinerary;
    }

	public double getEstimatedCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setRemainingBudget(double remainingBudget) {
		// TODO Auto-generated method stub
		
	}
}
