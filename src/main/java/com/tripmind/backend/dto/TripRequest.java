package com.tripmind.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class TripRequest {

    @NotBlank(message = "Destination cannot be empty")
    private String destination;

    @Min(value = 1, message = "Number of days must be at least 1")
    private long numberOfDays;

    @Min(value = 1, message = "Budget must be greater than zero")
    private long budget;

    @NotBlank(message = "Travel style cannot be empty")
    private String travelStyle;


    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Long getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(long numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public String getTravelStyle() {
        return travelStyle;
    }

    public void setTravelStyle(String travelStyle) {
        this.travelStyle = travelStyle;
    }
}