package com.example.DistanceCalculator.Distance_Calcuclator.provider;

import com.example.DistanceCalculator.Distance_Calcuclator.domain.DistanceCoordinates;
import com.example.DistanceCalculator.Distance_Calcuclator.entity.DistanceResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
public class DistanceProvider {

    @Value("${distance.url}")
    private String distanceUrl;

    @Value("${matrix.key}")
    private String matrixKey;

    public DistanceResponseDTO getDistance(final DistanceCoordinates distanceCoordinates) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(null, null);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(distanceUrl)
                .queryParam("origins", distanceCoordinates.getOriginLatLon())
                .queryParam("destinations", distanceCoordinates.getDestinationLatLon())
                .queryParam("key", matrixKey)
                .build();

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            String jsonResponse = responseEntity.getBody();
            return mapJsonToDistanceInfo(jsonResponse);
        } catch (HttpStatusCodeException e) {
            throw new Exception(e.getMessage());
        }
    }

    private DistanceResponseDTO mapJsonToDistanceInfo(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);

        // Extracting required fields from JSON response
        String originAddress = rootNode.path("origin_addresses").get(0).asText();
        String destinationAddress = rootNode.path("destination_addresses").get(0).asText();
        String originLatLon = rootNode.path("rows").get(0).path("elements").get(0).path("origin").asText();
        String destinationLatLon = rootNode.path("rows").get(0).path("elements").get(0).path("destination").asText();
        String distance = rootNode.path("rows").get(0).path("elements").get(0).path("distance").path("text").asText();
        String duration = rootNode.path("rows").get(0).path("elements").get(0).path("duration").path("text").asText();

        // Constructing DistanceInfo object
        return new DistanceResponseDTO(originAddress, destinationAddress, originLatLon, destinationLatLon, distance, duration);
    }
}
