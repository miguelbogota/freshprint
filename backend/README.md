# Backend

This Spring Boot app powers the Angular dashboard. It reads template fixtures, builds readable update summaries, and stores lightweight engagement metadata and decision operations with Spring Data JPA/Hibernate in a local H2 database. A list request never opens full engagement files.

- `model/{engagement,template,summary}` holds the small domain values from the original take-home. The two JPA entity classes in `model/engagement` map only the indexed engagement and operation tables.
- `service/{update,summary,decision}` holds the rules and workflows. `service/port` keeps the template lookup interfaces.
- `repository/` reads fixture templates and uses Spring Data JPA interfaces for the indexed engagement and operation tables. There is no application-level `JdbcTemplate` code.
- `controller/` exposes the HTTP endpoints, while `dto/` defines their response and decision shapes.
- `config/` wires local Spring settings; `exception/` formats expected API errors.

Tests mirror the relevant Java packages. `testfixture/` contains only shared test helpers. Spring scans everything below `com.freshprint`, starting at `FreshprintApplication`.

There are no `static/` or `templates/` resource folders because this server returns JSON; Angular owns the HTML. The resources this server actually needs are `application.properties` and `schema.sql`.

The server prefers a direct baseline-to-latest diff. If the fixture only has adjacent versions, it combines them by JSON path into the effective change. Summaries are cached by template and version pair in memory for this demo. H2 keeps engagement baselines and decision operations across restarts. `schema.sql` defines the local demo tables; Hibernate validates the mapping instead of changing that schema on startup.

The database stores only the small engagement index (ID, template, baseline version, and declined target) and decision-operation history. It does not store full engagement files or customer-entered form values. A decision is first saved as `ACCEPTED`, then processed in the background. Applying the baseline change and marking its operation `SUCCEEDED` happen in one transaction, so a failure cannot leave one committed without the other. A stale baseline cannot overwrite a newer one. On startup, unfinished operations become `FAILED` rather than pretending they succeeded.

This setup fits a single-instance local demo. For production, use a migration tool such as Flyway instead of re-running `schema.sql`, a managed database instead of local H2, and a durable job queue for decisions. The in-process duplicate check also needs a database-enforced concurrency rule if several server instances can accept decisions at once. Tests cover API errors, multi-version summaries, stale writes, operation persistence, restart recovery, and transaction rollback.

## Run

With Java 26, from the repository root:

```shell
cd backend
./mvnw spring-boot:run
```

Leave it running, then start Angular using the steps in the [root README](../README.md). To run backend tests separately from `backend/`, use `./mvnw verify`.

The API runs at `http://localhost:8080`. Useful endpoints:

```text
GET  /api/engagements/template-updates
GET  /api/engagements/{id}/template-update
POST /api/engagements/{id}/template-update-decisions
GET  /api/template-update-operations/{operationId}
```

The POST body is `{"decision":"APPLY","expectedBaselineVersion":6,"targetVersion":8}` (or `DECLINE`). It rejects stale versions with `409`, returns an `ACCEPTED` operation ID quickly, and processes the decision asynchronously. Angular polls the operation endpoint for `SUCCEEDED` or `FAILED`.

This is a local portfolio demo, not a production engagement service: the fixtures are bundled, the H2 index represents one demo workspace, and auth, tenant isolation, durable hooks, and full-file template merging are not implemented. Keep the server local.
