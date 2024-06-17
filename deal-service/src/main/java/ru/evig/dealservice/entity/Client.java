package ru.evig.dealservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.evig.dealservice.dto.EmploymentDto;
import ru.evig.dealservice.dto.PassportDto;
import ru.evig.dealservice.enums.Gender;
import ru.evig.dealservice.enums.MaritalStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder(toBuilder = true)
@Table(name = "client")
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Client {

    @Id
    @Column(name = "client_id")
    private UUID id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    @Type(type = "jsonb")
    @Column(name = "passport_id")
    private PassportDto passport;

    @Type(type = "jsonb")
    @Column(name = "employment_id")
    private EmploymentDto employment;

    @Column(name = "account_number")
    private String accountNumber;
}
