package com.example.hotel.service;

public interface EmailService {

    // Send welcome email after customer registration
    void sendWelcomeEmail(String email, String customerName);

    // Send booking confirmation email
    void sendBookingConfirmationEmail(
            String email,
            String customerName,
            String bookingNumber
    );

    // Send payment confirmation email
    void sendPaymentConfirmationEmail(
            String email,
            String customerName,
            String transactionId,
            String amount
    );

    // Send booking cancellation email
    void sendBookingCancellationEmail(
            String email,
            String customerName,
            String bookingNumber
    );
}
