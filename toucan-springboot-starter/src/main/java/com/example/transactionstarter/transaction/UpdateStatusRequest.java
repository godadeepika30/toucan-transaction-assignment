package com.example.transactionstarter.transaction;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull(message = "Transaction status is required") TransactionStatus status) {
}
