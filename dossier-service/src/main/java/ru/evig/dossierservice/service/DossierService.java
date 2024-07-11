package ru.evig.dossierservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.evig.dossierservice.dto.EmailDetails;
import ru.evig.dossierservice.dto.EmailMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class DossierService {
    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topic1.name}",
            groupId = "${kafka.topic.group}",
            containerFactory = "emailMessageKafkaListenerContainerFactory"
    )
    public void listenToTopic(EmailMessage message) {
        log.info("Input data for listenToTopic = " + message);

        emailService.sendEmail(getEmailDetails(message));
    }

    private EmailDetails getEmailDetails(EmailMessage message) {

        return EmailDetails.builder()
                .to(message.getAddress())
                .subject(message.getTheme().name())
                .text(message.getStatementId().toString())
                .build();
    }
}
