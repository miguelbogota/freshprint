# Backend

This is the Java side of Freshprint.

It has the main business models, the update checker, the change-summary strategies, and a few focused tests. There is no Spring app, API, database, or server to start. That is intentional: this part is all about the business logic.

## Folders

```text
src/main/java/com/freshprint/
├── domain/
│   ├── engagement/   Is an engagement current or behind?
│   ├── template/     What template and changes are we looking at?
│   └── summary/      How do we show those changes to a person?
└── application/
    ├── port/         How the services ask for template data
    ├── update/       Checks if an engagement needs an update
    └── summary/      Turns raw changes into simple summaries
```

The summary code uses small strategies for question changes, general section changes, and anything it does not recognize. The fallback keeps unknown changes visible and marks them for review.

## What happens

1. `EngagementUpdateEvaluator` asks `TemplateCatalog` for the latest template.
2. It returns `CURRENT`, `PENDING`, or `UNKNOWN`.
3. A pending result includes the real number of published versions after the engagement's baseline.
4. `PendingUpdateSummaryService` asks `TemplateDiffProvider` for one direct baseline-to-latest diff.
5. `ChangeSummaryGenerator` runs each raw change through the first strategy that understands it.
6. The final changes are grouped by section and returned as `AVAILABLE`.
7. If the diff cannot be found, the update stays pending and the summary is `UNAVAILABLE`.

`COMPUTING` is also part of the model for the future async cache flow described in the API contract.

## Why it's built this way

- Records hold immutable business data. Classes perform work.
- Sealed interfaces keep the possible update and summary states explicit.
- Ports keep the logic independent from JSON, databases, and caches.
- The Strategy pattern lets us add better wording for new template areas without changing the generator.
- The fallback strategy makes sure an unknown path is never silently dropped.
- Published versions are stored as a list instead of assuming version numbers are always consecutive.
- The direct baseline-to-latest diff handles several accumulated updates without showing noisy intermediate changes.
- `Clock` is injected so generated timestamps stay predictable in tests.
- Jackson is test-only. Production code stays plain Java.

The tests load the shared files in `../fixtures`, so they exercise the same sample data used by the rest of the project.

## Run the tests

You need Java 26, but you do not need to install Maven.

```shell
./mvnw clean test
```

The current tests cover update evaluation, missing metadata, skipped version numbers, multiple pending versions, direct multi-version diffs, readable summaries, unavailable summaries, unknown paths, invalid version combinations, raw diff operations, and immutable lists.

The bigger picture is in [DESIGN.md](../DESIGN.md).
