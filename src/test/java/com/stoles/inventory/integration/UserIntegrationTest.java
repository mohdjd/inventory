package com.stoles.inventory.integration;

import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * All /api/users/** endpoints require ADMIN. Users are never deleted between
 * tests (other entities hold ON DELETE RESTRICT FKs to AppUser.createdBy),
 * so each test uses a unique, nanoTime-suffixed username to avoid collisions.
 */
class UserIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = login("admin", "admin123");
    }

    private String uniqueUsername(String prefix) { return prefix + "_" + System.nanoTime(); }

    @Test
    void usersEndpoint_withoutToken_returnsForbidden() throws Exception {
        // No custom AuthenticationEntryPoint is configured, so an anonymous
        // request is rejected by the authorization filter with 403, not 401.
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void usersEndpoint_withNonAdminToken_returnsForbidden() throws Exception {
        String managerToken = login("manager", "manager123");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void userLifecycle_createToggleActiveAndResetPassword_succeeds() throws Exception {
        String username = uniqueUsername("newuser");
        Dtos.UserRequest createReq = Dtos.UserRequest.builder()
                .username(username).password("initialPass123")
                .fullName("New Test User").role(AppUser.Role.MANAGER).build();

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("MANAGER"))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn();

        Dtos.UserResponse created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), Dtos.UserResponse.class);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username == '" + username + "')]").exists());

        mockMvc.perform(patch("/api/users/{id}/toggle-active", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(patch("/api/users/{id}/toggle-active", created.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(patch("/api/users/{id}/reset-password", created.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("newPass456"))
                .andExpect(status().isOk());

        // the new password must actually work end-to-end through the auth endpoint
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Dtos.LoginRequest.builder()
                                .username(username).password("newPass456").build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void createUser_withDuplicateUsername_returnsBadRequest() throws Exception {
        String username = uniqueUsername("dupuser");
        Dtos.UserRequest req = Dtos.UserRequest.builder()
                .username(username).password("somePassword1")
                .fullName("Dup User").role(AppUser.Role.ACCOUNT).build();

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    void createUser_withMissingRequiredFields_returnsBadRequestWithFieldErrors() throws Exception {
        String invalidPayload = "{\"password\":\"short\"}";

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists())
                .andExpect(jsonPath("$.fieldErrors.fullName").exists())
                .andExpect(jsonPath("$.fieldErrors.role").exists());
    }

    @Test
    void toggleActive_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/users/{id}/toggle-active", 999_999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void resetPassword_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(patch("/api/users/{id}/reset-password", 999_999L)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("whatever123"))
                .andExpect(status().isNotFound());
    }
}
