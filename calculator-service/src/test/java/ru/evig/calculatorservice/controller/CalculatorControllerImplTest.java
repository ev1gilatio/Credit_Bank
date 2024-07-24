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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorControllerImpl.class)
public class CalculatorControllerImplTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CalculatorService service;

    @Test
    void shouldReturnLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(getLsrDto("Vladimir"));

        performOkRequest(json, "/calculator/offers");
    }

    @Test
    void shouldReturnHttpMessageNotReadableExceptionInsteadOfLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(service.getLoanOfferDtoList(getLsrDto(null)));

        performBadRequest(json, "/calculator/offers", "Invalid JSON payload");
    }

    @Test
    void shouldReturnMethodArgumentNotValidExceptionInsteadOfLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(getLsrDto(null));

        performBadRequest(json, "/calculator/offers", "{\"firstName\":\"must not be null\"}");
    }

    @Test
    void shouldReturnCreditDto() throws Exception {
        String json = mapper.writeValueAsString(getSdDto("Vladimir"));

        performOkRequest(json, "/calculator/calc");
    }

    @Test
    void shouldReturnHttpMessageNotReadableExceptionInsteadOfCreditDto() throws Exception {
        String json = mapper.writeValueAsString(service.getCreditDto(getSdDto(null)));

        performBadRequest(json, "/calculator/calc", "Invalid JSON payload");
    }

    @Test
    void shouldReturnMethodArgumentNotValidExceptionInsteadOfCreditDto() throws Exception {
        String json = mapper.writeValueAsString(getSdDto(null));

        performBadRequest(json, "/calculator/calc", "{\"firstName\":\"must not be null\"}");
    }

    private void performOkRequest(String json, String url) throws Exception {
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void performBadRequest(String json, String url, String expectedResponse) throws Exception {
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedResponse));
    }

    private LoanStatementRequestDto getLsrDto(String firstName) {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName(firstName)
                .lastName("Vladimir")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(2002, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }

    private ScoringDataDto getSdDto(String firstName) {
        EmploymentDto eDto = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYEE)
                .employmentINN("0123456789")
                .salary(new BigDecimal(100000))
                .position(EmploymentPosition.SENIOR)
                .workExperienceTotal(24)
                .workExperienceCurrent(12)
                .build();

        return ScoringDataDto.builder()
                .amount(new BigDecimal(30000))
                .term(6)
                .firstName(firstName)
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
