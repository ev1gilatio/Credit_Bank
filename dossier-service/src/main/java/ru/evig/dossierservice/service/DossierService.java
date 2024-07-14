package ru.evig.dossierservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.evig.dossierservice.DossierClient;
import ru.evig.dossierservice.dto.EmailDetails;
import ru.evig.dossierservice.dto.EmailMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class DossierService {
    private final EmailService emailService;
    private final DossierClient dossierClient;

    @KafkaListener(topics = "${kafka.finishRegistrationTopic.name}",
            groupId = "${kafka.topic.group}",
            containerFactory = "emailMessageKafkaListenerContainerFactory"
    )
    public void listenToFinishRegistrationTopic(EmailMessage message) {
        log.info("Input data for listenToFinishRegistrationTopic = " + message);

        emailService.sendEmail(getEmailDetailsForFR(message));
    }

    @KafkaListener(topics = "${kafka.sendDocumentsTopic.name}",
            groupId = "${kafka.topic.group}",
            containerFactory = "emailMessageKafkaListenerContainerFactory"
    )
    public void listenToSendDocumentsTopic(EmailMessage message) {
        log.info("Input data for listenToSendDocumentsTopic = " + message);

        emailService.sendEmail(getEmailDetailsForSD(message));
    }

    @KafkaListener(topics = "${kafka.createDocumentsTopic.name}",
            groupId = "${kafka.topic.group}",
            containerFactory = "emailMessageKafkaListenerContainerFactory"
    )
    public void listenToCreateDocumentsTopic(EmailMessage message) {
        log.info("Input data for listenToCreateDocumentsTopic = " + message);

        emailService.sendEmail(getEmailDetailsForCD(message));
    }

    @KafkaListener(topics = "${kafka.sendSesTopic.name}",
            groupId = "${kafka.topic.group}",
            containerFactory = "emailMessageKafkaListenerContainerFactory"
    )
    public void listenToSendSesTopic(EmailMessage message) {
        log.info("Input data for listenToSendSesTopic = " + message);

        emailService.sendEmail(getEmailDetailsForSS(message));
    }

    @KafkaListener(topics = "${kafka.creditIssuedTopic.name}",
            groupId = "${kafka.topic.group}",
            containerFactory = "emailMessageKafkaListenerContainerFactory"
    )
    public void listenToCreditIssuedTopic(EmailMessage message) {
        log.info("Input data for listenToCreditIssuedTopic = " + message);

        emailService.sendEmail(getEmailDetailsForCI(message));
    }

    @KafkaListener(topics = "${kafka.deniedTopic.name}",
            groupId = "${kafka.topic.group}",
            containerFactory = "emailMessageKafkaListenerContainerFactory"
    )
    public void listenToDeniedTopic(EmailMessage message) {
        log.info("Input data for listenToDeniedTopic = " + message);

        emailService.sendEmail(getEmailDetailsForDenied(message));
    }

    private EmailDetails getEmailDetailsForFR(EmailMessage message) {
        String text = "ID вашей заявки: " + message.getStatementId() +
                "\n\nДля завершения регистрации перейдите по ссылке" +
                "\nhttp://localhost:8081/deal/calculate/" + message.getStatementId() +
                "\n\nС уважением, ООО \"ОпятьКредит\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForSD(EmailMessage message) {
        String text = "ID вашей заявки: " + message.getStatementId() +
                "\n\nДля получения кредитных документов перейдите по ссылке" +
                "\nhttp://localhost:8081/deal/document/" + message.getStatementId() + "/send" +
                "\n\nС уважением, ООО \"ОпятьКредит\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForCD(EmailMessage message) {
        String text = dossierClient.getDocumentation(message.getStatementId().toString()) +
                "\n\nДля подписания документов перейдите по ссылке" +
                "\nhttp://localhost:8081/deal/document/" + message.getStatementId() + "/sign" +
                "\n\nС уважением, ООО \"ОпятьКредит\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForSS(EmailMessage message) {
        String id = message.getStatementId().toString();

        String text = "Ваш код сессии: " + dossierClient.getSesCode(id) +
                "\n\nДля завершения процедуры перейдите по ссылке" +
                "\nhttp://localhost:8081/deal/document/" + message.getStatementId() + "/code" +
                "\n\nС уважением, ООО \"ОпятьКредит\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForCI(EmailMessage message) {
        String text = "ID вашей заявки: " + message.getStatementId() +
                "\n\nВаш код сессии: " + dossierClient.getSesCode(message.getStatementId().toString()) +
                "\n\nПоздравляем с оформлением кредита!" +
                "\n\nС уважением, ООО \"ОпятьКредит\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForDenied(EmailMessage message) {
        String text = "ID вашей заявки: " + message.getStatementId() +
                "\n\nС вашей стороны поступил отказ." +
                "\nДальнейшая работа с данной заявкой более не представляется возможной!" +
                "\n\nС уважением, ООО \"ОпятьКредит\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetails(EmailMessage message, String text) {

        return EmailDetails.builder()
                .to(message.getAddress())
                .subject(message.getTheme().name())
                .text(text)
                .build();
    }
}
