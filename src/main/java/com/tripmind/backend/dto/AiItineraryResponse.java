
package com.tripmind.backend.dto;

public class AiItineraryResponse {

    private String destination;

    private int numberOfDays;

    private double budget;

    private BudgetBreakdown expenses;

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

    public String getItinerary() {
        return itinerary;
    }

    public void setItinerary(String itinerary) {
        this.itinerary = itinerary;
    }
}
