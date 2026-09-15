# Backend

This is the Java side of Freshprint.

Right now, it has the main business models and a few tests. There is no Spring app, API, database, or server to start. That is intentional: this part is all about the business logic.

## Folders

```text
src/main/java/com/freshprint/
├── domain/
│   ├── engagement/   Is an engagement current or behind?
│   ├── template/     What template and changes are we looking at?
│   └── summary/      How do we show those changes to a person?
└── application/
    └── port/         How the future services will ask for template data
```

The update checker and summary strategies are coming next.

## Run the tests

You need Java 26, but you do not need to install Maven.

```shell
./mvnw clean test
```

The current tests cover the three update statuses, invalid version combinations, raw diff operations, and immutable lists.

The bigger picture is in [DESIGN.md](../DESIGN.md).
