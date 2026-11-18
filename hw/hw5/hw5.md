# HW5 — Software Processes & Monitoring

**Posted:** Monday, October 5 · **Due:** Sunday, October 11 at 11:59 PM

In this assignment you will first be implementing a new API according to a detailed **Product Requirements** document. As we discussed during lecture, such documents are commonly driven by product managers and are used for getting broad alignment across various functions of a software development company — product managers, designers, and engineers.

In the second part of the assignment, you are going to start adding **monitoring** functionality to our StockApp. Monitoring is crucial to knowing how your application is being used by your customers, what areas are under stress or near capacity, and enables you to scale and deploy your application accordingly.

> **NOTE:** this assignment is a single Pull Request from a branch named exactly `hw5` — per the [syllabus](../../SYLLABUS.md#homework-assignments-55), a PR from a branch with any other name will not be graded.

Parts of this assignment ask you to explain things in writing. Answer those as part of the description of your Pull Request (PR). **A detailed template specific to this assignment is included at the very end.**

> **Oral examination:** this assignment includes a 10 minute oral examination about your work, and your submission score is multiplied by how well it goes. The full question bank and rubric are at the end of this document — read them before you sign up for a slot.

Good luck! And please ask any questions in our Slack **#hw5** room.

## Getting Ready Best Practice

Same as always — start from a clean, current `main` and branch from there:

1. `git fetch`
2. `git checkout main`
3. `git pull`
4. `./gradlew clean build` — confirm latest `main` builds for you

Then create your branch:

1. `git branch hw5`
2. `git checkout hw5`

Commit incremental progress with `git commit -a -m "description of incremental progress"`.

## Problem 1 — Subscribe to Symbol (20 points)

Since we have started working on **StockApp**, when you start the server in real FinHub mode (as opposed to mock FinHub mode), it has immediately subscribed for updates for two stocks: **TSLA** and **AAPL**. This has been fine for demonstration purposes, but obviously does not make for a robust, useful application to be used by real customers :).

For this problem you will:

- remove the subscription to **TSLA** and **AAPL** on start up.
- follow the requirement document below to subscribe to updates for a new symbol

### Subscribe to Symbol Product Requirements

As a StockApp user, I would like to be able to add stock symbols of interest to me to be tracked by StockApp. This will allow me to tailor StockApp's high power analytics to the set of stocks that are relevant to me and my investments. In turn, the insights that I get from StockApp will be more actionable to me.

Our new **subscribe** API will allow customers to specify a stock symbol for which they are interested in receiving real-time analytics. This API will serve as an entry point for customers to specify their focus symbols, enabling the backend to filter, process, and provide relevant analytics in response. This API is a critical addition to our **StockApp** application, as it allows customers to customize their analytics streams based on their specific investment interests.

The API should accept a single String parameter denoting a US stock symbol of interest.

The API should be available at **localhost:8000/subscribe/\<symbol\>**

The result of a successful subscription should be an immediate subscription with FinHub for the given symbol and therefore receipt of any new updates from FinHub for the given symbol from this point on.

The response back to the customer should be a **200** HTTP response code along with the message, **StockApp is now subscribed to updates for \<symbol\>.**

In this phase of implementation, we are going to impose a limit of **10 total symbols** for **StockApp** to be subscribed to at any given point in time.

### Error Handling

The API should perform the following validations on the stock symbol string provided by our customers:

- Is the string a potentially valid US stock symbol? To confirm this, we should make sure that the string is
  1. between 1 and 5 characters long, inclusive
  2. all of the characters are alphanumeric
  3. If either (a) or (b) are false, the API should return
     - HTTP error code **400** (Bad Request) with the body containing an error of the form: **\<symbol\> is not a valid US Stock symbol.**
- As long as the string is a potentially valid US stock symbol, the next thing we should check is: is **StockApp** already aware of this symbol? If this symbol has already been subscribed to, the API should return
  - HTTP error code **409** (Conflict) with the body containing an error of the form: **\<symbol\> has already been registered.**
