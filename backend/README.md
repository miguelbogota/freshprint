# Backend

This is the plain Java part of Freshprint. There is no Spring app, API, database, or server to start; the take-home asks for the business logic, not a running service.

The flow is short:

1. `EngagementUpdateEvaluator` compares an engagement's recorded template version with the latest published template. It returns `CURRENT`, `PENDING`, or `UNKNOWN` and counts the actual newer versions.
2. `PendingUpdateSummaryService` asks for one diff from the engagement's version straight to the latest version. If that diff is missing or mismatched, the update stays pending but its summary is `UNAVAILABLE`.
3. `ChangeSummaryGenerator` groups changes by section and writes short descriptions. An unfamiliar path is kept in an "Other changes" group and marked for closer review. The current fallback can show a technical path; a production version would need better wording for those paths.

The `domain` folders hold engagement, template, and summary values. The `application` folders hold the update and summary logic. Small `port` interfaces let that logic ask for template data without knowing where it is stored. Records hold immutable data; classes do the work.

The tests read JSON from [../fixtures](../fixtures/README.md). A **test-only** `FixtureDiffProvider` chooses a file from the template ID and version pair. Missing direct fixture files return no diff; a real system could generate that diff from stored template versions. `COMPUTING` is represented in the model for the future async cache flow, but no cache is built here.

## Test it

Use Java 26. You do not need to install Maven:

```shell
./mvnw clean test
```

The three focused tests cover current and unknown states, accumulated and skipped versions, readable changes, missing diffs, and mismatched provider data. The wider architecture is in [../DESIGN.md](../DESIGN.md).
