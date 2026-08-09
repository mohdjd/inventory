package com.stoles.inventory.integration;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;
    private Long workerId;

    @BeforeEach
    void setUp() throws Exception {
        cleanDatabase();
        adminToken = login("admin", "admin123");
        workerId = workerRepository.save(Worker.builder()
                .name("Ravi Kumar").phone("9876543210").build()).getId();
    }

    @Test
    void paymentsEndpoint_withoutToken_returnsForbidden() throws Exception {
        // No custom AuthenticationEntryPoint is configured, so an anonymous
        // request is rejected by the authorization filter with 403, not 401.
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isForbidden());
    }

    @Test
    void paymentLifecycle_createListDelete_succeeds() throws Exception {
        Dtos.PaymentRequest createReq = Dtos.PaymentRequest.builder()
                .workerId(workerId).amount(new BigDecimal("1500.00"))
                .paymentDate(LocalDate.of(2026, 1, 10)).paymentMode("Cash").build();

        MvcResult createResult = mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.workerId").value(workerId))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.paymentMode").value("Cash"))
                .andExpect(jsonPath("$.createdBy", containsString("admin")))
                .andReturn();

        Dtos.PaymentResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Dtos.PaymentResponse.class);

        mockMvc.perform(get("/api/payments")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(created.getId()));

        mockMvc.perform(delete("/api/payments/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/payments")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createPayment_withMissingRequiredFields_returnsBadRequestWithFieldErrors() throws Exception {
        String invalidPayload = "{\"paymentMode\":\"Cash\"}";

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.workerId").exists())
                .andExpect(jsonPath("$.fieldErrors.amount").exists())
                .andExpect(jsonPath("$.fieldErrors.paymentDate").exists());
    }

    @Test
    void createPayment_withUnknownWorkerId_returnsNotFound() throws Exception {
        Dtos.PaymentRequest req = Dtos.PaymentRequest.builder()
                .workerId(999_999L).amount(new BigDecimal("500.00"))
                .paymentDate(LocalDate.of(2026, 1, 10)).build();

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePayment_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/payments/{id}", 999_999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void listPayments_filteredByWorkerIdAndDateRange_returnsOnlyMatching() throws Exception {
        Long otherWorkerId = workerRepository.save(Worker.builder()
                .name("Sunita Devi").phone("9876543211").build()).getId();

        // in range, target worker
        createPayment(workerId, "300.00", LocalDate.of(2026, 1, 5));
        // out of range, target worker
        createPayment(workerId, "400.00", LocalDate.of(2026, 3, 1));
        // in range, other worker
        createPayment(otherWorkerId, "500.00", LocalDate.of(2026, 1, 6));

        mockMvc.perform(get("/api/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("workerId", String.valueOf(workerId))
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].workerId").value(workerId))
                .andExpect(jsonPath("$[0].amount").value(300.00));
    }

    private void createPayment(Long forWorkerId, String amount, LocalDate date) throws Exception {
        Dtos.PaymentRequest req = Dtos.PaymentRequest.builder()
                .workerId(forWorkerId).amount(new BigDecimal(amount)).paymentDate(date).build();

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}
