package com.example.transactionstarter.transaction;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank(message = "Transaction ID is required")
        @Size(max = 40, message = "Transaction ID must be at most 40 characters")
        @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Transaction ID may contain only letters, numbers, hyphens, and underscores")
        String transactionId,
        @NotBlank(message = "Customer ID is required")
        @Size(max = 40, message = "Customer ID must be at most 40 characters")
        @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Customer ID may contain only letters, numbers, hyphens, and underscores")
        String customerId,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        @DecimalMax(value = "1000000.00", message = "Amount exceeds the permitted maximum of 1000000.00")
        BigDecimal amount,
        @NotNull(message = "Currency is required") Currency currency,
        @NotNull(message = "Transaction type is required") TransactionType transactionType,
        @NotNull(message = "Transaction status is required") TransactionStatus status) {
}
