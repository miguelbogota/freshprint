# Frontend

The Angular app is a live dashboard for the Java API. It shows which engagements need review, lets you search and filter them, reads the server's plain-language change summary, and sends versioned Apply or Decline decisions. It polls the accepted operation until the server reports the final result.

`UpdateStore` owns loading, selection, and decision state. `DecisionGateway` is the only HTTP boundary. `UpdateList` and `UpdateDetail` handle display and user intent. The old fixture remains for isolated UI tests; the running app gets its data from the server.

## Run

Start the backend first, then use Node 24.15+ or 26+:

```shell
npm install
npm test -- --watch=false
npm run build
npm start
```

Open `http://localhost:4200`. The dev proxy forwards `/api` to `localhost:8080`. Styles include responsive layouts, subtle entrance/hover motion, and a reduced-motion fallback.

This is a local demo UI. It does not edit the actual engagement file, and the Java server has no authentication or firm isolation yet.
