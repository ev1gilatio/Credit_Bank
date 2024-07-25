package ru.evig.dealservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.evig.dealservice.dto.LoanOfferDto;
import ru.evig.dealservice.dto.StatementStatusHistoryDto;
import ru.evig.dealservice.enums.ApplicationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder(toBuilder = true)
@Table(name = "statement")
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Statement {

    @Id
    @GeneratedValue
    @Column(name = "statement_id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "client_id")
    private Client clientId;

    @OneToOne
    @JoinColumn(name = "credit_id")
    private Credit creditId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;

    @CreationTimestamp
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Type(type = "jsonb")
    @Column(name = "applied_offer")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private LocalDateTime signDate;

    @Column(name = "ses_code")
    private String sesCode;

    @Type(type = "jsonb")
    @Column(name = "status_history")
    private List<StatementStatusHistoryDto> statusHistory;
}
