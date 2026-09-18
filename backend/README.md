# Backend

The Spring Boot server turns the original Java logic into a running API. It keeps engagement metadata in a small local H2 database, loads shared template fixtures at startup, and never opens full engagement files on a list request.

- `domain/` and `application/` are the original version-checking and summary rules.
- `infrastructure/` loads fixture templates/diffs and reads the engagement metadata index.
- `api/` exposes the list, detail, decision, and operation-status endpoints.

The server prefers a direct baseline-to-latest diff. If the fixture only has adjacent versions, it combines them by JSON path into the effective change. Summaries are cached by template and version pair in memory for this demo. H2 keeps engagement baselines and decision operations across restarts.

## Run

With Java 26:

```shell
./mvnw test
./mvnw spring-boot:run
```

The API runs at `http://localhost:8080`. Useful endpoints:

```text
GET  /api/engagements/template-updates
GET  /api/engagements/{id}/template-update
POST /api/engagements/{id}/template-update-decisions
GET  /api/template-update-operations/{operationId}
```

The POST body is `{"decision":"APPLY","expectedBaselineVersion":6,"targetVersion":8}` (or `DECLINE`). It rejects stale versions with `409`, returns an `ACCEPTED` operation ID quickly, and processes the decision asynchronously. Check the operation endpoint for `SUCCEEDED` or `FAILED`.

This is a local portfolio demo, not a production engagement service: the fixtures are bundled, the H2 index represents one demo workspace, and auth, tenant isolation, durable hooks, and full-file template merging are not implemented. Keep the server local.
