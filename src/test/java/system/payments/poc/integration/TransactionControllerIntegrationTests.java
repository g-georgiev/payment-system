package system.payments.poc.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import system.payments.poc.dto.AuthDTO;
import system.payments.poc.dto.TransactionInputDto;
import system.payments.poc.dto.TransactionOutputDto;
import system.payments.poc.enums.TransactionType;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class TransactionControllerIntegrationTests {
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            return Jackson2ObjectMapperBuilder.json()
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .modules(new JavaTimeModule())
                    .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                    .build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource dataSource;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "testpassword";

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Insert test user
        jdbcTemplate.update(
                "INSERT INTO user_credentials (username, password, role, status, total_transaction_sum) VALUES (?, ?, ?, ?, ?) ON CONFLICT (username) DO NOTHING",
                TEST_USERNAME, new BCryptPasswordEncoder().encode(TEST_PASSWORD), "merchant", "ACTIVE", 0
        );
    }

    private String jwtToken;

    @BeforeEach
    void getJwtToken() throws Exception {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(TEST_USERNAME);
        authDTO.setPassword(TEST_PASSWORD);
        MvcResult result = mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        jwtToken = result.getResponse().getContentAsString();
    }

    @Test
    void testCreateTransaction() throws Exception {
        // First, create an AUTHORIZE transaction
        TransactionInputDto authorizeDto = TransactionInputDto.builder()
                .transactionType(TransactionType.AUTHORIZE)
                .amount(BigDecimal.valueOf(100.00))
                .customerEmail("test@example.com")
                .customerPhone("1234567890").build();

        MvcResult authorizeResult = mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.transactionType").value("AuthorizeTransaction"))
                .andReturn();

        TransactionOutputDto authorizeOutput = objectMapper.readValue(authorizeResult.getResponse().getContentAsString(), TransactionOutputDto.class);

        // Now, create a CHARGE transaction referencing the AUTHORIZE transaction
        TransactionInputDto chargeDto = TransactionInputDto.builder()
                .transactionType(TransactionType.CHARGE)
                .referenceId(authorizeOutput.getUuid()).build();

        MvcResult chargeResult = mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chargeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.transactionType").value("ChargeTransaction"))
                .andExpect(jsonPath("$.referenceTransactionUuid").value(authorizeOutput.getUuid().toString()))
                .andReturn();

        TransactionOutputDto chargeOutput = objectMapper.readValue(chargeResult.getResponse().getContentAsString(), TransactionOutputDto.class);

        // Now, create a REFUND transaction referencing the CHARGE transaction
        TransactionInputDto refundDto = TransactionInputDto.builder()
                .transactionType(TransactionType.REFUND)
                .referenceId(chargeOutput.getUuid()).build();

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refundDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.transactionType").value("RefundTransaction"))
                .andExpect(jsonPath("$.referenceTransactionUuid").value(chargeOutput.getUuid().toString()));

        // Now, create a REVERSAL transaction referencing the AUTHORIZE transaction
        TransactionInputDto reversalDto = TransactionInputDto.builder()
                .transactionType(TransactionType.REVERSAL)
                .referenceId(authorizeOutput.getUuid()).build();

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reversalDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.transactionType").value("ReversalTransaction"))
                .andExpect(jsonPath("$.referenceTransactionUuid").value(authorizeOutput.getUuid().toString()));

        // Now, create a CHARGE transaction referencing the reversed AUTHORIZE
        TransactionInputDto errorChargeDto = TransactionInputDto.builder()
                .transactionType(TransactionType.CHARGE)
                .referenceId(authorizeOutput.getUuid()).build();

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(errorChargeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.transactionType").value("ChargeTransaction"))
                .andExpect(jsonPath("$.referenceTransactionUuid").value(authorizeOutput.getUuid().toString()));

        // Now, try to create a REFUND transaction referencing the ERROR CHARGE transaction
        TransactionInputDto errorRefundDto = TransactionInputDto.builder()
                .transactionType(TransactionType.REFUND)
                .referenceId(chargeOutput.getUuid()).build();

        mockMvc.perform(post("/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(errorRefundDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.transactionType").value("RefundTransaction"))
                .andExpect(jsonPath("$.referenceTransactionUuid").value(chargeOutput.getUuid().toString()));
    }
}