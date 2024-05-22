package com.example.DistanceCalculator.Distance_Calcuclator.domain;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistanceCoordinates {
    private String originLatLon;
    private String destinationLatLon;
}
