# HW8 — Giving StockApp a Face: Building a UI with Coding Agents

**Posted:** Monday, October 26 · **Due:** Sunday, November 1 at 11:59 PM

For seven assignments StockApp has had no user interface.  You interacted with it via `curl`. This week it gets a real user interface — a React web app running in a browser, talking to the three microservices you finished last week.

You have never written React in this class, and we are not going to teach it to you. **That is the assignment.**

This is the one assignment of the semester where you are expected to use coding agents to write the code. Point an agent at the `webapp/` directory and let it write TypeScript you have never written before.  But you have to decide what to build, judging whether what came back is correct, pruning what you do not need, and being able to demo it in front of the class and in your oral examination.

The agent knows React. It does not know what StockApp is for, it does not know what your three services actually return, and it cannot take responsibility for the result. 

> **NOTE:** this assignment is a single Pull Request from a branch named exactly `hw8` — per the [syllabus](../../SYLLABUS.md#homework-assignments-55), a PR from a branch with any other name will not be graded.

Parts of this assignment ask you to explain things in writing. Answer those as part of the description of your Pull Request (PR). **A detailed template specific to this assignment is included at the very end.**

> **Oral examination:** this assignment includes a 10 minute oral examination about your work, and your submission score is multiplied by how well it goes. 

> **In-class presentation:** this assignment also includes a **5 minute live demo in class**, worth 15 of the 100 points. You will present on **Monday, November 2** or **Monday, November 9**. Details at the end of this document.

Good luck! And please ask any questions in our Slack **#hw8** room.

## AI / LLM Policy for this assignment — read this carefully

- **Inside `webapp/` — agents encouraged, without limit.** Any model, any tool, any number of them. Generate, iterate, regenerate, throw it away and start over.
- **Everywhere else — by hand, as always.** All Java. All Gradle. `.github/workflows/ci.yml`. `webapp/REQUIREMENTS.md`. Every word of your Pull Request description. Problem 1 of this assignment is Java, and it is a no-agent problem.



## Getting Ready Best Practice

**There is an extra step at the front this week.** The scaffold is new code published to the class repository, and it does not reach your fork on its own.

1. Open **your fork** on github.com, click **Sync fork**, then **Update branch**.
2. `git checkout main`
3. `git pull`
4. `./gradlew clean build` — confirm latest `main` builds for you

Then create your branch:

1. `git branch hw8`
2. `git checkout hw8`

Confirm the scaffold arrived:

```
ls webapp
```

You should see `package.json`, `index.html`, and a `src/` directory. If `ls` says no such directory, step 1 did not happen — go back and do it. More on the scaffold in Problem 3.

Commit incremental progress with `git commit -a -m "description of incremental progress"`.

## A note on how to pace this week

Problems 1 and 2 — the Java work and your product spec — **do not depend on knowing any React**, and you can finish both early. Problem 1 in particular is what unblocks everything else: until the browser can talk to your services, there is nothing for a UI to display. 

Problem 3 is where the agents come in, and it will go faster than you expect and then stall in a place you did not expect. Leave yourself the weekend for the part that is actually hard, which is not getting something on the screen — it is getting it to behave correctly when a request fails.

Write the Problem 4 tests **as each screen starts working**, not on Sunday night. A test written when the watchlist first renders is still there on Saturday when an agent rewrites that component.

## Problem 1 — Letting the Browser In (20 points)

**No AI assistance on this problem.** This is Java, and it is the part of the assignment you are already very familiar with.

Your web app will be served by a development server at `http://localhost:5173`. Your services listen on `http://localhost:8000`, `http://localhost:8001`, and `http://localhost:8004`. The moment your app calls `fetch` against any of them, the browser will refuse, and your console will fill with a message about **CORS**.

Nothing is broken. The browser is doing its job. Your job in Problem 1 is to address this.

### Background — what CORS is and why it exists

Read this section before you start.

**An origin is a scheme, a host, and a port — all three.** `http://localhost:5173` and `http://localhost:8000` are *different origins*, because the ports differ. So are `http://localhost:8000` and `http://127.0.0.1:8000`, because the hosts differ as text even though they resolve to the same machine. Your web app and your three services are four different origins sitting on one laptop.

**The same-origin policy is the browser's default: a page may send a request to another origin, but it may not read the response.** Note which half is blocked. The request often goes out and your server often handles it perfectly well; the browser simply refuses to hand the result back to the JavaScript that asked for it.

**Why browsers do this.** Your browser attaches your cookies to requests automatically. You are logged into your bank in another tab right now. Without the same-origin policy, any page you visit could `fetch("https://yourbank.com/accounts")` — with your session cookie along for the ride — and read your balance back as a string. The policy exists because the browser is carrying your credentials for every site you are signed into, and it has no way to know which pages deserve to use them.

**CORS is the opt-in that relaxes it.** Cross-Origin Resource Sharing is a set of response headers through which *the server* declares who is allowed to read its responses. The important word is *server*: the permission is granted by the thing being called, not claimed by the thing calling. Your services currently grant nothing, so the browser blocks everything.

**Simple requests versus preflighted requests.** This is the part that produces confusing errors.

- A **simple request** — a `GET`, a `HEAD`, or a `POST` with an ordinary content type, and no unusual headers — is sent straight out. The browser lets it go, then inspects the response for an `Access-Control-Allow-Origin` header and throws the response away if it does not like what it finds.
- Anything else — `DELETE`, `PUT`, a custom header, a JSON content type — is **preflighted**. Before sending your actual request, the browser sends an `OPTIONS` request of its own to the same URL, asking the server whether the real one would be permitted. If the answer is unsatisfactory, **your real request is never sent at all.**

The preflight is a request your code never wrote, to a method your handler has probably never seen. A server that answers `GET /unsubscribe/BAC` perfectly can still fail completely in a browser, because the browser asked `OPTIONS /unsubscribe/BAC` first and got a 404.

**The last thing to internalize: a CORS failure is a client-side failure.** Your service returned 200. Your logs are clean. There is nothing wrong in your terminal, and nothing you can reproduce with `curl`. The rejection happens inside the browser, after your response arrives, and the only place you will see it is the browser's console and Network tab.

### 1.1 — Unsubscribe becomes a DELETE (8 points)

Before the CORS work, one small correction to an API you wrote in HW5.

`GET /unsubscribe/<symbol>` removes a subscription. That is a GET request with a side effect, and RESTful API's have an old, strong convention against those: a GET is supposed to be **safe** — it reads, it does not change anything. Anything on the internet is allowed to assume that and act on it. Browsers pre-fetch links. Proxies cache GET responses. A crawler that wanders into your app can unsubscribe every symbol you have by doing nothing more aggressive than following links.

Change your **unsubscribe** endpoint in `finnhub-service` so that it is served on the HTTP **DELETE** method at the same path:

```
DELETE http://localhost:8004/unsubscribe/<symbol>
```

A GET to that path should now be rejected with **405 Method Not Allowed**. Everything else about the endpoint — validation, error codes, response messages — stays exactly as HW5 specified it. Update any unit tests and any `curl` commands in your own notes accordingly:

```
curl -i -X DELETE http://localhost:8004/unsubscribe/BAC
```

Leave **subscribe** and **subscribed-symbols** on GET. The asymmetry is useful in the next part.

### 1.2 — Wrapping your handlers for CORS (6 points)

We are giving you the CORS handler. Copy it into your project as-is:

```java
package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

// An HttpHandler that wraps another handler, layering on CORS support so that
// browsers can talk to our service.
// https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
public class CORSHandler implements HttpHandler {

   final HttpHandler handler;

   public CORSHandler(HttpHandler handler) {
      this.handler = handler;
   }

   @Override
   public void handle(HttpExchange exchange) throws IOException {
      exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

      if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
         exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
         exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
         exchange.sendResponseHeaders(204, -1);
         return;
      }

      handler.handle(exchange);
   }
}
```

Read it before you use it. Both pieces from the background section are there: the `Access-Control-Allow-Origin` header on every response, and the early return that answers a preflight `OPTIONS` with a 204 and a list of what would be allowed.

Notice the shape. `CORSHandler` **is** an `HttpHandler`, and it **has** an `HttpHandler`. It adds something to the exchange and then delegates the real work to the handler it wraps. That is the same relationship `MostActiveStockHandler` has with `AnalyticsComputor`, and it is why none of your existing handlers need to change — you are only changing the lines that register them with the `HttpServer`:

```java
server.createContext("/symbols", new CORSHandler(new SymbolListHandler(...)));
```

**Your job is placement and wiring, and where this class lives is part of the grade.** The browser needs to reach all three of these:

| Port | Served by | Endpoints |
| --- | --- | --- |
| 8000 | `monolith-service` | `/symbols`, `/price/<symbol>`, `/mostactive` |
| 8001 | `monolith-service` (`MetricsWebServer`) | `/updates-volume`, `/price-volume` |
| 8004 | `finnhub-service` | `/subscribe/<symbol>`, `/unsubscribe/<symbol>`, `/subscribed-symbols` |

That is three servers across **two Gradle modules**. Three copies of this file is the wrong answer, and you already built the right answer in HW6 and HW7. Put it where it belongs, fix the package declaration to match, and wrap every handler on all three servers.

Make sure `./gradlew preCommit` still passes, and that your existing integration test still passes.

### 1.3 — Watch it work, then explain it (6 points)

Get your three services running and your web app talking to them before you answer this. Two of these questions are about things you need to have actually looked at.

**a) Find the preflight.** Open your browser's Network tab and trigger an unsubscribe. You will see a request your code never made: an `OPTIONS` to `/unsubscribe/<symbol>`, sent by the browser before the real `DELETE`. **In your PR:** paste or describe what the `OPTIONS` request and its response look like, and say which specific line of `CORSHandler` produced that response. Then explain why a `GET` to `/symbols` produces no `OPTIONS` at all.

