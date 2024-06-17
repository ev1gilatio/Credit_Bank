package ru.evig.dealservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.evig.dealservice.dto.PaymentScheduleElementDto;
import ru.evig.dealservice.enums.CreditStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@Table(name = "credit")
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Credit {

    @Id
    @Column(name = "credit_id")
    private UUID id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "term")
    private Integer term;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "psk")
    private BigDecimal psk;

    @Type(type = "jsonb")
    @Column(name = "payment_schedule")
    private List<PaymentScheduleElementDto> paymentSchedule;

    @Column(name = "insurance_enabled")
    private Boolean insuranceEnabled;

    @Column(name = "salary_client")
    private Boolean salaryClient;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status")
    private CreditStatus creditStatus;
}
