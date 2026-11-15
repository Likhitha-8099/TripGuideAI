
package com.tripmind.backend.dto;

import java.util.List;

public class AiItineraryResponse {

    private String destination;

    private int numberOfDays;

    private double budget;

    private BudgetBreakdown expenses;

    private double estimatedCost;

    private double remainingBudget;

    private List<DayItinerary> itinerary;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public BudgetBreakdown getExpenses() {
        return expenses;
    }

    public void setExpenses(BudgetBreakdown expenses) {
        this.expenses = expenses;
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

    public List<DayItinerary> getItinerary() {
        return itinerary;
    }

    public void setItinerary(List<DayItinerary> itinerary) {
        this.itinerary = itinerary;
    }
}
