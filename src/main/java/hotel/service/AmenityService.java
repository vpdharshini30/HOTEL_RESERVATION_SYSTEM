package com.example.hotel.service;

import java.util.List;

import com.example.hotel.entity.Amenity;

public interface AmenityService {

    Amenity addAmenity(Amenity amenity);

    List<Amenity> getAllAmenities();

    Amenity getAmenityById(Long id);

    Amenity updateAmenity(Long id, Amenity amenity);

    void deleteAmenity(Long id);
}
