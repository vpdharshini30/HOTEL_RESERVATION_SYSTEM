package com.example.hotel.controller;



import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotel.dto.response.ApiResponse;
import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.dto.response.RoomTypeResponse;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.enums.RoomStatus;
import com.example.hotel.service.RoomService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> addRoom(
            @Valid @RequestBody Room room) {

        Room savedRoom = roomService.addRoom(room);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true,
                        "Room added successfully",
                        roomService.getRoomResponseById(savedRoom.getId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(
            @PathVariable Long id) {

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Room retrieved successfully",
                roomService.getRoomResponseById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Rooms retrieved successfully",
                roomService.getAllRoomResponses()));
    }

    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomByRoomNumber(
            @PathVariable String roomNumber) {

        Room room = roomService.getRoomByRoomNumber(roomNumber);

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Room retrieved successfully",
                roomService.getRoomResponseById(room.getId())));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByStatus(
            @PathVariable RoomStatus status) {

        List<RoomResponse> rooms = roomService.getRoomsByStatus(status)
                .stream()
                .map(room -> roomService.getRoomResponseById(room.getId()))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Rooms retrieved successfully", rooms));
    }

    @GetMapping("/type/{roomTypeId}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByRoomType(
            @PathVariable Long roomTypeId) {

        List<RoomResponse> rooms =
                roomService.getRoomsByRoomType(roomTypeId)
                .stream()
                .map(room -> roomService.getRoomResponseById(room.getId()))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Rooms retrieved successfully", rooms));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> searchAvailableRooms(
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate) {

        List<RoomResponse> rooms =
                roomService.searchAvailableRooms(checkInDate, checkOutDate)
                .stream()
                .map(room -> roomService.getRoomResponseById(room.getId()))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Available rooms retrieved successfully", rooms));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody Room room) {

        Room updatedRoom = roomService.updateRoom(id, room);

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Room updated successfully",
                roomService.getRoomResponseById(updatedRoom.getId())));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoomStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status) {

        Room updatedRoom = roomService.updateRoomStatus(id, status);

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Room status updated successfully",
                roomService.getRoomResponseById(updatedRoom.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(
            @PathVariable Long id) {

        roomService.deleteRoom(id);

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Room deleted successfully",
                "Room deleted successfully"));
    }

    // Room Type APIs

    @PostMapping("/types")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> addRoomType(
            @Valid @RequestBody RoomType roomType) {

        RoomType savedRoomType = roomService.addRoomType(roomType);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true,
                        "Room type added successfully",
                        roomService.getRoomTypeResponseById(
                                savedRoomType.getId())));
    }

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<RoomTypeResponse>>> getAllRoomTypes() {

        return ResponseEntity.ok(new ApiResponse<>(true,
                "Room types retrieved successfully",
                roomService.getAllRoomTypeResponses()));
    }
}

