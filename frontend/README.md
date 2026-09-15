# Frontend

This is the small Angular side of Freshprint. It lets someone spot a template update, read the changes, and choose Apply or Decline. It uses a hardcoded response-shaped fixture; it makes no HTTP calls and does not talk to the Java excerpt.

There are only a few moving parts:

- `update.model.ts` describes the proposed JSON response and decision request.
- `update.fixture.ts` gives the screen examples of pending, current, unknown, and still-computing information.
- `UpdateStore` holds the selected engagement and decision state. Its `canDecide` rule enables actions only for a pending update with a ready summary. That is a demo safety choice, not a rule imposed by the exercise.
- `UpdateList` shows the files; `UpdateDetail` shows the already-readable summary and emits Apply or Decline.
- `DecisionGateway` returns a fake `ACCEPTED` receipt. It does **not** apply a template. The store sends the exact baseline and target versions the user reviewed, so a future server can reject a stale decision.

The screen uses plain labels instead of technical status codes. It does not interpret raw JSON diffs; that belongs to Java. There is no CSS, filtering, sorting, search, authentication, or bulk action work because the take-home does not ask for it.

## Run it

Use Node 24.15+ or 26+:

```shell
npm install
npm test -- --watch=false
npm run build
npm start
```

The two focused tests check summary display, disabled actions while a summary is still being prepared, versioned Apply/Decline requests, and duplicate-click protection. The intended API is in [../DESIGN.md](../DESIGN.md).