- Finally, if **StockApp** is already subscribed to 10 different stock symbols, we should reject subsequent subscription requests. In this case, the API should return
  - HTTP error code **409** (Conflict) with the body containing an error of the form: **\<symbol\> cannot be subscribed to because the server is at its limit of 10 subscriptions.**

Remember that you can start your in-progress implementation's server with any of the `./gradlew runServer*` variants and then test your new APIs with curl, e.g. `curl -i localhost:8000/subscribe/BAC`

### Out of Scope

In this phase of implementation, customers will only be accessing this API via curl/browser and we are not building a User Interface on top of it yet.

### Considerations

Note that **StockApp** already has support for a **symbols** endpoint. Please be aware, that the functionality we are adding here is different. The existing **symbols** endpoint returns the symbols for which our server has seen at least one update, which is semantically different than the symbols for which customers have requested a subscription for. For example, consider the following timeline:

1. Customer requests subscription for **GOOG**
2. Customer requests subscription for **MSFT**
3. **StockApp** receives an update for **GOOG**
4. At this point the existing **symbols** API would return **GOOG**. But StockApp has subscribed to both **GOOG** and **MSFT**
5. Customer requests subscription for **MSFT** — this should produce a **409** error even though we have not received any updates for **MSFT** yet, because a subscription for it already exists.

### 1.1 — Removal of hardcoded subscribed to symbols (5 points)

For this part of the problem, find the place in the code where on start-up we are automatically adding subscriptions for **AAPL** and **TSLA** and remove that static subscribing. While doing that, take a look at the code that is creating the subscription as that will be useful for you for the next part of the problem.

### 1.2 — End to end Subscribe implementation (10 points)

Add support for the **/subscribe** endpoint based on the product requirements document above.

Make sure that the error handling is consistent with the requirements document. You'll need to make changes to multiple classes and potentially interfaces to have this API be fully functional.

Make sure that you add unit test coverage for any non-trivial code changes that you are making, and that all static analysis warnings in your new code are addressed.

### 1.3 — How did you decide which classes/interface to modify while implementing this new API? (5 points)

**In your PR** (template below).

## Problem 2 — Subscribed Symbols (10 points)

As mentioned above, our existing **symbols** API serves a slightly different purpose than the flexible subscriptions we are working on here. That API returns the symbols for which we have seen data, which is different than the symbols for which subscriptions exist.

For this problem, we will be adding a **subscribed-symbols** endpoint that returns the set of symbols that have been subscribed for via calls to the **subscribe** API you implemented in Problem 1.

The API should be available at **localhost:8000/subscribed-symbols**

This API should return a **200** HTTP response code and a response in the form **StockApp is subscribed to \<N\> symbols. They are: \<symbol-1\>, \<symbol-2\>.** If **StockApp** is not subscribed to any symbols, you can leave off the second sentence completely.

Make sure that you add unit test coverage for any non-trivial code changes that you are making, and that all static analysis warnings in your new code are addressed.

## Problem 3 — Unsubscribe from Symbol (20 points)

As a StockApp user, I would like to be able to remove stock symbols that are tracked by StockApp. Tracking symbols that are of no interest to me waste my time by injecting noise into the otherwise high value signal I get from StockApp's analytics. Together with the subscribe API, these two API's will allow me to tailor my StockApp experience to the exact set of stocks that I am interested in.

Just as the subscribe API is crucial to making **StockApp** more dynamic and customizable for particular customer needs, **unsubscribe** is needed to fully support this flexibility. There are a few reasons that a customer may need to be able to unsubscribe **StockApp** from updates for a given symbol:

1. When **StockApp** reaches its current limit of at most 10 subscriptions, without the **unsubscribe** API, customers would be permanently stuck with the set of 10 subscriptions at the time of reaching the limit. The **unsubscribe** API will allow them to remove a symbol in order to make room for an additional subscription.
2. Certain API's supported by **StockApp**, perform an analysis across all data points known to **StockApp**. For example, **mostactive** performs a computation that is not localized to a single stock — rather it considers all symbols known to **StockApp**. In certain cases, if a customer is no longer interested in seeing a particular symbol always being returned as the most active one, they may want to **unsubscribe** from updates for that symbol.

