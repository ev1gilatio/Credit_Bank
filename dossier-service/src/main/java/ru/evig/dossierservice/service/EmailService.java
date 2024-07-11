package ru.evig.dossierservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.evig.dossierservice.dto.EmailDetails;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender sender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public void sendEmail(EmailDetails details) {
        log.info("Input data for sendEMail = " + details);

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(from);
            message.setTo(details.getTo());
            message.setSubject(details.getSubject());
            message.setText(details.getText());

            sender.send(message);

            log.info("Sent data to Email from sendEmail = " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
