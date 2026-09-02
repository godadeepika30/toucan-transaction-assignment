package com.example.transactionstarter.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TransactionRepository repository;

    @BeforeEach
    void clearDatabase() { repository.deleteAll(); }

    @Test
    void createsAndPersistsValidTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(json(request("TXN-1", "CUST-1", "25.50"))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.transactionId").value("TXN-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
        org.assertj.core.api.Assertions.assertThat(repository.findByTransactionId("TXN-1")).isPresent();
    }

    @Test
    void rejectsNegativeAmount() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(json(request("TXN-2", "CUST-1", "-1.00"))))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Amount must be positive"));
    }

    @Test
    void rejectsDuplicateTransactionId() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(json(request("TXN-3", "CUST-1", "10.00")))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(json(request("TXN-3", "CUST-2", "11.00"))))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Transaction ID already exists"));
        org.assertj.core.api.Assertions.assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void returnsNotFoundForMissingTransaction() throws Exception {
        mockMvc.perform(get("/api/transactions/MISSING")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transaction 'MISSING' was not found"));
    }

    @Test
    void rejectsInvalidTransactionIdInPath() throws Exception {
        mockMvc.perform(get("/api/transactions/{transactionId}", "invalid id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Transaction ID may contain only letters, numbers, hyphens, and underscores"));
    }

    @Test
    void updatesPendingStatusButRejectsTerminalTransition() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(json(request("TXN-4", "CUST-1", "10.00")))).andExpect(status().isCreated());
        mockMvc.perform(patch("/api/transactions/TXN-4/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"));
        mockMvc.perform(patch("/api/transactions/TXN-4/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"FAILED\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsOnlyRequestedCustomersTransactions() throws Exception {
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(json(request("TXN-5", "CUST-5", "10.00")))).andExpect(status().isCreated());
        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(json(request("TXN-6", "CUST-OTHER", "10.00")))).andExpect(status().isCreated());
        mockMvc.perform(get("/api/customers/CUST-5/transactions")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].transactionId").value("TXN-5"));
    }

    private CreateTransactionRequest request(String transactionId, String customerId, String amount) {
        return new CreateTransactionRequest(transactionId, customerId, new BigDecimal(amount), Currency.INR,
                TransactionType.PAYMENT, TransactionStatus.PENDING);
    }

    private String json(Object value) throws Exception { return objectMapper.writeValueAsString(value); }
}
