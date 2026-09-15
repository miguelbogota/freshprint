# Frontend

This is the small Angular side of Freshprint. It shows which engagements have a template update, lets someone read the changes, and offers Apply or Decline. It uses hardcoded data shaped like the JSON contract in `../DESIGN.md`; it does not call the Java code or an API.

There is no CSS on purpose. The take-home asks for state and data flow, not styling.

## How it works

- `update.model.ts` defines the response and decision shapes used by the screen.
- `update.fixture.ts` is sample data: a pending update with a readable summary, a summary still computing, a current engagement, and an unknown one.
- `UpdateStore` owns selection and decision state. It sends the engagement ID plus the exact baseline and target versions the user reviewed.
- `UpdateList` shows each engagement's state; `UpdateDetail` shows the selected summary and emits Apply or Decline.
- `DecisionGateway` stands in for the decision endpoint and returns `ACCEPTED`. It does not actually apply the update.

Apply and Decline stay disabled until a pending engagement has an available summary. This is a client-side safety choice for the demo, not a production policy. A real server would also check the versions and return a conflict if they changed during review.

## Run it

Use a Node version supported by Angular 22 (Node 24.15+ or 26+), then:

```shell
npm install
npm test -- --watch=false
npm run build
npm start
```

The two tests check the screen's summary/disabled-button behavior and that a decision sends the reviewed versions only once while it is in flight.
