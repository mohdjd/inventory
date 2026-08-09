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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DispatchIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;
    private Long stockItemId;
    private Long workerId;
    private Long workTypeId;

    @BeforeEach
    void setUp() throws Exception {
        cleanDatabase();
        adminToken = login("admin", "admin123");

        stockItemId = stockItemRepository.save(StockItem.builder()
                .label("Batch-A").fabric("Cotton").size("M").weight("2kg")
                .quantity(100).purchaseDate(LocalDate.of(2026, 1, 1)).build()).getId();

        workerId = workerRepository.save(Worker.builder()
                .name("Ravi Kumar").phone("9876543210").build()).getId();

        workTypeId = workTypeRepository.save(WorkType.builder()
                .name("Embroidery").pricePerPiece(new BigDecimal("45.00")).build()).getId();
    }

    private Dtos.DispatchResponse createDispatch(int sentQty) throws Exception {
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

    @Test
    void dispatchesEndpoint_withoutToken_returnsForbidden() throws Exception {
        // No custom AuthenticationEntryPoint is configured, so an anonymous
        // request is rejected by the authorization filter with 403, not 401.
        mockMvc.perform(get("/api/dispatches"))
                .andExpect(status().isForbidden());
    }

    @Test
    void createDispatch_usesWorkTypeDefaultPrice_andAppearsInPendingList() throws Exception {
        Dtos.DispatchResponse created = createDispatch(100);

        mockMvc.perform(get("/api/dispatches")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(created.getId()))
                .andExpect(jsonPath("$[0].pricePerPiece").value(45.00))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].pendingQty").value(100));

        mockMvc.perform(get("/api/dispatches/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void receiveDispatch_partialThenFull_completesDispatch() throws Exception {
        Dtos.DispatchResponse created = createDispatch(100);

        Dtos.ReceiveRequest partial = Dtos.ReceiveRequest.builder()
                .quantity(40).receivedDate(LocalDate.of(2026, 1, 10)).build();

        mockMvc.perform(patch("/api/dispatches/{id}/receive", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PARTIAL"))
                .andExpect(jsonPath("$.receivedQty").value(40))
                .andExpect(jsonPath("$.pendingQty").value(60))
                .andExpect(jsonPath("$.totalPayable").value(1800.00));

        Dtos.ReceiveRequest remainder = Dtos.ReceiveRequest.builder()
                .quantity(60).receivedDate(LocalDate.of(2026, 1, 15)).build();

        mockMvc.perform(patch("/api/dispatches/{id}/receive", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(remainder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.receivedQty").value(100))
                .andExpect(jsonPath("$.pendingQty").value(0))
                .andExpect(jsonPath("$.totalPayable").value(4500.00));

        mockMvc.perform(get("/api/dispatches/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void receiveDispatch_withQuantityExceedingPending_returnsBadRequest() throws Exception {
        Dtos.DispatchResponse created = createDispatch(100);

        Dtos.ReceiveRequest tooMany = Dtos.ReceiveRequest.builder()
                .quantity(150).receivedDate(LocalDate.of(2026, 1, 10)).build();

        mockMvc.perform(patch("/api/dispatches/{id}/receive", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooMany)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void receiveDispatch_afterAlreadyCompleted_returnsBadRequest() throws Exception {
        Dtos.DispatchResponse created = createDispatch(50);

        Dtos.ReceiveRequest full = Dtos.ReceiveRequest.builder()
                .quantity(50).receivedDate(LocalDate.of(2026, 1, 10)).build();

        mockMvc.perform(patch("/api/dispatches/{id}/receive", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(full)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(patch("/api/dispatches/{id}/receive", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(full)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createDispatch_withUnknownStockItemId_returnsNotFound() throws Exception {
        Dtos.DispatchRequest req = Dtos.DispatchRequest.builder()
                .stockItemId(999_999L).workerId(workerId).workTypeId(workTypeId)
                .sentQty(10).sentDate(LocalDate.of(2026, 1, 5)).build();

        mockMvc.perform(post("/api/dispatches")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDispatch_withMissingRequiredFields_returnsBadRequestWithFieldErrors() throws Exception {
        String invalidPayload = "{\"sentQty\":10}";

        mockMvc.perform(post("/api/dispatches")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.stockItemId").exists())
                .andExpect(jsonPath("$.fieldErrors.workerId").exists())
                .andExpect(jsonPath("$.fieldErrors.workTypeId").exists())
                .andExpect(jsonPath("$.fieldErrors.sentDate").exists());
    }

    @Test
    void listDispatches_filteredByWorkerId_returnsOnlyMatchingWorker() throws Exception {
        createDispatch(20);
        Long otherWorkerId = workerRepository.save(Worker.builder()
                .name("Sunita Devi").phone("9876543211").build()).getId();

        Dtos.DispatchRequest reqForOtherWorker = Dtos.DispatchRequest.builder()
                .stockItemId(stockItemId).workerId(otherWorkerId).workTypeId(workTypeId)
                .sentQty(30).sentDate(LocalDate.of(2026, 1, 6)).build();

        mockMvc.perform(post("/api/dispatches")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqForOtherWorker)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/dispatches")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("workerId", String.valueOf(workerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].workerId").value(workerId));
    }
}
