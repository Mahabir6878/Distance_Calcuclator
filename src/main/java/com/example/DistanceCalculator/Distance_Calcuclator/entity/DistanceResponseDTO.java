package com.example.DistanceCalculator.Distance_Calcuclator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DistanceResponseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String originAddress;
    private String destinationAddress;
    private String originLatLon;
    private String destinationLatLon;
    private String distance;
    private String duration;

    public DistanceResponseDTO(String originAddress, String destinationAddress, String originLatLon, String destinationLatLon, String distance, String duration) {
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
        this.originLatLon = originLatLon;
        this.destinationLatLon = destinationLatLon;
        this.distance = distance;
        this.duration = duration;
    }
}


