package ru.evig.calculatorservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.evig.calculatorservice.dto.EmploymentDto;
import ru.evig.calculatorservice.dto.LoanStatementRequestDto;
import ru.evig.calculatorservice.dto.ScoringDataDto;
import ru.evig.calculatorservice.enums.EmploymentPosition;
import ru.evig.calculatorservice.enums.EmploymentStatus;
import ru.evig.calculatorservice.enums.Gender;
import ru.evig.calculatorservice.enums.MaritalStatus;
import ru.evig.calculatorservice.service.CalculatorService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
public class CalculatorControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CalculatorService service;

    @Test
    void shouldReturnLoanOfferDtoList() throws Exception {
        LoanStatementRequestDto lsrDto = getLsrDto();

        String json = mapper.writeValueAsString(lsrDto);
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnCreditDto() throws Exception {
        ScoringDataDto sdDto = getSdDto();

        String json = mapper.writeValueAsString(sdDto);
        mvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private LoanStatementRequestDto getLsrDto() {
        LoanStatementRequestDto lsrDto = new LoanStatementRequestDto();

        lsrDto.setAmount(new BigDecimal(30000));
        lsrDto.setTerm(6);
        lsrDto.setFirstName("Vladimir");
        lsrDto.setLastName("Vladimir");
        lsrDto.setEmail("vov@vov.vov");
        lsrDto.setBirthday(LocalDate.of(2002, 4, 27));
        lsrDto.setPassportSeries("0123");
        lsrDto.setPassportNumber("456789");

        return lsrDto;
    }

    private ScoringDataDto getSdDto() {
        EmploymentDto eDto = new EmploymentDto();
        ScoringDataDto sdDto = new ScoringDataDto();

        eDto.setEmploymentStatus(EmploymentStatus.EMPLOYEE);
        eDto.setEmploymentINN("INN");
        eDto.setSalary(new BigDecimal(100000));
        eDto.setPosition(EmploymentPosition.MILORD);
        eDto.setWorkExperienceTotal(24);
        eDto.setWorkExperienceCurrent(12);

        sdDto.setAmount(new BigDecimal(30000));
        sdDto.setTerm(6);
        sdDto.setFirstName("Vladimir");
        sdDto.setLastName("Vladimir");
        sdDto.setGender(Gender.MALE);
        sdDto.setBirthday(LocalDate.of(2002, 4, 27));
        sdDto.setPassportSeries("0123");
        sdDto.setPassportNumber("456789");
        sdDto.setPassportIssueDate(LocalDate.of(2020, 4, 27));
        sdDto.setPassportIssueBranch("SBp_NeoFlex");
        sdDto.setMaritalStatus(MaritalStatus.NOT_MARRIED);
        sdDto.setEmployment(eDto);
        sdDto.setAccountNumber("AccNumber");
        sdDto.setIsInsuranceEnabled(false);
        sdDto.setIsSalaryClient(false);

        return sdDto;
    }
}
