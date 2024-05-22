package com.example.DistanceCalculator.Distance_Calcuclator.repository;

import com.example.DistanceCalculator.Distance_Calcuclator.entity.DistanceResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceMatrixResponseRepository extends JpaRepository<DistanceResponseDTO, Long> {
    DistanceResponseDTO findByOriginLatLonAndDestinationLatLon(String originAddresses, String destinationAddresses);
}