The API should accept a single String parameter denoting a US stock symbol of interest.

The API should be available at **localhost:8000/unsubscribe/\<symbol\>**

The result of a successful unsubscription should be an immediate removal of the subscription with FinHub for the given symbol.

The response back to the customer should be a **200** HTTP response code along with the message, **StockApp is now unsubscribed from updates for \<symbol\>.**

### Error Handling

The API should perform the following validations on the stock symbol string provided by our customers:

- Is the string a potentially valid US stock symbol? To confirm this, we should make sure that the string is
  1. between 1 and 5 characters long, inclusive
  2. all of the characters are alphanumeric
  3. If either (a) or (b) are false, the API should return
     - HTTP error code **400** (Bad Request) with the body containing an error of the form: **\<symbol\> is not a valid US Stock symbol.**
- As long as the string is a potentially valid US stock symbol, the next thing we should check is: is **StockApp** currently subscribed for updates to this symbol? If this symbol has not been subscribed to, the API should return
  - HTTP error code **409** (Conflict) with the body containing an error of the form: **\<symbol\> has not been previously subscribed to.**
 
### Real FinHub Client

Now, StockApp should return active stock data if it's running with the FinHub API during US stock market hours! If nothing other than `ping` is received, you should check your subscribe and unsubscribe logic to the FinHub API.

> US stock market hours are Monday through Friday from 9:30 a.m. to 4:00 p.m. Eastern Time.

### 3.1 — Implementation (10 points)

Add support for the **/unsubscribe** endpoint based on the product requirements document above.

Make sure that the error handling is consistent with the requirements document. You'll need to make changes to multiple classes and potentially interfaces to have this API be fully functional.

Make sure that you add unit test coverage for any non-trivial code changes that you are making, and that all static analysis warnings in your new code are addressed.

### 3.2 — DRY (5 points)

In lecture this week we talked about the **DRY** (Do Not Repeat Yourself) principle. Is there a piece of code that you needed to implement for problems 1 and 3 that is exactly the same? Make sure that you factor this code out into a shared collaborator that is reused by your solutions for problems 1 and 3.

### 3.3 — How did you decide which code should or should not be shared? (5 points)

**In your PR** (template below).

## Problem 4 — Monitoring Service (50 points)

In lecture this week we talked about the importance of having robust monitoring in place for any software system. We also discussed the availability of excellent tooling that enhance monitoring with support for visualization and API based metric reporting. We will integrate StockApp with these tools later in the class, but for the purposes of this assignment we are going to implement our own metrics service that should give you a really good sense of the benefits of metric reporting and collection and some of the challenges involved with it.

Further, we talked about the distinction between **Application** and **Infrastructure** monitoring. Starting with Application monitoring in this HW assignment is appealing for a few reasons:

- it is easier to iterate on this type of monitoring locally, as it does not require any cloud infrastructure to be available to us (yet)
- it is a bit more relevant to the Software Development Lifecycle we covered this week in that it requires product level thinking (e.g. what customer behaviors is it interesting for us to be aware of?)
- once we do start integrating with high power tools like Nagios, Prometheus, and Amazon Cloud Watch, we may get a lot of the infrastructure monitoring out of the box for us.

As you are well aware at this point, **edu.bu.server.BasicWebServer** is the entry point for the web server that responds to StockApp customer requests for endpoints like **localhost:8000/price/\<symbol\>**.

In this problem, we are going to add a second service to StockApp.

### Scaffolding (10 points)

Create a class called **edu.bu.metrics.server.MetricsWebServer**. Feel free to use **BasicWebServer** as your template for creating **MetricsWebServer**.

In order for **BasicWebServer** and **MetricsWebServer** to happily co-exist and respond to their appropriate endpoints, they will listen for requests on different ports. **BasicWebServer** is currently configured to listen for requests on port **8000**. And you should configure **MetricsWebServer** to listen on port **8001**.

