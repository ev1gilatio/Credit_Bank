package ru.evig.dossierservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.evig.dossierservice.DossierClient;
import ru.evig.dossierservice.dto.EmailDetails;
import ru.evig.dossierservice.dto.EmailMessage;
import ru.evig.dossierservice.dto.PassportDto;
import ru.evig.dossierservice.entity.Client;
import ru.evig.dossierservice.entity.Credit;
import ru.evig.dossierservice.entity.Statement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

        emailService.sendEmailWithPdf(getEmailDetailsForCD(message));
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
        String text = "Statement ID: " + message.getStatementId() +
                "\n\nTo complete the registration, follow the link" +
                "\nhttp://localhost:8081/deal/calculate/" + message.getStatementId() +
                "\n\nYour, OOO \"CreditAgain\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForSD(EmailMessage message) {
        String text = "Statement ID: " + message.getStatementId() +
                "\n\nTo get credit documents, follow the link" +
                "\nhttp://localhost:8081/deal/document/" + message.getStatementId() + "/send" +
                "\n\nYour, OOO \"CreditAgain\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForCD(EmailMessage message) {
        String text = createDocumentation(message.getStatementId().toString()) +
                "\n\nTo sign documents, follow the link" +
                "\nhttp://localhost:8081/deal/document/" + message.getStatementId() + "/sign" +
                "\n\nYour, OOO \"CreditAgain\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForSS(EmailMessage message) {
        String text = "Session code: " + dossierClient.getSesCode(message.getStatementId().toString()) +
                "\n\nTo complete operation, follow the link" +
                "\nhttp://localhost:8081/deal/document/" + message.getStatementId() + "/code" +
                "\n\nYour, OOO \"CreditAgain\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForCI(EmailMessage message) {
        String text = "Statement ID: " + message.getStatementId() +
                "\n\nSession code: " + dossierClient.getSesCode(message.getStatementId().toString()) +
                "\n\nCongratulations! Now you have a credit!" +
                "\n\nYour, OOO \"CreditAgain\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetailsForDenied(EmailMessage message) {
        String text = "Statement ID: " + message.getStatementId() +
                "\n\nYou have refused the offer." +
                "\nFurther operations with this statement are not possible" +
                "\n\nYour, OOO \"CreditAgain\"";

        return getEmailDetails(message, text);
    }

    private EmailDetails getEmailDetails(EmailMessage message, String text) {

        return EmailDetails.builder()
                .to(message.getAddress())
                .subject(message.getTheme().name())
                .text(text)
                .build();
    }

    private String createDocumentation(String statementId) {
        Statement statement = dossierClient.getStatement(statementId);
        Client client = statement.getClientId();
        Credit credit = statement.getCreditId();

        String theme = "Credit documents";
        String clientStatementId = "Statement Id: " + statementId;
        String lastName = client.getLastName();
        String firstName = client.getFirstName();
        String middleName = client.getMiddleName();
        String fio = String.format("%s %s %s", lastName, firstName, middleName);
        LocalDate birthDate = client.getBirthDate();
        String birth = birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String email = client.getEmail();
        PassportDto pDto = client.getPassport();
        String passport = String.format("%s %s", pDto.getSeries(), pDto.getNumber());
        String subTheme = "Credit terms";
        String amount = credit.getAmount().toString();
        String term = credit.getTerm().toString();
        String monthlyPayment = credit.getMonthlyPayment().toString();
        String rate = credit.getRate().toString();
        String psk = credit.getPsk().toString();
        boolean insurance = credit.isInsuranceEnabled();
        boolean salaryClient = credit.isSalaryClient();

        return theme + "\n\n" +
                clientStatementId + "\n" +
                fio + "\n" +
                birth + "\n" +
                email + "\n" +
                "Passport: " + passport + "\n\n" +
                subTheme + "\n\n" +
                "Amount: " + amount + "\n" +
                "Term: " + term + "\n" +
                "Monthly payment: " + monthlyPayment + "\n" +
                "Rate: " + rate + "\n" +
                "PSK: " + psk + "\n" +
                "Insurance: " + insurance + "\n" +
                "Salary client: " + salaryClient;
    }
}
