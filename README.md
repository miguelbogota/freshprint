# Freshprint

Freshprint is a small full-stack demo for a simple question: a template changed after a working file was created, so which files need attention, and what changed?

An _engagement_ is the firm's working file. A _product template_ is the starting blueprint for that file. Users can review a newer template and choose Apply or Decline. This project shows the decision flow; it does **not** merge template content into an engagement.

Opening one full engagement takes about a minute. The server therefore keeps a small metadata index for fast update lists. It never opens a full engagement to serve the dashboard.

## What's here

- [DESIGN.md](DESIGN.md) - the architecture, JSON contract, tests, operations, and tradeoffs.
- [backend](backend/README.md) - Spring Boot API, Spring Data JPA/H2 metadata index, fixture-backed template diffs, summaries, and asynchronous decisions.
- [frontend](frontend/README.md) - Angular dashboard with live API calls, search, filtering, responsive styling, and decisions.
- [fixtures](fixtures/README.md) - sample templates, engagements, and JSON diffs.
- [SUBMISSION.md](SUBMISSION.md) - assumptions, AI usage, time spent, and next steps.

This `alternative-approach` branch builds the connected app on top of the original take-home logic. [DESIGN.md](DESIGN.md) and [SUBMISSION.md](SUBMISSION.md) describe the original scoped submission, not a claim that this branch is production-ready.

## Try it

Start the Java server first (Java 26; Maven wrapper included):

```shell
cd backend
./mvnw test
./mvnw spring-boot:run
```

In another terminal, start Angular 22 (Node 24.15+ or 26+):

```shell
cd frontend
npm install
npm test -- --watch=false
npm run build
npm start
```

Open `http://localhost:4200`. Angular proxies `/api` to the Java server on port 8080. The server seeds twelve demo engagements from JSON on first run; its H2 data lives in ignored `backend/data/`.

Apply and Decline return a quick receipt. Angular checks the operation status and refreshes the list when it finishes. Apply advances the recorded template version; Decline keeps that version and hides the same offer until a newer template is published. The app does **not** merge template fields into an engagement file.

This remains a local demo: templates come from bundled fixtures, and it has no authentication, firm isolation, real hook consumer, or full-file merge. Do not expose its API publicly without those pieces.

## Take-home checklist

- [x] Design: architecture and client/server split, plan, testing, observability, failure modes, tradeoffs, and JSON API contract.
- [x] Java: original domain rules plus Spring Boot HTTP API, H2 metadata index, async decisions, and unit/integration tests.
- [x] Angular: live update list, summary review, Apply/Decline flow, API polling, responsive styles, and focused tests.
- [x] Submission notes: assumptions, AI use, corrections, limits, and next steps.
- [x] [SUBMISSION.md](SUBMISSION.md) now records the approximate active work time and the longer overnight elapsed time.
- [ ] Before sending: make sure you can explain the choices in a live review.

The original take-home rules are preserved in the backend's `model` and `service` packages; this branch explores the fuller product around them.
