
package com.example.hotel.service_impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.dto.response.RoomTypeResponse;
import com.example.hotel.entity.Amenity;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.enums.RoomStatus;
import com.example.hotel.exception.DuplicateResourceException;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.exception.RoomNotAvailableException;
import com.example.hotel.mapper.RoomMapper;
import com.example.hotel.mapper.RoomTypeMapper;
import com.example.hotel.repository.AmenityRepository;
import com.example.hotel.repository.RoomRepository;
import com.example.hotel.repository.RoomTypeRepository;
import com.example.hotel.service.RoomService;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final AmenityRepository amenityRepository;
    private final RoomMapper roomMapper;
    private final RoomTypeMapper roomTypeMapper;

    public RoomServiceImpl(
            RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository,
            AmenityRepository amenityRepository,
            RoomMapper roomMapper,
            RoomTypeMapper roomTypeMapper) {

        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.amenityRepository = amenityRepository;
        this.roomMapper = roomMapper;
        this.roomTypeMapper = roomTypeMapper;
    }

    // Add Room
    @Override
    public Room addRoom(Room room) {

        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new DuplicateResourceException("Room number already exists");
        }

        if (room.getRoomType() == null || room.getRoomType().getId() == null) {
            throw new IllegalArgumentException("Room type is required");
        }

        RoomType roomType = roomTypeRepository
                .findById(room.getRoomType().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room type not found"));

        room.setRoomType(roomType);

        if (room.getAmenities() != null) {
            List<Amenity> amenities = new ArrayList<>();

            for (Amenity amenity : room.getAmenities()) {
                if (amenity.getId() == null) {
                    throw new IllegalArgumentException("Amenity ID is required");
                }

                Amenity existingAmenity = amenityRepository
                        .findById(amenity.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Amenity not found with ID: " + amenity.getId()));

                amenities.add(existingAmenity);
            }

            room.setAmenities(amenities);
        }

        return roomRepository.save(room);
    }

    // Get Room By ID
    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {

        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + id));
    }

    // Get Room By Room Number
    @Override
    @Transactional(readOnly = true)
    public Room getRoomByRoomNumber(String roomNumber) {

        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found: " + roomNumber));
    }

    // Get All Rooms
    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Get Rooms By Status
    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status);
    }

    // Get Rooms By Room Type
    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByRoomType(Long roomTypeId) {

        if (!roomTypeRepository.existsById(roomTypeId)) {
            throw new ResourceNotFoundException(
                    "Room type not found with ID: " + roomTypeId);
        }

        return roomRepository.findByRoomTypeId(roomTypeId);
    }

    // Search Available Rooms
    @Override
    @Transactional(readOnly = true)
    public List<Room> searchAvailableRooms(
            LocalDate checkInDate,
            LocalDate checkOutDate) {

        if (checkInDate == null || checkOutDate == null
                || !checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException(
                    "Invalid check-in or check-out dates");
        }

        return roomRepository.findAvailableRooms(
                RoomStatus.AVAILABLE,
                checkInDate,
                checkOutDate);
    }

    // Update Room
    @Override
    public Room updateRoom(Long id, Room updatedRoom) {

        Room existingRoom = getRoomById(id);

        if (updatedRoom.getRoomNumber() != null
                && !existingRoom.getRoomNumber().equals(updatedRoom.getRoomNumber())
                && roomRepository.existsByRoomNumber(updatedRoom.getRoomNumber())) {

            throw new DuplicateResourceException(
                    "Room number already exists");
        }

        if (updatedRoom.getRoomNumber() != null) {
            existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        }

        if (updatedRoom.getDescription() != null) {
            existingRoom.setDescription(updatedRoom.getDescription());
        }

        if (updatedRoom.getFloorNumber() != null) {
            existingRoom.setFloorNumber(updatedRoom.getFloorNumber());
        }

        if (updatedRoom.getStatus() != null) {
            existingRoom.setStatus(updatedRoom.getStatus());
        }

        if (updatedRoom.getRoomType() != null
                && updatedRoom.getRoomType().getId() != null) {

            RoomType roomType = roomTypeRepository
                    .findById(updatedRoom.getRoomType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Room type not found"));

            existingRoom.setRoomType(roomType);
        }

        if (updatedRoom.getAmenities() != null) {

            List<Amenity> amenities = new ArrayList<>();

            for (Amenity amenity : updatedRoom.getAmenities()) {

                if (amenity.getId() == null) {
                    throw new IllegalArgumentException(
                            "Amenity ID is required");
                }

                Amenity existingAmenity = amenityRepository
                        .findById(amenity.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Amenity not found with ID: " + amenity.getId()));

                amenities.add(existingAmenity);
            }

            existingRoom.setAmenities(amenities);
        }

        return roomRepository.save(existingRoom);
    }

    // Update Room Status
    @Override
    public Room updateRoomStatus(Long id, RoomStatus status) {

        Room room = getRoomById(id);
        room.setStatus(status);

        return roomRepository.save(room);
    }

    // Delete Room
    @Override
    public void deleteRoom(Long id) {

        Room room = getRoomById(id);

        if (room.getReservations() != null
                && !room.getReservations().isEmpty()) {

            throw new RoomNotAvailableException(
                    "Cannot delete room because reservations exist");
        }

        roomRepository.delete(room);
    }

    // Add Room Type
    @Override
    public RoomType addRoomType(RoomType roomType) {

        if (roomTypeRepository.existsByTypeName(roomType.getTypeName())) {
            throw new DuplicateResourceException(
                    "Room type already exists");
        }

        return roomTypeRepository.save(roomType);
    }

    // Get All Room Types
    @Override
    @Transactional(readOnly = true)
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    // Get Room Response By ID
    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomResponseById(Long id) {

        Room room = getRoomById(id);
        return roomMapper.toResponse(room);
    }

    // Get All Room Responses
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRoomResponses() {

        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    // Get Room Type Response By ID
    @Override
    @Transactional(readOnly = true)
    public RoomTypeResponse getRoomTypeResponseById(Long id) {

        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room type not found with ID: " + id));

        return roomTypeMapper.toResponse(roomType);
    }

    // Get All Room Type Responses
    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeResponse> getAllRoomTypeResponses() {

        return roomTypeRepository.findAll()
                .stream()
                .map(roomTypeMapper::toResponse)
                .toList();
    }
}
