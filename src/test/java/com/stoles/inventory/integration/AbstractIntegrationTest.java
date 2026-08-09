package com.stoles.inventory.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoles.inventory.dto.Dtos;
import com.stoles.inventory.repository.DispatchRepository;
import com.stoles.inventory.repository.PaymentRepository;
import com.stoles.inventory.repository.StockItemRepository;
import com.stoles.inventory.repository.WorkerRepository;
import com.stoles.inventory.repository.WorkTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Shared setup for full-stack controller integration tests: real Spring
 * context (controller, service, repository, security filter chain, JWT
 * auth) against the isolated in-memory H2 database (application-test.properties).
 *
 * Spring caches a single ApplicationContext (and therefore a single
 * in-memory H2 database) across all subclasses in a test run, since they
 * share the same configuration. That means data created by one test class
 * is still there when the next class starts. {@link #cleanDatabase()}
 * clears every business table in FK-safe order (dispatches and payments
 * reference stock/workers/work-types, so they go first) and should be
 * called from each subclass's @BeforeEach. It deliberately leaves
 * app_users alone: every other entity's created_by/received_by is an
 * ON DELETE RESTRICT FK to AppUser, so deleting seeded/created users is
 * unsafe once any other data references them.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DispatchRepository dispatchRepository;

    @Autowired
    protected PaymentRepository paymentRepository;

    @Autowired
    protected StockItemRepository stockItemRepository;

    @Autowired
    protected WorkerRepository workerRepository;

    @Autowired
    protected WorkTypeRepository workTypeRepository;

    protected void cleanDatabase() {
        dispatchRepository.deleteAll();
        paymentRepository.deleteAll();
        stockItemRepository.deleteAll();
        workerRepository.deleteAll();
        workTypeRepository.deleteAll();
    }

    protected String login(String username, String password) throws Exception {
        Dtos.LoginRequest req = Dtos.LoginRequest.builder()
                .username(username).password(password).build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        Dtos.AuthResponse resp = objectMapper.readValue(
                result.getResponse().getContentAsString(), Dtos.AuthResponse.class);
        return resp.getToken();
    }
}
