package ru.evig.dealservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.evig.dealservice.DealClient;
import ru.evig.dealservice.dto.*;
import ru.evig.dealservice.entity.Client;
import ru.evig.dealservice.entity.Statement;
import ru.evig.dealservice.enums.*;
import ru.evig.dealservice.service.DealService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealControllerImpl.class)
public class DealControllerImplTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DealClient dealClient;

    @MockBean
    private DealService service;

    @Test
    void shouldReturnLoanOfferDtoList() throws Exception {
        when(service.createClient(getLsrDto(30000)))
                .thenReturn(createClient(getLsrDto(30000)));

        Client client = service.createClient(getLsrDto(30000));

        when(service.createStatement(client))
                .thenReturn(createStatement(client));

        String json = mapper.writeValueAsString(getLsrDto(30000));
        performOkRequest(json, "/deal/statement");
    }

    @Test
    void shouldReturnHttpMessageNotReadableExceptionInsteadOfLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(null);
        performBadRequest(json,
                "/deal/statement",
                "Invalid JSON payload"
        );
    }

    @Test
    void shouldReturnMethodArgumentNotValidExceptionInsteadOfLoanOfferDtoList() throws Exception {
        String json = mapper.writeValueAsString(getLsrDto(3000));
        performBadRequest(json,
                "/deal/statement",
                "{\"amount\":\"must be greater than or equal to 30000\"}"
        );
    }

    @Test
    void shouldSelectLoanOffer() throws Exception {
        String json = mapper.writeValueAsString(getLoDto(6));
        performOkRequest(json, "/deal/offer/select");
    }

    @Test
    void shouldReturnHttpMessageNotReadableException() throws Exception {
        String json = mapper.writeValueAsString(null);
        performBadRequest(json,
                "/deal/offer/select",
                "Invalid JSON payload"
        );
    }

    @Test
    void shouldReturnMethodArgumentNotValidException() throws Exception {
        String json = mapper.writeValueAsString(getLoDto(-1));
        performBadRequest(json,
                "/deal/offer/select",
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

    private Client createClient(LoanStatementRequestDto lsrDto) {
        PassportDto passport = PassportDto.builder()
                .series(lsrDto.getPassportSeries())
                .number(lsrDto.getPassportNumber())
                .build();

        return Client.builder()
                .id(UUID.randomUUID())
                .lastName(lsrDto.getLastName())
                .firstName(lsrDto.getFirstName())
                .middleName(lsrDto.getMiddleName())
                .birthDate(lsrDto.getBirthday())
                .email(lsrDto.getEmail())
                .passport(passport)
                .build();
    }

    private Statement createStatement(Client client) {
        return Statement.builder()
                .id(UUID.randomUUID())
                .clientId(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(new ArrayList<>())
                .build();
    }

    private LoanStatementRequestDto getLsrDto(Integer amount) {

        return LoanStatementRequestDto.builder()
                .amount(new BigDecimal(amount))
                .term(6)
                .firstName("Vladimir")
                .lastName("Petrov")
                .email("vov@vov.vov")
                .birthday(LocalDate.of(2002, 4, 27))
                .passportSeries("0123")
                .passportNumber("456789")
                .build();
    }

    private LoanOfferDto getLoDto(Integer term) {

        return LoanOfferDto.builder()
                .statementId(createStatement(createClient(getLsrDto(30000))).getId())
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
