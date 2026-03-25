package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Reservation;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(Reservation reservation) {
        if (reservation == null || reservation.getUser() == null || reservation.getUser().getEmail() == null) {
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a");

        String roomName = reservation.getRoom() != null ? reservation.getRoom().getName() : "Unknown Room";
        String buildingName = reservation.getRoom() != null && reservation.getRoom().getBuilding() != null
                ? reservation.getRoom().getBuilding().getName()
                : "Unknown Building";

        String body = "Your room booking has been confirmed.\n\n"
                + "Room: " + roomName + "\n"
                + "Building: " + buildingName + "\n"
                + "Start: " + reservation.getStartTime().format(formatter) + "\n"
                + "End: " + reservation.getEndTime().format(formatter) + "\n\n"
                + "Thank you.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(reservation.getUser().getEmail());
        message.setSubject("Room Booking Confirmation");
        message.setText(body);

        try {
            mailSender.send(message);
            System.out.println("Booking confirmation email sent to " + reservation.getUser().getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation email to "
                    + reservation.getUser().getEmail() + ": " + e.getMessage());
        }
    }
}