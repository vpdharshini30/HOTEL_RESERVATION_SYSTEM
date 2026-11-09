package com.example.hotel.repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hotel.entity.Room;
import com.example.hotel.enums.RoomStatus;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Find room using room number
    Optional<Room> findByRoomNumber(String roomNumber);

    // Check duplicate room number
    boolean existsByRoomNumber(String roomNumber);

    // Find rooms based on status
    List<Room> findByStatus(RoomStatus status);

    // Find rooms based on room type
    List<Room> findByRoomTypeId(Long roomTypeId);

    // Find available rooms by room type
    List<Room> findByRoomTypeIdAndStatus(
            Long roomTypeId,
            RoomStatus status
    );

    // Search rooms that are available for the requested dates
  
    @Query("SELECT r FROM Room r WHERE r.status = :status " +
    	       "AND NOT EXISTS (" +
    	       "SELECT res.id FROM Reservation res " +
    	       "WHERE res.room = r " +
    	       "AND res.status IN (" +
    	       "com.example.hotel.enums.ReservationStatus.PENDING, " +
    	       "com.example.hotel.enums.ReservationStatus.CONFIRMED) " +
    	       "AND res.checkInDate < :checkOutDate " +
    	       "AND res.checkOutDate > :checkInDate)")
    	List<Room> findAvailableRooms(
    	        @Param("status") RoomStatus status,
    	        @Param("checkInDate") LocalDate checkInDate,
    	        @Param("checkOutDate") LocalDate checkOutDate
    	);
}