package com.example.hotel.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotel.entity.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

}
