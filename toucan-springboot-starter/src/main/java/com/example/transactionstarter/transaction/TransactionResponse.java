package com.example.transactionstarter.transaction;

import java.math.BigDecimal;

public record TransactionResponse(String transactionId, String customerId, BigDecimal amount, Currency currency,
                                  TransactionType transactionType, TransactionStatus status) {
    static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(transaction.getTransactionId(), transaction.getCustomerId(), transaction.getAmount(),
                transaction.getCurrency(), transaction.getTransactionType(), transaction.getStatus());
    }
}
