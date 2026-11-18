# EC427 React Web App

This directory contains the React web app that provides the user interface for StockApp. It arrives with HW8 — see [hw/hw8/hw8.md](../hw/hw8/hw8.md) for the assignment.

Everything here is a starting point. The screens are TODO-shaped stubs and you are expected to replace them. The build, the linter, the test runner, and API mocking are already configured.

**Node 22.** The `.nvmrc` in this directory pins it. If you use `nvm`, run `nvm use` here before anything else.

## The services this app talks to

Your web app runs on `http://127.0.0.1:5173` and reads from the three microservices you built in HW6 and HW7:

| Port | Served by | Endpoints |
| --- | --- | --- |
| 8000 | `monolith-service` | `/symbols`, `/price/<symbol>`, `/mostactive` |
| 8001 | `monolith-service` (`MetricsWebServer`) | `/updates-volume`, `/price-volume` |
| 8004 | `finnhub-service` | `/subscribe/<symbol>`, `/unsubscribe/<symbol>`, `/subscribed-symbols` |

`src/constants.ts` currently knows about exactly one of them. A browser treats `localhost` and `127.0.0.1` as **different origins**, so pick one spelling and use it everywhere — the dev server is configured for `127.0.0.1`.

Those services will reject your requests until you have done Problem 1 of HW8. That is expected, and the assignment explains why.

## Important Files

- `index.html`
  - The one and only HTML file for our entire application. In "single page apps" (SPAs), there's typically only one very small HTML file, which bootstraps the JavaScript code which builds out the rest of the structure of the app.
- `src/main.tsx`
  - The initial entry point for the app referred to by the HTML. Bootstraps API mocking and routing.
  - TypeScript + React files generally have either `.ts` or `.tsx` extensions. The `.tsx` extension is required if you use any JSX within the file, so it can be safest to just always use `.tsx`.
- `src/routes.tsx`
  - Defines the URL structure and determines which components get used for what routes. You'll define additional routes here.
- `src/constants.ts`
  - Where the address of the API lives.
- `src/StockApp.tsx`
  - The first React component that we provide for the app. It is mounted at the top of the routing hierarchy.
- `src/StockApp.test.tsx`
  - A unit test for the StockApp component, commented with the Arrange / Act / Assert structure. **Read this before you write your own tests.** Tests may be placed in the same directory as the components they cover.
- `src/tests/setup.js`
  - Runs before every test file. It starts Mock Service Worker, which means **your tests never need any of the three services to be running** — you define the responses.
- `src/mocks/handlers.ts`
  - The mock API responses. Edit this to add more, in tests and in `devWithMocks`.

## Important Commands

The following can all be run from `webapp/`:

- `npm install`
  - Downloads the code for all of the libraries our app depends upon. Must be run once before any of the other commands.
- `npm run dev`
  - Starts the development server
- `npm run devWithMocks`
  - Starts the development server with API mocking enabled, so you can build screens without running any Java
- `npm run test`
  - Runs the unit tests
- `npm run lint`
  - Runs the linter
- `npm run build`
  - Type-checks and produces a production build. This is the one that fails on a TypeScript error that `npm run dev` let you get away with.

## Reference Documentation

- [TypeScript](https://www.typescriptlang.org/docs/handbook/intro.html)
- [React](https://react.dev/reference/react)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro)
- [Vitest API](https://vitest.dev/api/)
- [Mock Service Worker](https://mswjs.io/docs/)
