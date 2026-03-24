package com.room_booking_app.sprint1.service;

import com.room_booking_app.sprint1.model.Reservation;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
// import org.springframework.beans.factory.annotation.Value;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // @Value("${spring.mail.host:NOT_SET}")
    // private String mailHost;

    // @Value("${spring.mail.port:-1}")
    // private String mailPort;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(Reservation reservation) {
        if (reservation == null || reservation.getUser() == null || reservation.getUser().getEmail() == null) {
            return;
        }
        // System.out.println("EMAIL_USERNAME=" + System.getenv("EMAIL_USERNAME"));
        // System.out.println("EMAIL_PASSWORD_SET=" + (System.getenv("EMAIL_PASSWORD") != null));

        // System.out.println("MAIL HOST=" + mailHost);
        // System.out.println("MAIL PORT=" + mailPort);
        // System.out.println("EMAIL_USERNAME=" + System.getenv("EMAIL_USERNAME"));
        // System.out.println("EMAIL_PASSWORD_SET=" + (System.getenv("EMAIL_PASSWORD") != null));

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

        mailSender.send(message);
        System.out.println("Booking confirmation email sent to " + reservation.getUser().getEmail());
    }
}