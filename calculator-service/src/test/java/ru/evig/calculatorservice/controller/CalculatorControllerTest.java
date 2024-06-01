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

        json = mapper.writeValueAsString(service.getLoanOfferDtoList(lsrDto));
        mvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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

        json = mapper.writeValueAsString(service.getCreditDto(sdDto));
        mvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private LoanStatementRequestDto getLsrDto() {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName("Vladimir")
                .lastName("Vladimir")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(2002, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }

    private ScoringDataDto getSdDto() {
        EmploymentDto eDto = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYEE)
                .employmentINN("INN")
                .salary(new BigDecimal(100000))
                .position(EmploymentPosition.SENIOR)
                .workExperienceTotal(24)
                .workExperienceCurrent(12)
                .build();
        
        return ScoringDataDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName("Vladimir")
                .lastName("Vladimir")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(2002, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .passportIssueDate(LocalDate.of(2020, 4, 27))
                .passportIssueBranch("SBp NeoFlex")
                .maritalStatus(MaritalStatus.NOT_MARRIED)
                .employment(eDto)
                .accountNumber("AccNumber")
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
    }
}
