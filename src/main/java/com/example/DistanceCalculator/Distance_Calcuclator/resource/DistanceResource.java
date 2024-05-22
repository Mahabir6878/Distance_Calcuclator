package com.example.DistanceCalculator.Distance_Calcuclator.resource;

import com.example.DistanceCalculator.Distance_Calcuclator.domain.DistanceDetails;

import com.example.DistanceCalculator.Distance_Calcuclator.entity.DistanceResponseDTO;
import com.example.DistanceCalculator.Distance_Calcuclator.entity.GeocodingCoordinatesEntity;
import com.example.DistanceCalculator.Distance_Calcuclator.service.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/distance")
public class DistanceResource {

    private DistanceService distanceService;

    @Autowired
    public DistanceResource(final DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    @GetMapping("/zipcode")
    public @ResponseBody List<GeocodingCoordinatesEntity> geocodingCoordinatesEntity(DistanceDetails distanceDetails) throws Exception {

        return distanceService.getPinDetails(distanceDetails);
    }

    @GetMapping("/zipcodeDistance")
    public @ResponseBody DistanceResponseDTO getDistanceFromZipCode(DistanceDetails distanceDetails) throws Exception {

        return distanceService.getDistanceResponse(distanceDetails);
    }
}
