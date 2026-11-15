
package com.tripmind.backend.dto;

import java.util.List;

public class RouteResponse {

    private double distanceInKm;
    private double durationInMinutes;
    private List<List<Double>> coordinates;

    public RouteResponse() {
    }

    public RouteResponse(
            double distanceInKm,
            double durationInMinutes,
            List<List<Double>> coordinates) {

        this.distanceInKm = distanceInKm;
        this.durationInMinutes = durationInMinutes;
        this.coordinates = coordinates;
    }

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public double getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(double durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}
