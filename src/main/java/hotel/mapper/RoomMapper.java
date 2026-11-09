
package com.example.hotel.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.hotel.dto.response.RoomResponse;
import com.example.hotel.entity.Room;

@Component
public class RoomMapper {

    public RoomResponse toResponse(Room room) {

        RoomResponse response = new RoomResponse();

        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setDescription(room.getDescription());
        response.setFloorNumber(room.getFloorNumber());
        response.setStatus(room.getStatus());

        if (room.getRoomType() != null) {

            response.setRoomTypeId(room.getRoomType().getId());
            response.setRoomTypeName(room.getRoomType().getTypeName());
            response.setPricePerNight(
                    room.getRoomType().getPricePerNight());
            response.setCapacity(room.getRoomType().getCapacity());
        }

        if (room.getAmenities() != null) {

            List<String> amenities = room.getAmenities()
                    .stream()
                    .map(amenity -> amenity.getName())
                    .toList();

            response.setAmenities(amenities);
        }

        return response;
    }
}
