# Frontend

The Angular app is a live dashboard for the Java API. It shows which engagements need review, lets you search and filter them, reads the server's plain-language change summary, and sends versioned Apply or Decline decisions. It polls the accepted operation until the server reports the final result.

The root `AppComponent` contains only the router outlet. The dashboard lives in `src/app/features/dashboard/` and loads through its own route. Its `data-access/` folder holds the update types, state store, and test fixture; `components/` holds the list and detail views. The HTTP `DecisionGateway` lives in `src/app/core/services/` because it is a root-provided API service. Each folder exposes its public pieces through an `index.ts` file. There are no empty auth, guard, or shared folders yet—those can be added when the app needs them.

`UpdateStore` owns loading, selection, and decision state. `DecisionGateway` is the only HTTP boundary. `UpdateListComponent` and `UpdateDetailComponent` handle display and user intent. The fixture remains for isolated UI tests; the running app gets its data from the server. Each component owns its nested SCSS; the small `src/styles.scss` contains only document-wide defaults and reduced-motion behavior.

## Run

Start the backend first using the [root README](../README.md). Then, with Node 24.15+ or 26+, run this from the repository root in a second terminal:

```shell
cd frontend
npm ci
npm start
```

Open `http://localhost:4200`. The dev proxy forwards `/api` to `localhost:8080`. Styles include responsive layouts, subtle entrance/hover motion, and a reduced-motion fallback.

To check the frontend separately from `frontend/`, run `npm test -- --watch=false` and `npm run build`. Tests cover the root shell, lazy routes, dashboard, list, detail, store, and HTTP gateway. Type declarations, barrel exports, and static fixture data are exercised through those behavior tests rather than given empty one-to-one spec files.

This is a local demo UI. It does not edit the actual engagement file, and the Java server has no authentication or firm isolation yet.
