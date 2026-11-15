package com.tripmind.backend.dto;

public class NearbyPlaceResponse {
	
	private String name;
	
	private String category;
	
	private double latitude;
	
	private double longitude;
	
	private String address;
	
	private double distanceInMeters;

	public NearbyPlaceResponse() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getDistanceInMeters() {
		return distanceInMeters;
	}

	public void setDistanceInMeters(double distanceInMeters) {
		this.distanceInMeters = distanceInMeters;
	}

}