Create another class called **edu.bu.metrics.MetricsTracker**. In **StockAppServer**, create an instance of **MetricsTracker**. Then after **BasicWebServer** has been started, instantiate **MetricsWebServer**, pass the **MetricsTracker** collaborator to it and start **MetricsWebServer**.

For the rest of this HW assignment, you will be adding functionality to **MetricsTracker** and **MetricsWebServer** — to gather data, hold it in **MetricsTracker**, expose it via public methods in **MetricsTracker** and ultimately expose it to our DevOps engineers via HTTP requests handled by **MetricsWebServer**.

The first endpoint we will be implementing is one that will return the number of FinHub updates we have received for each stock symbol. This endpoint will live at **localhost:8001/updates-volume**. More details on the requirements for this endpoint are below, but for the purposes of scaffolding, start by creating support for this endpoint and making it return the string **Hello metrics world**.

Confirm that you can start StockApp with either `./gradlew runServer` or `./gradlew runServerWithMockFinhub` and once it has started make a request:

```
curl -i "http://localhost:8001/updates-volume"
```

Once you have this working enough to see the expected **Hello metrics world** response, your scaffolding is complete and you can move onto implementing this endpoint with real data.

### Updates-volume endpoint (15 points)

The intent of this metrics endpoint is to give us an idea of how frequently FinHub is sending us updates for all of the symbols that we have subscribed to over the uptime of the **StockApp** server. The requirement is that this endpoint returns a string that looks like this:

```
Updates-volume:
RIVN : 2
BAC : 1
NKE : 1
```

where **Updates-volume:** is a hardcoded header. Then each stock symbol that we have seen a FinHub update for should appear on its own line, where the format of that line is **\<symbol\>: \<updates-count\>**. The stock symbols should be sorted in descending order by the number of updates we have seen for each symbol.

### Price-volume endpoint (15 points)

The updates-volume endpoint we have just implemented is great for helping us monitor our interactions with our third-party stock data provider (FinHub), but we are also very interested in how our customers are interacting with the endpoints that we are making available to them.

Implement a metrics endpoint at **localhost:8001/price-volume** that is very similar to **updates-volume** but for the **/price** endpoint. The output format for this endpoint is virtually identical:

```
Price-volume:
RIVN : 2
BAC : 1
NKE : 1
```

where **Price-volume:** is a hardcoded header. Then each stock symbol that we have seen a customer **price** request for should appear on its own line, where the format of that line is **\<symbol\>: \<requests-count\>**. The stock symbols should be sorted in descending order by the number of requests we have seen for each symbol.

During implementation, play around with your new endpoint by starting the server, making some **price** requests and confirming that the new **price-volume** endpoint returns expected data.

Add unit test coverage as appropriate and make sure that your code passes all of the static analysis checks.

### 4.1 — Design decisions (4 points)

**In your PR:** while implementing both of the endpoints above, how did you decide on the classes that **MetricsTracker** should or should not collaborate with?

### 4.2 — Further endpoints (3 points)

**In your PR:** now that you have implemented two metrics endpoints, what other metric endpoints do you think would be valuable to add to **StockApp** and why?

### 4.3 — Alerting (3 points)

**In your PR:** based on either the metrics endpoints you just implemented, or the ones that you have suggested for further work, what alerts would be useful to add for **StockApp**? Who would be responsible for responding to these alerts and what actions might they take?

## Submitting this assignment

Same as previous assignments. Make sure everything is committed with `git commit -a -m "<description>"`, and check `git status` for anything outstanding.

Before pushing, run `./gradlew preCommit`. It cleans, reformats, compiles, runs static analysis, and executes the unit tests. If it passes, your branch is in good shape.

Push with `git push --set-upstream origin hw5`, or just `git push` if the branch already exists remotely.

Then create a Pull Request from GitHub by clicking **Compare & pull request** for your new branch.

> **IMPORTANT: make sure the base of the pull request is your fork and not the base for the class.** Throughout this course you only create Pull Requests against, and merge code into, your own repository.

