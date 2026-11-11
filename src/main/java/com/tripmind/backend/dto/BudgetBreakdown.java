
package com.tripmind.backend.dto;

public class BudgetBreakdown {

	private Double accommodation;
	private Double food;
	private Double transport;
	private Double activities;
	private Double miscellaneous;

	public Double getAccommodation() {
		return accommodation;
	}

	public void setAccommodation(Double accommodation) {
		this.accommodation = accommodation;
	}

	public Double getFood() {
		return food;
	}

	public void setFood(Double food) {
		this.food = food;
	}

	public BudgetBreakdown() {
		super();
	}

	public Double getTransport() {
		return transport;
	}

	public void setTransport(Double transport) {
		this.transport = transport;
	}

	public Double getActivities() {
		return activities;
	}

	public void setActivities(Double activities) {
		this.activities = activities;
	}

	public Double getMiscellaneous() {
		return miscellaneous;
	}

	public void setMiscellaneous(Double miscellaneous) {
		this.miscellaneous = miscellaneous;
	}

}
