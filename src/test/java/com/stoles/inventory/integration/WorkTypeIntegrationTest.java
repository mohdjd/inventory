package com.stoles.inventory.integration;

import com.stoles.inventory.dto.Dtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkTypeIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        cleanDatabase();
        adminToken = login("admin", "admin123");
    }

    @Test
    void workTypesEndpoint_withoutToken_returnsForbidden() throws Exception {
        // No custom AuthenticationEntryPoint is configured, so an anonymous
        // request is rejected by the authorization filter with 403, not 401.
        mockMvc.perform(get("/api/work-types"))
                .andExpect(status().isForbidden());
    }

    @Test
    void workTypeLifecycle_createListUpdateDelete_succeeds() throws Exception {
        Dtos.WorkTypeRequest createReq = Dtos.WorkTypeRequest.builder()
                .name("Embroidery").pricePerPiece(new BigDecimal("45.00")).build();

        MvcResult createResult = mockMvc.perform(post("/api/work-types")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Embroidery"))
                .andExpect(jsonPath("$.pricePerPiece").value(45.00))
                .andExpect(jsonPath("$.createdBy", containsString("admin")))
                .andReturn();

        Dtos.WorkTypeResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Dtos.WorkTypeResponse.class);

        mockMvc.perform(get("/api/work-types")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(created.getId()));

        Dtos.WorkTypeRequest updateReq = Dtos.WorkTypeRequest.builder()
                .name("Embroidery Premium").pricePerPiece(new BigDecimal("60.00")).build();

        mockMvc.perform(put("/api/work-types/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Embroidery Premium"))
                .andExpect(jsonPath("$.pricePerPiece").value(60.00));

        mockMvc.perform(delete("/api/work-types/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/work-types")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createWorkType_withMissingRequiredFields_returnsBadRequestWithFieldErrors() throws Exception {
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/work-types")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.pricePerPiece").exists());
    }

    @Test
    void deleteWorkType_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/work-types/{id}", 999_999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
