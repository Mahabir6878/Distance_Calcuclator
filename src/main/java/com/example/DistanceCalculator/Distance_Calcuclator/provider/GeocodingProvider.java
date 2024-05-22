package com.example.DistanceCalculator.Distance_Calcuclator.provider;

import com.example.DistanceCalculator.Distance_Calcuclator.domain.DistanceDetails;
import com.example.DistanceCalculator.Distance_Calcuclator.entity.GeocodingCoordinatesEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeocodingProvider {

    @Value("${api.key}")
    private String apiKey;

    @Value("${geocoding.url}")
    private String geocodingUrl;

    public List<GeocodingCoordinatesEntity> geocodingCoordinatesEntity(final DistanceDetails distanceDetails) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        List<GeocodingCoordinatesEntity> responseList = new ArrayList<>();

        for (Map.Entry<String, String> disMap : distanceDetails.getZipMap().entrySet()) {
            final ResponseEntity<GeocodingCoordinatesEntity> responseEntity;
            HttpEntity<String> requestEntity = new HttpEntity<>(null, null);

            UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(geocodingUrl)
                    .queryParam("zip", disMap.getKey() + "," + disMap.getValue())
                    .queryParam("appid", apiKey).build();

            System.out.println(uriBuilder.toUriString());
            try {
                responseEntity = restTemplate.exchange(uriBuilder.toUriString(),
                        HttpMethod.GET, requestEntity,
                        GeocodingCoordinatesEntity.class);

                GeocodingCoordinatesEntity body = responseEntity.getBody();
                if (body != null) {
                    body.setPincode(disMap.getKey() + "," + disMap.getValue());
                    responseList.add(body);
                }
            } catch (HttpStatusCodeException e) {
                throw new Exception(e.getMessage());
            }
        }
        return responseList;
    }
}
