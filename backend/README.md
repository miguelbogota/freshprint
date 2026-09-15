# Freshprint Java Domain

This module contains the framework-independent Java domain models, interfaces, and core logic for evaluating pending template updates and producing human-readable change summaries.

It intentionally has no application framework, HTTP layer, controller, persistence adapter, or runnable service. Those production concerns are described in `DESIGN.md` but are outside the targeted implementation.

## Requirements

- Java 25
- No global Maven installation is required

## Run the tests

```shell
./mvnw test
```
