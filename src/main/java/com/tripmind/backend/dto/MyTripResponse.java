
package com.tripmind.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MyTripResponse {

    private Long id;
    private String destination;
    private Long numberOfDays;
    private BigDecimal budget;
    private String travelStyle;
    private String generatedItinerary;
    private LocalDateTime createdAt;

    public MyTripResponse() {
    }

    public MyTripResponse(
            Long id,
            String destination,
            Long numberOfDays,
            BigDecimal budget,
            String travelStyle,
            String generatedItinerary,
            LocalDateTime createdAt) {

        this.id = id;
        this.destination = destination;
        this.numberOfDays = numberOfDays;
        this.budget = budget;
        this.travelStyle = travelStyle;
        this.generatedItinerary = generatedItinerary;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public Long getNumberOfDays() {
        return numberOfDays;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public String getTravelStyle() {
        return travelStyle;
    }

    public String getGeneratedItinerary() {
        return generatedItinerary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
