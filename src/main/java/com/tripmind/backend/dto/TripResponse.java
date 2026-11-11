package com.tripmind.backend.dto;

public class TripResponse {
	private String destination;
    private int numberOfDays;
    private double estimatedCost;
    private double remainingBudget;
    private String itinerary;
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public int getNumberOfDays() {
		return numberOfDays;
	}
	public TripResponse() {
		super();
	}
	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}
	public double getEstimatedCost() {
		return estimatedCost;
	}
	public void setEstimatedCost(double estimatedCost) {
		this.estimatedCost = estimatedCost;
	}
	public double getRemainingBudget() {
		return remainingBudget;
	}
	public void setRemainingBudget(double remainingBudget) {
		this.remainingBudget = remainingBudget;
	}
	public String getItinerary() {
		return itinerary;
	}
	public void setItinerary(String itinerary) {
		this.itinerary = itinerary;
	}

}
