# Freshprint

> **Note**
>
> Want to see the full thing working? Check out the [`alternative-approach`](../../tree/alternative-approach) branch. It contains a complete implementation with the frontend and backend wired together, built as an additional exploration beyond the scope of the take-home.


Freshprint is a small take-home demo for a simple question: a template changed after a working file was created, so which files need attention, and what changed?

An _engagement_ is the firm's working file. A _product template_ is the starting blueprint for that file. Users can review a newer template and choose Apply or Decline. This project shows the decision flow; it does **not** merge template content into an engagement.

Opening one full engagement takes about a minute. The [design](DESIGN.md) therefore proposes a small metadata index for fast update lists. The index, HTTP API, hooks, and cache are design ideas, not running infrastructure in this repo.

## What's here

- [DESIGN.md](DESIGN.md) - the architecture, JSON contract, tests, operations, and tradeoffs.
- [backend](backend/README.md) - plain Java code that checks versions and turns diffs into readable summaries.
- [frontend](frontend/README.md) - an unstyled Angular screen with hardcoded data and Apply/Decline interactions.
- [fixtures](fixtures/README.md) - sample templates, engagements, and JSON diffs.
- [SUBMISSION.md](SUBMISSION.md) - assumptions, AI usage, time spent, and next steps.

Java and Angular are separate excerpts. They use the same proposed update and decision shapes but do not call each other. A production API adapter would also handle fields missing from older, unindexed files.

## Try it

Java needs Java 26. The Maven wrapper is included:

```shell
cd backend
./mvnw clean test
```

Angular 22 needs a supported Node version (24.15+ or 26+):

```shell
cd frontend
npm install
npm test -- --watch=false
npm run build
npm start
```

## Take-home checklist

- [x] Design: architecture and client/server split, plan, testing, observability, failure modes, tradeoffs, and JSON API contract.
- [x] Java: pending-update model, accumulated versions, readable server-side summaries, and three focused tests. No framework or HTTP layer.
- [x] Angular: update list, summary review, Apply/Decline intent, hardcoded fixture, and two focused tests. No HTTP calls or CSS.
- [x] Submission notes: assumptions, AI use, corrections, limits, and next steps.
- [x] [SUBMISSION.md](SUBMISSION.md) now records the approximate active work time and the longer overnight elapsed time.
- [ ] Before sending: make sure you can explain the choices in a live review.

The exercise favors a small, coherent example over a full application. That is the point of this repo.
