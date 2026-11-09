package com.example.hotel.service_impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hotel.entity.Amenity;
import com.example.hotel.repository.AmenityRepository;
import com.example.hotel.service.AmenityService;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;

    public AmenityServiceImpl(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    @Override
    public Amenity addAmenity(Amenity amenity) {
        return amenityRepository.save(amenity);
    }

    @Override
    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    @Override
    public Amenity getAmenityById(Long id) {
        return amenityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Amenity not found with id: " + id));
    }

    @Override
    public Amenity updateAmenity(Long id, Amenity amenity) {
        Amenity existingAmenity = getAmenityById(id);

        existingAmenity.setName(amenity.getName());
        existingAmenity.setDescription(amenity.getDescription());

        return amenityRepository.save(existingAmenity);
    }

    @Override
    public void deleteAmenity(Long id) {
        Amenity amenity = getAmenityById(id);
        amenityRepository.delete(amenity);
    }
}
