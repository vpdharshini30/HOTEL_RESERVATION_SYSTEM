package com.example.hotel.service_impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.hotel.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(
            String to,
            String subject,
            String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendWelcomeEmail(
            String email,
            String customerName) {

        String subject = "Welcome to Hotel Reservation System";

        String body = "Hello " + customerName + ",\n\n"
                + "Welcome to our Hotel Reservation System!\n"
                + "Your account has been successfully created.\n\n"
                + "Thank you for registering with us.";

        sendEmail(email, subject, body);
    }

    @Override
    public void sendBookingConfirmationEmail(String email, String customerName,String bookingNumber) {

        String subject = "Booking Confirmation";

        String body = "Hello " + customerName + ",\n\n"
                + "Your hotel reservation has been confirmed.\n\n"
                + "Booking Number: " + bookingNumber + "\n\n"
                + "Thank you for choosing our hotel.";

        sendEmail(email, subject, body);
    }

    @Override
    public void sendPaymentConfirmationEmail(
            String email,
            String customerName,
            String transactionId,
            String amount) {

        String subject = "Payment Confirmation";

        String body = "Hello " + customerName + ",\n\n"
                + "Your payment was successfully confirmed.\n\n"
                + "Transaction ID: " + transactionId + "\n"
                + "Amount: " + amount + "\n\n"
                + "Thank you for your payment.";

        sendEmail(email, subject, body);
    }

    @Override
    public void sendBookingCancellationEmail(
            String email,
            String customerName,
            String bookingNumber) {

        String subject = "Booking Cancellation";

        String body = "Hello " + customerName + ",\n\n"
                + "Your hotel reservation has been cancelled.\n\n"
                + "Booking Number: " + bookingNumber + "\n\n"
                + "Contact our support team if you need assistance.";

        sendEmail(email, subject, body);
    }
}
