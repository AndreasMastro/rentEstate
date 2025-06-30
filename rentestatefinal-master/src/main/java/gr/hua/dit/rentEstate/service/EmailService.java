package gr.hua.dit.rentEstate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    //the JavaMailSender used to send emails
    private final JavaMailSender mailSender;

    // Retrieves the sender email address from the application.properties (app.email.from)
    @Value("${app.email.from}")
    private String fromEmail;

    // Constructor
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Sends a welcome email to the user after registration
    public void sendWelcomeEmail(String userEmail, String username) {
        try {
            String subject = "Welcome to the RentEstate team!";
            String message = "Welcome " + username + "!\n\n" +
                    "Your registration is completed!\n\n" +
                    "Now you can log in to RentEstate and browse through the estates or even add one!\n\n" +
                    "For any information, please contact our team.\n\n" +
                    "The RentEstate Team";

            sendEmail(userEmail, subject, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    // Utility method to construct and send an email
    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            // Sends the email
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}