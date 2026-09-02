# AI Usage Disclosure

## Tools used

ChatGPt was used as a coding assistant while completing this exercise.

## What AI was used for

It assisted with reviewing the starter project, suggesting a simple controller-service-repository structure, validation and status-lifecycle ideas, integration tests, error handling.

## Significant suggestions and review

The implementation uses `BigDecimal` for amounts, enums for controlled values, H2 through the existing configuration, a unique transaction ID, and a small terminal-status lifecycle. The generated work was reviewed against the starter project's existing files and challenge requirements. The sample application class, sample endpoint, configuration, Maven configuration, and original context-load test were retained.

## Assumptions and verification

No assigned candidate variant was found in the repository. Currency, type, and maximum-amount values are documented as assumptions and should be replaced if the assignment provides different values. The existing test suite was run before implementation, new integration tests were added, and the complete Maven test suite was run after implementation.
