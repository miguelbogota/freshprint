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

## Run the tests

You need Java 26, but you do not need to install Maven.

```shell
./mvnw clean test
```

The current tests cover update evaluation, multiple pending versions, readable summaries, unknown paths, invalid version combinations, raw diff operations, and immutable lists.

The bigger picture is in [DESIGN.md](../DESIGN.md).
