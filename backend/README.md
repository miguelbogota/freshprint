# Backend

The Spring Boot server turns the original Java logic into a running API. It keeps engagement metadata in a small local H2 database, loads shared template fixtures at startup, and never opens full engagement files on a list request.

- `model/{engagement,template,summary}` holds the small domain values from the original take-home.
- `service/{update,summary,decision}` holds the rules and workflows. `service/port` keeps the template lookup interfaces.
- `repository/` reads the fixture templates and stores indexed engagement metadata and operations.
- `controller/` exposes the HTTP endpoints, while `dto/` defines their response and decision shapes.
- `config/` wires local Spring settings; `exception/` formats expected API errors.

Tests mirror the relevant Java packages. `testfixture/` contains only shared test helpers. Spring scans everything below `com.freshprint`, starting at `FreshprintApplication`.

There are no `static/` or `templates/` resource folders because this server returns JSON; Angular owns the HTML. The resources this server actually needs are `application.properties` and `schema.sql`.

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
