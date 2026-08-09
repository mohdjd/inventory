package com.stoles.inventory.integration;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.StockItem;
import com.stoles.inventory.entity.WorkType;
import com.stoles.inventory.entity.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DashboardIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        cleanDatabase();
        adminToken = login("admin", "admin123");
    }

    @Test
    void dashboardEndpoint_withoutToken_returnsForbidden() throws Exception {
        // No custom AuthenticationEntryPoint is configured, so an anonymous
        // request is rejected by the authorization filter with 403, not 401.
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void dashboardSummary_withNoData_returnsZeroedTotals() throws Exception {
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStockPieces").value(0))
                .andExpect(jsonPath("$.totalSent").value(0))
                .andExpect(jsonPath("$.totalReceived").value(0))
                .andExpect(jsonPath("$.totalPendingAtWorker").value(0))
                .andExpect(jsonPath("$.availableStock").value(0))
                .andExpect(jsonPath("$.totalEarned").value(0))
                .andExpect(jsonPath("$.totalPaid").value(0))
                .andExpect(jsonPath("$.totalOutstanding").value(0))
                .andExpect(jsonPath("$.workerSummary.length()").value(0))
                .andExpect(jsonPath("$.recentDispatches.length()").value(0));
    }

    @Test
    void dashboardSummary_aggregatesStockDispatchesAndPayments() throws Exception {
        Long stockItemId = stockItemRepository.save(StockItem.builder()
                .label("Batch-A").fabric("Cotton").size("M").weight("2kg")
                .quantity(100).purchaseDate(LocalDate.of(2026, 1, 1)).build()).getId();

        Long workerId = workerRepository.save(Worker.builder()
                .name("Ravi Kumar").phone("9876543210").build()).getId();

        Long workTypeId = workTypeRepository.save(WorkType.builder()
                .name("Embroidery").pricePerPiece(new BigDecimal("45.00")).build()).getId();

        // Dispatch #1: sent 60, receive 40 -> status PARTIAL
        Dtos.DispatchResponse dispatch1 = createDispatch(stockItemId, workerId, workTypeId, 60);
        mockMvc.perform(patch("/api/dispatches/{id}/receive", dispatch1.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Dtos.ReceiveRequest.builder()
                                .quantity(40).receivedDate(LocalDate.of(2026, 1, 10)).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PARTIAL"));

        // Dispatch #2: sent 20, never received -> stays PENDING
        Dtos.DispatchResponse dispatch2 = createDispatch(stockItemId, workerId, workTypeId, 20);

        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Dtos.PaymentRequest.builder()
                                .workerId(workerId).amount(new BigDecimal("1000.00"))
                                .paymentDate(LocalDate.of(2026, 1, 12)).build())))
                .andExpect(status().isCreated());

        // totalEarned = receivedQty(40) * price(45.00) = 1800.00; outstanding = 1800 - 1000 = 800.00
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStockPieces").value(100))
                .andExpect(jsonPath("$.totalSent").value(80))
                .andExpect(jsonPath("$.totalReceived").value(40))
                .andExpect(jsonPath("$.totalPendingAtWorker").value(40))
                .andExpect(jsonPath("$.availableStock").value(20))
                .andExpect(jsonPath("$.totalEarned").value(1800.00))
                .andExpect(jsonPath("$.totalPaid").value(1000.00))
                .andExpect(jsonPath("$.totalOutstanding").value(800.00))
                .andExpect(jsonPath("$.workerSummary.length()").value(1))
                .andExpect(jsonPath("$.workerSummary[0].workerId").value(workerId))
                .andExpect(jsonPath("$.workerSummary[0].totalJobs").value(2))
                .andExpect(jsonPath("$.workerSummary[0].totalSent").value(80))
                .andExpect(jsonPath("$.workerSummary[0].totalReceived").value(40))
                .andExpect(jsonPath("$.workerSummary[0].pending").value(40))
                .andExpect(jsonPath("$.workerSummary[0].outstanding").value(800.00))
                // recentDispatches only surfaces strictly-PENDING dispatches, not PARTIAL ones
                .andExpect(jsonPath("$.recentDispatches.length()").value(1))
                .andExpect(jsonPath("$.recentDispatches[0].id").value(dispatch2.getId()))
                .andExpect(jsonPath("$.recentDispatches[0].status").value("PENDING"));
    }

    private Dtos.DispatchResponse createDispatch(Long stockItemId, Long workerId, Long workTypeId, int sentQty) throws Exception {
        Dtos.DispatchRequest req = Dtos.DispatchRequest.builder()
                .stockItemId(stockItemId).workerId(workerId).workTypeId(workTypeId)
                .sentQty(sentQty).sentDate(LocalDate.of(2026, 1, 5)).build();

        MvcResult result = mockMvc.perform(post("/api/dispatches")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(
                result.getResponse().getContentAsString(), Dtos.DispatchResponse.class);
    }
}
