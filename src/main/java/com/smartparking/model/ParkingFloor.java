package com.smartparking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "parking_floors")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ParkingFloor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer floorNumber;

    public ParkingFloor() {
    }

    public ParkingFloor(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public Long getId() {
        return id;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }
}