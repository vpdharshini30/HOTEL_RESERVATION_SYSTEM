package com.example.hotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.entity.Amenity;
import com.example.hotel.service.AmenityService;

@RestController
@RequestMapping("/api/amenities")
public class AmenityController {

    private final AmenityService amenityService;

    public AmenityController(AmenityService amenityService) {
        this.amenityService = amenityService;
    }

    // Add Amenity
    @PostMapping
    public ResponseEntity<Amenity> addAmenity(
            @RequestBody Amenity amenity) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(amenityService.addAmenity(amenity));
    }

    // Get All Amenities
    @GetMapping
    public ResponseEntity<List<Amenity>> getAllAmenities() {

        return ResponseEntity.ok(
                amenityService.getAllAmenities());
    }

    // Get Amenity By ID
    @GetMapping("/{id}")
    public ResponseEntity<Amenity> getAmenityById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                amenityService.getAmenityById(id));
    }

    // Update Amenity
    @PutMapping("/{id}")
    public ResponseEntity<Amenity> updateAmenity(
            @PathVariable Long id,
            @RequestBody Amenity amenity) {

        return ResponseEntity.ok(
                amenityService.updateAmenity(id, amenity));
    }

    // Delete Amenity
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAmenity(
            @PathVariable Long id) {

        amenityService.deleteAmenity(id);

        return ResponseEntity.ok(
                "Amenity deleted successfully");
    }
}