**b) Break it on purpose.** Comment out the `Access-Control-Allow-Origin` line, restart, and reload your app. **In your PR:** what does the browser console say, what does the Network tab show for the request, and what does your *server's* terminal show? Put the last one in your own words.

**c) The wildcard.** The handler we gave you allows every origin — `*`. For local development that is fine. Describe a concrete, specific bad thing that could happen if StockApp shipped to production with `Access-Control-Allow-Origin: *` on all three services, and say what you would configure instead.

**d) Why does `curl` not care?** Every one of these endpoints has worked from `curl` all semester, with no CORS headers anywhere. What does that tell you about who CORS is actually protecting, and from what?

## Problem 2 — Your Product Requirements (10 points)

Every assignment so far has handed you a product requirements document. This week you write one.

Create **`webapp/REQUIREMENTS.md`** and **commit it before you create or modify any file under `webapp/src/`.** We will check `git log`. An agent with a specification builds your product; an agent without one builds something that looks like a product, but possibly with many extra functions and incorrect details.

Keep it to **one page**. Write it in the voice of the requirements documents you have been reading all semester — HW5's is a good model. It must cover:

- **Who this is for and what they are trying to do.** Two or three sentences, no more.
- **Each screen**, and for each one, what a user can see and do there.
- **What happens when things go wrong.** Which of your services can be down, and what the user sees when one is.
- **Out of scope.** What you deliberately decided not to build, and why. Be specific — "authentication, because there is no concept of a user in StockApp" is a real answer; "extra features" is not.

