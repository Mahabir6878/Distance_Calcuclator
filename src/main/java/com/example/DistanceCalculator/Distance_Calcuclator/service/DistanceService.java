package com.example.DistanceCalculator.Distance_Calcuclator.service;

import com.example.DistanceCalculator.Distance_Calcuclator.domain.DistanceCoordinates;
import com.example.DistanceCalculator.Distance_Calcuclator.domain.DistanceDetails;
import com.example.DistanceCalculator.Distance_Calcuclator.entity.DistanceResponseDTO;
import com.example.DistanceCalculator.Distance_Calcuclator.entity.GeocodingCoordinatesEntity;
import com.example.DistanceCalculator.Distance_Calcuclator.provider.DistanceProvider;
import com.example.DistanceCalculator.Distance_Calcuclator.provider.GeocodingProvider;
import com.example.DistanceCalculator.Distance_Calcuclator.repository.DistanceMatrixResponseRepository;
import com.example.DistanceCalculator.Distance_Calcuclator.repository.GeoJPARepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DistanceService {

    @Autowired
    DistanceMatrixResponseRepository distanceMatrixResponseRepository;
    @Autowired
    private GeocodingProvider geocodingProvider;
    @Autowired
    private DistanceProvider distanceProvider;

    @Autowired
    private GeoJPARepo geoJPARepo;


    public List<GeocodingCoordinatesEntity> getPinDetails(DistanceDetails distanceDetails) throws Exception {
        List<GeocodingCoordinatesEntity> geocodingCoordinatesEntities = new ArrayList<>();
        List<String> missingZipCodes = new ArrayList<>();

        for (Map.Entry<String, String> dDZip : distanceDetails.getZipMap().entrySet()) {
            String pincode = dDZip.getKey() + "," + dDZip.getValue();
            GeocodingCoordinatesEntity gce = geoJPARepo.findByPincode(pincode);
            if (gce != null) {
                // If the pincode is found in the database, add it to the list
                log.info("Fetched from DB");
                geocodingCoordinatesEntities.add(gce);
            } else {
                // If the pincode is not found in the database, add it to the missing list
                missingZipCodes.add(pincode);
            }
        }

        if (!missingZipCodes.isEmpty()) {
            // Prepare a new DistanceDetails object with only the missing zip codes
            DistanceDetails missingDetails = DistanceDetails.builder()
                    .zipMap(missingZipCodes.stream()
                            .collect(Collectors.toMap(
                                    zip -> zip.split(",")[0],
                                    zip -> zip.split(",")[1]
                            )))
                    .build();

            try {
                // Fetch the missing zip codes from the API
                List<GeocodingCoordinatesEntity> newGeocodingCoordinatesEntities = geocodingProvider.geocodingCoordinatesEntity(missingDetails);

                // Set the pincode for the new entities
                for (GeocodingCoordinatesEntity newGce : newGeocodingCoordinatesEntities) {
                    String pincode = newGce.getPincode();  // Assuming API returns pincode
                    newGce.setPincode(pincode);
                    geocodingCoordinatesEntities.add(newGce);
                }

                // Save the new entities to the database
                geoJPARepo.saveAll(newGeocodingCoordinatesEntities);
            } catch (HttpStatusCodeException e) {
                throw new Exception(e.getMessage());
            }
        }

        return geocodingCoordinatesEntities;
    }

    private DistanceCoordinates mapToDistanceCoordinates(List<GeocodingCoordinatesEntity> gce) {
        DistanceCoordinates distanceCoordinates = new DistanceCoordinates();
        distanceCoordinates.setOriginLatLon(gce.get(0).getLatitude() + "," + gce.get(0).getLongitude());
        distanceCoordinates.setDestinationLatLon(gce.get(1).getLatitude() + "," + gce.get(1).getLongitude());
        return distanceCoordinates;
    }

//    public DistanceMatrixResponseEntity getDistanceResponse(final DistanceDetails distanceDetails) throws Exception {
//        DistanceCoordinates distanceCoordinates = mapToDistanceCoordinates(getPinDetails(distanceDetails));
//        return distanceProvider.getDistance(distanceCoordinates);
//    }
public DistanceResponseDTO getDistanceResponse(DistanceDetails distanceDetails) throws Exception {
    List<GeocodingCoordinatesEntity> geocodingCoordinatesEntities = getPinDetails(distanceDetails);
    DistanceCoordinates distanceCoordinates = mapToDistanceCoordinates(geocodingCoordinatesEntities);

    // Check if the response is already in the database
    DistanceResponseDTO existingResponse = distanceMatrixResponseRepository.findByOriginLatLonAndDestinationLatLon(
            distanceCoordinates.getOriginLatLon(),
            distanceCoordinates.getDestinationLatLon()
    );

    if (existingResponse != null) {
        log.info("Fetched Distance Matrix from DB");
        return existingResponse;
    } else {
        // Fetch from the API
        DistanceResponseDTO newResponse = distanceProvider.getDistance(distanceCoordinates);

        // Save the new response to the database
        distanceMatrixResponseRepository.save(newResponse);

        return newResponse;
    }
}

}
