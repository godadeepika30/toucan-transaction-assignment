package com.example.transactionstarter.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService service;

    public TransactionController(TransactionService service) { this.service = service; }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) { return service.create(request); }

    @GetMapping("/transactions/{transactionId}")
    public TransactionResponse get(@PathVariable @NotBlank(message = "Transaction ID is required")
                                   @Size(max = 40, message = "Transaction ID must be at most 40 characters")
                                   @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Transaction ID may contain only letters, numbers, hyphens, and underscores") String transactionId) {
        return service.get(transactionId);
    }

    @PatchMapping("/transactions/{transactionId}/status")
    public TransactionResponse updateStatus(@PathVariable @NotBlank(message = "Transaction ID is required")
                                            @Size(max = 40, message = "Transaction ID must be at most 40 characters")
                                            @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Transaction ID may contain only letters, numbers, hyphens, and underscores") String transactionId,
                                            @Valid @RequestBody UpdateStatusRequest request) {
        return service.updateStatus(transactionId, request.status());
    }

    @GetMapping("/customers/{customerId}/transactions")
    public List<TransactionResponse> getForCustomer(@PathVariable @NotBlank(message = "Customer ID is required")
                                                     @Size(max = 40, message = "Customer ID must be at most 40 characters")
                                                     @Pattern(regexp = "[A-Za-z0-9_-]+", message = "Customer ID may contain only letters, numbers, hyphens, and underscores") String customerId) {
        return service.getForCustomer(customerId);
    }
}
