package ru.evig.dossierservice.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.evig.dossierservice.dto.EmailDetails;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;

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
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(from);
            message.setTo(details.getTo());
            message.setSubject(details.getSubject());
            message.setText(details.getText());

            sender.send(message);

            log.info("Sent data to Email from sendEmail = " + message);
        } catch (Exception e) {
            logging(e);
        }
    }

    public void sendEmailWithPdf(EmailDetails details) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(details.getTo());
            helper.setSubject(details.getSubject());
            helper.setText(details.getText());

            createPdf(details.getText());
            FileSystemResource file = new FileSystemResource(new File("Documents.pdf"));
            helper.addAttachment("Documents.pdf", file);

            sender.send(message);

            log.info("Sent data to Email from sendEmail = " + file);
        } catch (Exception e) {
            logging(e);
        }
    }

    private void createPdf(String text) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("Documents.pdf"));

        document.open();

        Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
        Paragraph paragraph = new Paragraph(text, font);
        document.add(paragraph);

        document.close();
    }

    private void logging(Exception e) {
        String logMessage = String.format("Exception: %s, message: %s",
                e.getClass().getSimpleName(), e.getMessage());

        log.error(logMessage);
    }
}
