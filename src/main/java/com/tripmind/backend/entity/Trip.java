
package com.tripmind.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @NotBlank
    @Column(nullable = false)
    private String destination;

    @Positive
    @Column(name = "number_of_days", nullable = false)
    private Long numberOfDays;

    @NotNull
    @Positive
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal budget;

    private String travelStyle;

    @Column(columnDefinition = "TEXT")
    private String generatedItinerary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Trip() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Long getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(Long long1) {
        this.numberOfDays = long1;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(@NotNull @Positive @NotNull @Positive BigDecimal l) {
        this.budget = l;
    }

    public String getTravelStyle() {
        return travelStyle;
    }

    public void setTravelStyle(String travelStyle) {
        this.travelStyle = travelStyle;
    }

    public String getGeneratedItinerary() {
        return generatedItinerary;
    }

    public void setGeneratedItinerary(String generatedItinerary) {
        this.generatedItinerary = generatedItinerary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
