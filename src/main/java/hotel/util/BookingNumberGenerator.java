package com.example.hotel.util;



import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class BookingNumberGenerator {

    public String generateBookingNumber() {

        return "BK-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }
}
