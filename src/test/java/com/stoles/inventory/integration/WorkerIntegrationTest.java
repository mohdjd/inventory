package com.stoles.inventory.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoles.inventory.dto.Dtos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WorkerIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        cleanDatabase();
        adminToken = login("admin", "admin123");
    }

    @Test
    void workersEndpoint_withoutToken_returnsForbidden() throws Exception {
        // No custom AuthenticationEntryPoint is configured, so an anonymous
        // request is rejected by the authorization filter with 403, not 401.
        mockMvc.perform(get("/api/workers"))
                .andExpect(status().isForbidden());
    }

    @Test
    void workerLifecycle_createListUpdateDelete_succeeds() throws Exception {
        Dtos.WorkerRequest createReq = Dtos.WorkerRequest.builder()
                .name("Ravi Kumar").phone("9876543210").address("Surat").build();

        MvcResult createResult = mockMvc.perform(post("/api/workers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Ravi Kumar"))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.createdBy", containsString("admin")))
                .andReturn();

        Dtos.WorkerResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Dtos.WorkerResponse.class);

        mockMvc.perform(get("/api/workers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(created.getId()));

        Dtos.WorkerRequest updateReq = Dtos.WorkerRequest.builder()
                .name("Ravi Kumar Updated").phone("9999999999").address("Ahmedabad").build();

        mockMvc.perform(put("/api/workers/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ravi Kumar Updated"))
                .andExpect(jsonPath("$.phone").value("9999999999"));

        mockMvc.perform(delete("/api/workers/{id}", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/workers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createWorker_withMissingRequiredFields_returnsBadRequestWithFieldErrors() throws Exception {
        String invalidPayload = "{\"phone\":\"9876543210\"}";

        mockMvc.perform(post("/api/workers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void deleteWorker_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/workers/{id}", 999_999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void workerSummary_withNoDispatches_returnsEmptyList() throws Exception {
        mockMvc.perform(post("/api/workers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Dtos.WorkerRequest.builder()
                                .name("Sunita Devi").phone("9876543211").build())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/workers/summary")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
