# Frontend

This is the small Angular side of Freshprint. It shows which engagements have a template update, lets someone read the changes, and offers Apply or Decline. It uses hardcoded data shaped like the JSON contract in `../DESIGN.md`; it does not call the Java code or an API.

There is no CSS on purpose. The take-home asks for state and data flow, not styling.

## How it works

- `update.model.ts` defines the response and decision shapes used by the screen, including summary availability and freshness.
- `update.fixture.ts` is sample data: a pending update with a readable summary, a summary still computing, a current engagement, and an unknown one.
- `UpdateStore` owns selection and decision state. Its computed `canDecide` rule keeps the buttons and action guard in sync. It sends the engagement ID plus the exact baseline and target versions the user reviewed.
- `UpdateList` shows each engagement's state; `UpdateDetail` shows the selected summary and emits Apply or Decline.
- `DecisionGateway` stands in for the decision endpoint and returns `ACCEPTED`. It does not actually apply the update.

Apply and Decline stay disabled until a pending engagement has an available summary. This is a client-side safety choice for the demo, not a production policy. A real server would also check the versions and return a conflict if they changed during review.

The list translates status codes into simple labels. The detail shows the server-written change descriptions without interpreting raw JSON diffs. Technical reason codes remain in the fixture/contract, but the screen gives users plain-language messages. `ACCEPTED` does not change an engagement to `CURRENT` because the actual update may still be running.

## Run it

Use a Node version supported by Angular 22 (Node 24.15+ or 26+), then:

```shell
npm install
npm test -- --watch=false
npm run build
npm start
```

The two tests check summary display, stale/unknown information, disabled buttons while a summary computes, Apply and Decline payloads, and duplicate-click protection.

## Take-home scope check

- Real Angular components with a clear boundary: list selects, detail reviews, store handles state and actions.
- Hardcoded response-shaped fixture; no HTTP calls or backend connection.
- Pending, current, unknown, computing, and stale information are represented.
- Readable summary comes from the fixture as though Java had generated it; Angular does not transform raw diffs.
- Apply and Decline initiate a version-checked request; neither action merges template content.
- Two focused tests and no CSS, filtering, sorting, search, authentication, or bulk actions.
