package com.example.hotel.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hotel.entity.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    // Find room type by name
    Optional<RoomType> findByTypeName(String typeName);

    // Check duplicate room type
    boolean existsByTypeName(String typeName);

    // Find room types by capacity
    List<RoomType> findByCapacity(Integer capacity);

    // Find room types within price range
    List<RoomType> findByPricePerNightBetween(
            java.math.BigDecimal minPrice,
            java.math.BigDecimal maxPrice
    );

}