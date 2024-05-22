package com.example.DistanceCalculator.Distance_Calcuclator.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@Setter
public class DistanceDetails {
    private Map<String,String> zipMap;
}
