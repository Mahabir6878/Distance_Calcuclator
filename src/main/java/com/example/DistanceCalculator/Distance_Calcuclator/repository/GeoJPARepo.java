package com.example.DistanceCalculator.Distance_Calcuclator.repository;


import com.example.DistanceCalculator.Distance_Calcuclator.entity.GeocodingCoordinatesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface GeoJPARepo extends JpaRepository<GeocodingCoordinatesEntity,Long> {
    public GeocodingCoordinatesEntity findByPincode(String pin);
}