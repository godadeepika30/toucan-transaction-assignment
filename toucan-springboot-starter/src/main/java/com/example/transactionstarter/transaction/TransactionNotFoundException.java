package com.example.transactionstarter.transaction;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String transactionId) {
        super("Transaction '" + transactionId + "' was not found");
    }
}
