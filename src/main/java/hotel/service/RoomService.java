package com.example.hotel.service;




import java.time.LocalDate;
import java.util.List;

import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.dto.response.RoomTypeResponse;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.enums.RoomStatus;


public interface RoomService {

    Room addRoom(Room room);

    Room getRoomById(Long id);

    Room getRoomByRoomNumber(String roomNumber);

    List<Room> getAllRooms();

    List<Room> getRoomsByStatus(RoomStatus status);

    List<Room> getRoomsByRoomType(Long roomTypeId);

    List<Room> searchAvailableRooms(
            LocalDate checkInDate,
            LocalDate checkOutDate);

    Room updateRoom(Long id, Room room);

    Room updateRoomStatus(Long id, RoomStatus status);

    void deleteRoom(Long id);

    RoomType addRoomType(RoomType roomType);

    List<RoomType> getAllRoomTypes();

    // Room DTO Methods
    RoomResponse getRoomResponseById(Long id);

    List<RoomResponse> getAllRoomResponses();

    // Room Type DTO Methods
    RoomTypeResponse getRoomTypeResponseById(Long id);

    List<RoomTypeResponse> getAllRoomTypeResponses();
}
