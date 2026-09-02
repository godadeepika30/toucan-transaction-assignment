# Customer Transaction Service

A Spring Boot REST service for creating, retrieving, updating, and listing customer transactions. It uses the starter project's embedded H2 database. The original sample endpoint remains available at `GET /api/sample`.

## Assumptions and validation

No candidate-specific variant was included in this repository. The following values are therefore configurable assumptions that must be checked against the assigned variant: supported currencies are `INR`, `USD`, and `EUR`; transaction types are `PAYMENT`, `REFUND`, and `TRANSFER`; and the maximum amount is `1,000,000.00`.

- Transaction and customer IDs are required, 40 characters or fewer, and contain only letters, digits, `-`, or `_`.
- Amount is required, uses `BigDecimal`, is at least `0.01`, and at most `1,000,000.00`.
- Currency, type, and status are required enum values.
- A transaction ID is unique. New transactions must start as `PENDING`.

## API

| Method | Endpoint | Purpose | Main outcomes |
| --- | --- | --- | --- |
| POST | `/api/transactions` | Create a transaction | `201`, `400`, `409` |
| GET | `/api/transactions/{transactionId}` | Get one transaction | `200`, `404` |
| PATCH | `/api/transactions/{transactionId}/status` | Change its status | `200`, `400`, `404`, `409` |
| GET | `/api/customers/{customerId}/transactions` | List a customer's transactions | `200` (an empty list if none) |

Example create body:

```json
{"transactionId":"TXN-100","customerId":"CUST-20","amount":125.50,"currency":"INR","transactionType":"PAYMENT","status":"PENDING"}
```

Status update body: `{"status":"COMPLETED"}`. Errors use `{ "status": 400, "message": "..." }`.

## Status transitions

Only `PENDING → COMPLETED`, `PENDING → FAILED`, and `PENDING → CANCELLED` are allowed. Each destination is terminal, preventing a completed payment from later being changed to failed or cancelled. Repeating `PENDING` is also rejected.

## Testing

The integration tests cover successful persistence, invalid negative amounts, duplicate IDs, a missing transaction, status-transition enforcement, and customer isolation. Run on Windows with `mvnw.cmd clean test` (or `./mvnw clean test` on Linux/macOS).

## Limitations and future work

H2 is embedded for this exercise; there is no authentication, authorization, pagination, audit trail, external payment gateway, or distributed locking. A production version could add those concerns, API documentation, observability, and a production database.