Remember to invite the `staff` team as a reviewer.

### GitHub Pull Request Template for this Assignment

```markdown
## Problem 1 — Subscribe to Symbol
### Problem 1.3 — How did you decide which classes/interface to modify while implementing this new API?

Response here

## Problem 3 — Unsubscribe from Symbol
### Problem 3.3 — How did you decide which code should or should not be shared?

Response here

## Problem 4 — Monitoring Service
### Problem 4.1 — While implementing both of the endpoints above, how did you decide on the classes that MetricsTracker should or should not collaborate with?

Response here

### Problem 4.2 — Now that you have implemented two metrics endpoints, what other metric endpoints do you think would be valuable to add to StockApp and why?

Response here

### Problem 4.3 — Based on either the metrics endpoints you just implemented, or the ones that you have suggested for further work, what alerts would be useful to add for StockApp? Who would be responsible for responding to these alerts and what actions might they take?

Response here
```

## Oral Examination — Questions and Rubric

A **10 minute conversation** with the TA or the instructor during the week after the deadline, producing a **multiplier between 0.0 and 1.0** applied to your submission score.

Same format as the previous assignments. Nothing here is a secret — read it before you sign up.

### What the conversation looks like

You will be asked **three questions**, all anchored in **the code you actually submitted**. We will have your Pull Request open in front of us.

**Question 1 — Walk us through something you built.**

We pick one of: your `/subscribe` request path, your `/unsubscribe` error handling, the collaborator you factored out for DRY, or one of your two metrics endpoints. **We pick it, not you.** Be ready to say what it does, why you did it that way, and what you tried that did not work.

**Question 2 — A decision you made.**

Something specific in your code or your PR write-up. Which classes you chose to modify for the subscribe API; where you drew the line on what code to share between subscribe and unsubscribe; which classes `MetricsTracker` collaborates with and which you deliberately kept it away from.

**Question 3 — A concept from the bank, tied back to your code.**

From the bank below, and we will ask you to point at the line in your submission where it applies.

### Question bank

1. Walk us through what happens, class by class, from the moment `curl -i localhost:8000/subscribe/BAC` hits your server to the moment FinHub starts sending updates for **BAC**.
2. Where does your symbol validation live? Why there, and what other class could it have lived in?
3. Why does the requirements document call for a **400** in some error cases and a **409** in others? What is the difference to the caller?
4. The existing **symbols** endpoint and your new **subscribed-symbols** endpoint can return different answers at the same moment. Point at the code that explains why.
5. What code did you factor out into a shared collaborator for Problem 3.2? Show us a piece of code you deliberately did *not* share, and tell us why not.
6. What happens if a customer subscribes to an 11th symbol, unsubscribes from one, and subscribes again? Point at the code that makes that work.
7. Why does `MetricsWebServer` listen on its own port instead of adding the metrics endpoints to `BasicWebServer` on port 8000? What would be the trade-offs of doing it the other way?
8. Walk us through how a single FinHub update ends up changing the output of `updates-volume`. Which classes touch it along the way?
9. How does a customer's `/price` request end up incrementing a count inside `MetricsTracker`? Was there a way to do this without modifying the price handling code at all?
10. How did you implement the descending sort in your metrics endpoints, and how did you test it?
11. **updates-volume** measures our interaction with FinHub; **price-volume** measures customers' interaction with us. Describe a production scenario where one is high and the other is near zero — and what you would do about it.
12. Application monitoring vs infrastructure monitoring — which one did you build this week? Point at a metric you'd want that your code cannot see.

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

- you can navigate your code and describe what it does without guessing
- you can explain *why* you made a choice, not just *what* the code does
- you can reason about a condition the assignment did not ask you to handle

> **A note on AI assistance:** the [AI / LLM Policy](../../SYLLABUS.md#ai--llm-policy) applies to this assignment. The oral examination is where that policy has teeth. If you cannot explain code that appears under your name, the multiplier is where it shows up — regardless of whether the submission passes every automated check.
