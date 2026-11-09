package com.example.hotel.mapper;



import org.springframework.stereotype.Component;

import com.example.hotel.dto.response.RoomTypeResponse;
import com.example.hotel.entity.RoomType;


@Component
public class RoomTypeMapper {

    public RoomTypeResponse toResponse(RoomType roomType) {

        RoomTypeResponse response = new RoomTypeResponse();

        response.setId(roomType.getId());
        response.setTypeName(roomType.getTypeName());
        response.setDescription(roomType.getDescription());
        response.setPricePerNight(roomType.getPricePerNight());
        response.setCapacity(roomType.getCapacity());

        return response;
    }
}
