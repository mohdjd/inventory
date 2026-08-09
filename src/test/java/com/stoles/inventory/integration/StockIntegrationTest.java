package com.stoles.inventory.integration;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import com.stoles.inventory.dto.Dtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class StockIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        cleanDatabase();
        adminToken = login("admin", "admin123");
    }

    @Test
    void stockEndpoint_withoutToken_returnsForbidden() throws Exception {
        // No custom AuthenticationEntryPoint is configured, so an anonymous
        // request is rejected by the authorization filter with 403, not 401.
        mockMvc.perform(get("/api/stock"))
                .andExpect(status().isForbidden());
    }

    @Test
    void stockLifecycle_createListUpdateDelete_succeeds() throws Exception {
        Dtos.StockRequest createReq = Dtos.StockRequest.builder()
                .label("Batch-A").fabric("Cotton").size("M").weight("2kg")
                .quantity(100).purchaseDate(LocalDate.of(2026, 1, 15)).build();

        MvcResult createResult = mockMvc.perform(post("/api/stock")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.label").value("Batch-A"))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.createdBy", containsString("admin")))
                .andReturn();

        Dtos.StockResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Dtos.StockResponse.class);

        mockMvc.perform(get("/api/stock")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(created.getId()));

        Dtos.StockRequest updateReq = Dtos.StockRequest.builder()
                .label("Batch-A-Updated").fabric("Cotton").size("L").weight("2.5kg")
                .quantity(80).purchaseDate(LocalDate.of(2026, 1, 15)).build();

        mockMvc.perform(put("/api/stock/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Batch-A-Updated"))
                .andExpect(jsonPath("$.quantity").value(80));

        mockMvc.perform(delete("/api/stock/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/stock")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createStockItem_withMissingRequiredFields_returnsBadRequestWithFieldErrors() throws Exception {
        String invalidPayload = "{\"label\":\"\",\"fabric\":\"Cotton\"}";

        mockMvc.perform(post("/api/stock")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.label").exists())
                .andExpect(jsonPath("$.fieldErrors.size").exists())
                .andExpect(jsonPath("$.fieldErrors.quantity").exists());
    }

    @Test
    void deleteStockItem_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/stock/{id}", 999_999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
