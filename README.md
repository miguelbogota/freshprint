# Freshprint

Freshprint helps firm staff answer a simple question: a product template changed, so which engagement files need attention, and what actually changed?

An *engagement* is a firm's working file. A *product template* is its starting blueprint. Staff can review a plain-language summary and choose Apply or Decline. This app demonstrates that decision flow; it does **not** merge template content into a full engagement file.

Opening a full engagement can take about a minute, so the dashboard reads a small metadata index instead. The backend compares template versions and sends the summary to Angular. The frontend and backend are connected through HTTP; the JSON fixture in the frontend is only for tests.

## Run both apps

You'll need Java 26, Node 24.15+ or 26+, and npm. Start from the repository root and leave each command running in its own terminal.

Terminal 1 — start the Spring Boot API:

```shell
cd backend
./mvnw spring-boot:run
```

Terminal 2 — start Angular:

```shell
cd frontend
npm ci
npm start
```

Open [http://localhost:4200](http://localhost:4200). Angular forwards `/api` requests to Spring Boot on port 8080 through its dev proxy. Start the backend first so the dashboard can load data right away.

On the first run, the backend seeds twelve demo engagements from the JSON fixtures. The local H2 database lives in ignored `backend/data/`, so decisions can survive an app restart. Apply and Decline return an `ACCEPTED` receipt quickly; Angular checks the operation's result and refreshes the list after success.

## Check it

Run these separately from the repository root:

```shell
cd backend
./mvnw verify
```

```shell
cd frontend
npm test -- --watch=false
npm run build
```

## What's in the repo

- [backend](backend/README.md) — Spring Boot API, JPA/H2 metadata index, summaries, and decision operations.
- [frontend](frontend/README.md) — lazy-loaded Angular dashboard with scoped SCSS and live API calls.
- [fixtures](fixtures/README.md) — sample engagements, templates, and template diffs.
- [PRODUCT.md](PRODUCT.md) — who Freshprint is for and what the demo is meant to do.
- [DESIGN.md](DESIGN.md) and [SUBMISSION.md](SUBMISSION.md) — the original take-home design and submission notes. They describe the scoped exercise; this branch goes further by connecting the apps.

This is still a local portfolio demo. It has no authentication, firm isolation, durable hook consumer, or full-file template merge. Please don't expose the API publicly as-is.
