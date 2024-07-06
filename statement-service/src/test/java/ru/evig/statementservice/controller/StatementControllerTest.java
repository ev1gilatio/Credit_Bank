package ru.evig.statementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.evig.statementservice.StatementClient;
import ru.evig.statementservice.dto.LoanOfferDto;
import ru.evig.statementservice.dto.LoanStatementRequestDto;
import ru.evig.statementservice.service.StatementService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatementController.class)
public class StatementControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StatementClient statementClient;

    @MockBean
    private StatementService service;

    @Test
    void shouldReturnLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(getLsrDto(30000, 2002));
        performOkRequest(json, "/statement");
    }

    @Test
    void shouldReturnHttpMessageNotReadableExceptionInsteadOfLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(null);
        performBadRequest(json,
                "/statement",
                "Invalid JSON payload"
        );
    }

    @Test
    void shouldReturnMethodArgumentNotValidExceptionInsteadOfLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(getLsrDto(3000, 2002));
        performBadRequest(json,
                "/statement",
                "{\"amount\":\"must be greater than or equal to 30000\"}"
        );
    }

    @Test
    void shouldSelectLoanOffer() throws Exception {
        String json = mapper.writeValueAsString(getLoDto(6));
        performOkRequest(json, "/statement/offer");
    }

    @Test
    void shouldReturnHttpMessageNotReadableException() throws Exception {
        String json = mapper.writeValueAsString(null);
        performBadRequest(json,
                "/statement/offer",
                "Invalid JSON payload"
        );
    }

    @Test
    void shouldReturnMethodArgumentNotValidException() throws Exception {
        String json = mapper.writeValueAsString(getLoDto(-1));
        performBadRequest(json,
                "/statement/offer",
                "{\"term\":\"must be greater than or equal to 6\"}"
        );
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

    private LoanStatementRequestDto getLsrDto(int amount, int birthYear) {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(amount))
                .term(6)
                .firstName("Vladimir")
                .lastName("Petrov")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(birthYear, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }

    private LoanOfferDto getLoDto(int term) {

        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(new BigDecimal(30000))
                .totalAmount(new BigDecimal(30000))
                .term(term)
                .monthlyPayment(new BigDecimal("5154.24"))
                .rate(new BigDecimal("10.5"))
                .isInsuranceEnable(false)
                .isSalaryClient(false)
                .build();
    }
}