Agents produce far more than you asked for. A list of what you decided *not* to build is how you notice when that has happened.

## Problem 3 — The Web App (30 points)

### The scaffold

`webapp/` contains a working React + TypeScript + Vite project, with routing, a linter, a test runner, and API mocking already configured. Read `webapp/README.md` first — it is a tour of which file does what.

From `webapp/`:

| Command | What it does |
| --- | --- |
| `npm install` | Downloads dependencies. Run this once before anything else. |
| `npm run dev` | Starts the development server, by default on `http://localhost:5173` |
| `npm run test` | Runs the unit tests |
| `npm run lint` | Runs the linter |
| `npm run build` | Type-checks and produces a production build |

All four of `install`, `lint`, `test`, and `build` must succeed on your submitted branch. The scaffold ships a `.nvmrc` pinning **Node 22** — use it.

### Run the scaffold before you prompt anything

Do this first, start to finish, before you open a coding agent. It takes ten minutes, and it is how you learn what "working" looks like before you change anything.

**1. Get Node 22.** See [One-time setup in the README](../../README.md#one-time-setup-install-node-22). Then:

```bash
cd webapp
node -v          # must report v22.x
```

If you use `nvm`, run `nvm use` here first — it reads `.nvmrc`.

**2. Install dependencies.**

```bash
npm ci
```

Use `npm ci`, not `npm install`. It installs the exact versions in `package-lock.json`.

**3. Run it with mock data.** No Java needed for this step:

```bash
npm run devWithMocks
```

Open **http://127.0.0.1:5173**. You should see a "Stock App" header with **Symbols** and **Most Active** links. Click both. Each shows nothing but a heading — those are the TODO stubs you are replacing. That is the correct starting state.

**4. Run the checks.** In a second terminal, from `webapp/`:

```bash
npm run test     # 1 test passes
npm run lint     # no output means clean
npm run build    # writes to dist/
```

All three must pass before you change a line. If one fails now, it is an environment problem, not a code problem.

**5. Point it at your real services.** Stop `devWithMocks`. In three more terminals:

```bash
./gradlew queue-service:runService
./gradlew finnhub-service:runServiceWithMockFinhubPreload
./gradlew monolith-service:runServer
```

Then, from `webapp/`, `npm run dev`. Open the app, open your browser's developer tools (**Cmd+Option+I** on macOS, **Ctrl+Shift+I** on Windows and Linux), and look at the **Console** tab.

**It will not work yet.** You should see a CORS error — the one described in the background section above. That is Problem 1.

Once Problem 1 is done, come back and repeat step 5. The error should be gone.

The scaffold's existing `Symbols` and `MostActive` screens are TODO-shaped stubs. They are a **starting point, not a requirement**. Repurpose them, or delete them. Deleting code that came with the project is a legitimate and often correct decision.

You may add libraries. You may change the styling however you like. You may not replace the stack: it stays React + TypeScript + Vite, so that we can all read each other's code.

### 3.1 — The watchlist (13 points)

The primary screen. A user should be able to see the symbols StockApp is currently subscribed to, with the current price for each, **updating on its own** without the user reloading the page. From this screen they can subscribe to a new symbol and unsubscribe from one they no longer want.

Your services do not push to the browser — you will be **polling**. Pick an interval and **justify the number in a code comment.** You solved a closely related problem in HW7 when you wrote exponential back-off into `QueueReader`; think about what carries over to a browser and what does not.

Your subscribe form has to handle what your API actually does, not what it would be convenient for it to do. HW5 specified a 409 on a duplicate symbol, a 409 at the ten-symbol limit, and a 400 on a malformed one. Each of those needs to reach the user as something they can act on.

### 3.2 — The metrics screen (7 points)

A second screen showing your `/updates-volume` and `/price-volume` metrics from port 8001. The bar is simply that it is **more useful than the raw text the endpoint returns.** If displaying it unchanged is as good as it gets, say so in `REQUIREMENTS.md` and explain why.

### 3.3 — Loading, error, empty, and stale (10 points)

Every one of these states is reachable in StockApp by stopping a service in a terminal.

Every view that loads data must handle all four:

- **Loading** — first load, no data yet.

- **Error** — the request failed, or came back with a status you did not expect. The user is told what failed and what they can do.

- **Empty** — the request succeeded and there is nothing to show. Zero subscribed symbols is a real, normal, first-run state, not an error.

- **Stale** — you had data, and the latest refresh failed or has not landed. Keep showing the old data and tell the user when it was last updated. 

  

**Develop against mock FinnHub.** The markets are closed when you are doing your homework, and a live-updating UI with nothing to update is impossible to debug:

1. `./gradlew queue-service:runService`
2. `./gradlew finnhub-service:runServiceWithMockFinhubPreload`
3. `./gradlew monolith-service:runServer`
4. `cd webapp && npm run dev`

> To a browser, `http://localhost:8000` and `http://127.0.0.1:8000` are **different origins**. Pick one spelling and use it everywhere.

## Problem 4 — How Do You Know It Works? (17 points)

Here is the honest situation you are in. There are now several hundred lines of TypeScript in your repository that you did not write, in a framework we never taught you, and you are responsible for the result. You are not going to audit all of it line by line, and we are not going to ask you to.

**You know it works because you checked, and you know it still works because the check is automated.** Everything you learned about testing in HW2 applies here without modification.

This problem is three questions, and they are the three questions that matter when the code is not yours: *How do you know it is working? How do you know your next change did not break it? What happened when you found a bug?*

### 4.1 — Tests that tell you it works (8 points)

The scaffold already has everything you need. `npm run test` runs **Vitest**. `src/tests/setup.js` already starts **MSW** — Mock Service Worker — which intercepts `fetch` inside your tests and answers with whatever you tell it to. This is your HW2 mocking lesson in a different language: **your tests will not need a single one of your three services to be running.** You define the response, so you can produce a price update, an empty symbol list, a 409, or a connection failure on demand — including the cases you could never reliably trigger against a real service.

`src/StockApp.test.tsx` is a worked example, commented with the Arrange / Act / Assert structure. Read it before you ask an agent for more.

Write at least **four** tests. They must cover, at minimum:

1. The watchlist renders the symbols and prices your services returned.
2. The **empty** state — the request succeeded and there are no subscribed symbols.
3. A **failure** path — a service returns an error or does not respond, and the user is told.
4. One of your subscribe-form error cases — the 409 on a duplicate symbol, or the 409 at the ten-symbol limit.

`npm run test` must pass on your submitted branch, and your CI job from Problem 5 must run it.

You may have an agent write these. Deciding *what* must be true for your app to be working is the part we are grading.

### 4.2 — Knowing you did not break it (4 points)

**In your PR:** you made changes late in the week, on top of an app that was already partly working. Pick **one specific change** you made after something was already working.

- What was the change?
- What told you that the things that used to work still worked? If the honest answer is "I clicked around," then say exactly which things you clicked and why that set and not another.
- Which of your Problem 4.1 tests would have caught it if that change had broken something?
- What part of your app currently has **nothing** watching it? Name it.

### 4.3 — A bug you found by using your own app (5 points)

Somewhere this week you clicked something and your app did the wrong thing. **In your PR**, write that up:

- **What you did, what you expected, and what actually happened.** Concretely enough that we could reproduce it.
- **How you tracked it down.** Which tool told you where the problem was — the browser console, the Network tab, your service's terminal output, `curl`, a `console.log` you added? What did it show you?
- **What the fix was**, in one or two sentences. You may describe the fix in terms of behavior rather than code.
- **Did you add a test for it?** If yes, which one. If no, say why not — "it is a styling issue and not worth a test" is a legitimate answer, honestly given.

A bug that an agent found and fixed before you ever saw it does not count.

## Problem 5 — Extending the Pipeline (4 points)

**No AI assistance** — this file is yours.

The comment at the top of `.github/workflows/ci.yml` has promised since HW1 that you would extend this pipeline later in the semester. This is later in the semester.

Add a **second job** to the workflow that builds the web app. It should check out the code, set up Node 22, install dependencies, and run lint, test, and build. Run `npm ci` rather than `npm install` — and in your PR, say why that distinction matters in CI specifically.

Keep the existing Gradle job as it is. The two jobs are independent and should be able to run at the same time.

## Problem 6 — In-Class Presentation (19 points)

On **Monday, November 2** and **Monday, November 9** you will each give a **5 minute live demo** to the class.

**Claim your slot by Friday, October 30** on the **[hw8-demo.md sheet](https://github.com/BU-EC427-Fall2026/review-sign-ups/blob/main/hw8-demo.md)** in the review-sign-ups repository — same mechanics as an oral exam sign-up. There are exactly as many slots as there are students, so every open slot belongs to someone who has not signed up yet; anyone without one after Friday gets assigned whatever is left. Note that this is a **separate sheet** from your oral examination slot, and you need both.

The code you demo must be the code in your Pull Request, so a November 2 slot means demoing what you submitted the night before.

**Format:**

- **Your slot is 5 minutes, hard stop**, and that includes questions. Plan for about **four minutes of demo and a minute of questions** from the room. There will be a timer on the projector.
- **Live, from your own laptop**, against `runServiceWithMockFinhubPreload`.  Have your three services and your dev server **already running** before you come up — the timer does not wait for `npm install`.

**Your four minutes must cover three things,** and you should plan to spend most of the time on the second and third:

1. **Show it.** What you built and who it is for.
2. **One product decision you made, and why.** Something that was genuinely a choice, where a reasonable classmate would have chosen differently.
3. **How you know it works.** Your bug from Problem 4.3 is the best material here — what broke, how you found it, and what you did so it stays fixed.

You are graded on whether the three things above come through clearly and whether you can answer a question about your own app, not on polish or public speaking.

## Submitting this assignment

Same as previous assignments. Make sure everything is committed with `git commit -a -m "<description>"`, and check `git status` for anything outstanding.

Before pushing:

1. Run `./gradlew preCommit` from the repository root. It cleans, reformats, compiles, runs static analysis, and executes the unit tests across every module.
2. From `webapp/`, run `npm run lint && npm run test && npm run build`. All three must pass.

Confirm that `webapp/node_modules` and `webapp/dist` are **not** in your commit. The `.gitignore` that ships with the scaffold covers both — if you see thousands of files in `git status`, stop and fix that before you push.

Push with `git push --set-upstream origin hw8`, or just `git push` if the branch already exists remotely.

Then create a Pull Request from GitHub by clicking **Compare & pull request** for your new branch.

> **IMPORTANT: make sure the base of the pull request is your fork and not the base for the class.** Throughout this course you only create Pull Requests against, and merge code into, your own repository.

Remember to invite the `staff` team as a reviewer.

### GitHub Pull Request Template for this Assignment

```markdown
## AI Tool Citation

Which agents and models I used, and roughly what I used each one for:

Response here

## Problem 1 — Letting the Browser In
### Problem 1.3a — The preflight: what the OPTIONS request and response look like, which line of CORSHandler produced it, and why GET /symbols produces no OPTIONS

Response here

### Problem 1.3b — With Access-Control-Allow-Origin commented out: what the browser console said, what the Network tab showed, and what my server's terminal showed

Response here

### Problem 1.3c — What could go wrong shipping `Access-Control-Allow-Origin: *` to production, and what I would configure instead

Response here

### Problem 1.3d — Why does curl not care about any of this? Who is CORS actually protecting, and from what?

Response here

## Problem 3 — The Web App
### Polling interval

What interval I chose, and why. What carried over from HW7's exponential back-off, and what did not:

Response here

## Problem 4 — How Do You Know It Works?
### Problem 4.2 — Knowing I did not break it

The change I made on top of working code; what told me the working things still worked; which of my tests would have caught a break; and what part of my app currently has nothing watching it:

Response here

### Problem 4.3 — A bug I found by using my own app

What I did, what I expected, what happened; how I tracked it down and what the tool showed me; what the fix was; and whether I added a test for it:

Response here

## Problem 5 — Extending the Pipeline
### Why `npm ci` rather than `npm install` in CI?

## Oral Examination

A **10 minute conversation** with the TA or the instructor, producing a **multiplier between 0.0 and 1.0** applied to your submission score.

**Scheduling note:** the week immediately after this deadline has the midterm in it, so HW8 oral examinations are pushed a week — they run **Tuesday, November 10 through Friday, November 13**. Claim a slot on the **[hw8.md sheet](https://github.com/BU-EC427-Fall2026/review-sign-ups/blob/main/hw8.md)** in the review-sign-ups repository, which is live now. Your in-class demo does **not** replace this conversation, and it is a **different sheet** — you need a slot on both.

Same format as HW6 and HW7: there is no published question bank, and we will have your Pull Request open in front of us. You will be asked **three questions**. There is nothing to study beyond your own submission.

**What is different this time is that the questions come in two flavors, and which one you get depends on which part of the assignment we are looking at.**

**On the parts you wrote by hand — Problem 1, Problem 2, and Problem 5 — nothing changes.** We ask detailed, line-by-line code questions, exactly as we have all semester. We pick a line, you tell us what it does, why you wrote it that way, and what happens under a condition the assignment did not spell out.

**On `webapp/`, we ask about testing and verification instead.** We will not ask you to explain a line of TypeScript you did not write. We will ask things like:

- Show us what the app does when `finnhub-service` is down.
- Which of your tests covers this screen, and what does it assert?
- How would you know if this stopped working?
- Why this polling interval?
- Where would you look first if a price stopped updating?
- Walk us through the bug from Problem 4.3.

Both kinds of question carry the same weight.

### How your multiplier is calculated

You start at **1.0**.

- **Answer all three questions well → 1.0.**
- **If you cannot answer a question, we ask a follow-up** — a smaller, more concrete version of the same question. Answer that well and you lose only **0.05** for that part.
- **If you can answer neither the question nor the follow-up, you lose 0.33** for that part.

The multiplier never goes below 0.0.

| What happened | Multiplier |
| --- | --- |
| All three answered well | **1.00** |
| Two answered well, one recovered on the follow-up | **0.95** |
| One answered well, two recovered on follow-ups | **0.90** |
| All three recovered on follow-ups | **0.85** |
| Two answered well, one missed entirely | **0.67** |
| One answered well, one recovered, one missed entirely | **0.62** |
| One answered well, two missed entirely | **0.34** |
| All three missed entirely | **0.00** |

### What "answered well" means

You do not need to be word perfect, and you will not be marked down for pausing to think or re-reading your own code. We are checking that you can explain **your own submission** and reason about it:

- you can navigate your submission and describe what it does without guessing
- you can explain *why* you made a choice, not just *what* the result was
- you can reason about a condition the assignment did not ask you to handle

> **A note on AI assistance:** this assignment permits agents inside `webapp/` and nowhere else. What that permission covers is the typing, not the responsibility. You are not expected to have read every line, but you are expected to know what your app does, what you verified, and what you did not. The multiplier is where that shows up.
