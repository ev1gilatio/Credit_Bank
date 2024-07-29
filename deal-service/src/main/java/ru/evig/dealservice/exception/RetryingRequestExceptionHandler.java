package ru.evig.dealservice.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.evig.dealservice.entity.Statement;
import ru.evig.dealservice.enums.ApplicationStatus;
import ru.evig.dealservice.repository.StatementRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RetryingRequestExceptionHandler {
    private final StatementRepository statementRepository;

    public void checkClientDeniedAndCreditIssuedStatus(String statementId, ApplicationStatus status) {
        if (status.equals(ApplicationStatus.CLIENT_DENIED)) {
            throw new StatementDeniedException("Statement with ID " + statementId +
                    " cannot be processed further");
        } else if (status.equals(ApplicationStatus.CREDIT_ISSUED)) {
            throw new StatementIssuedException("Statement with ID " + statementId +
                    " already issued");
        }
    }

    public void checkCreatedCredit(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));
        checkClientDeniedAndCreditIssuedStatus(statementId, statement.getStatus());

        if (statement.getCreditId() != null) {
            throw new CreditCreatedException("Credit for statement with ID " + statementId +
                    " already created");
        }
    }

    public void checkSelectedOffer(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));
        checkClientDeniedAndCreditIssuedStatus(statementId, statement.getStatus());

        if (statement.getAppliedOffer() != null) {
            throw new OfferSelectedException("Offer for statement with ID " + statementId +
                    " already selected");
        }
    }

    public void checkDocumentStatus(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));
        ApplicationStatus status = statement.getStatus();
        checkClientDeniedAndCreditIssuedStatus(statementId, status);

        if (status.equals(ApplicationStatus.PREPARE_DOCUMENTS) ||
                status.equals(ApplicationStatus.DOCUMENT_CREATED)) {
            throw new DocumentStatusException("Documents for statement with ID " + statementId +
                    " already generated or performing is in progress");
        }
    }

    public void checkSesCodeGenerated(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));
        checkClientDeniedAndCreditIssuedStatus(statementId, statement.getStatus());

        if (statement.getSesCode() != null) {
            throw new SesCodeGeneratedException("Ses-code for statement with ID " + statementId +
                    " already generated");
        }
    }

    public void checkDocumentSignedAndCreditIssuedStatus(String statementId) {
        Statement statement = findStatementInDB(UUID.fromString(statementId));
        ApplicationStatus status = statement.getStatus();

        if (status.equals(ApplicationStatus.DOCUMENT_SIGNED) ||
                status.equals(ApplicationStatus.CREDIT_ISSUED)) {
            throw new DocumentStatusException("Documents for statement with ID " + statementId +
                    " already signed");
        }
    }

    private Statement findStatementInDB(UUID id) {
        Statement statement;
        Optional<Statement> statementOptional = statementRepository.findById(id);

        if (statementOptional.isPresent()) {
            statement = statementOptional.get();
        } else {
            throw new EntityNotFoundException("Statement with ID " + id + " not found");
        }

        return statement;
    }
}
