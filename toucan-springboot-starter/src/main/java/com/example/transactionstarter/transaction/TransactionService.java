package com.example.transactionstarter.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) { this.repository = repository; }

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request) {
        if (request.status() != TransactionStatus.PENDING) {
            throw new BadRequestException("New transactions must have PENDING status");
        }
        if (repository.existsByTransactionId(request.transactionId())) {
            throw new ConflictException("Transaction ID already exists");
        }
        Transaction transaction = new Transaction(request.transactionId(), request.customerId(), request.amount(),
                request.currency(), request.transactionType(), request.status());
        return TransactionResponse.from(repository.save(transaction));
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(String transactionId) {
        return TransactionResponse.from(find(transactionId));
    }

    @Transactional
    public TransactionResponse updateStatus(String transactionId, TransactionStatus requestedStatus) {
        Transaction transaction = find(transactionId);
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new ConflictException("Status transition is not permitted from " + transaction.getStatus());
        }
        if (requestedStatus == TransactionStatus.PENDING) {
            throw new ConflictException("Status transition from PENDING to PENDING is not permitted");
        }
        transaction.setStatus(requestedStatus);
        return TransactionResponse.from(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getForCustomer(String customerId) {
        return repository.findAllByCustomerId(customerId).stream().map(TransactionResponse::from).toList();
    }

    private Transaction find(String transactionId) {
        return repository.findByTransactionId(transactionId).orElseThrow(() -> new TransactionNotFoundException(transactionId));
    }
}
