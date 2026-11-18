# HW9: Persistence, StockApp Learns to Remember

**Posted:** Monday, November 9 · **Due:** Sunday, November 15 at 11:59 PM

Stop `finnhub-service` and start it again. Every subscription is gone. The subscribed symbol set lives in memory and nowhere else, so a deploy, a crash, or a laptop reboot erases it. This week you fix that: the subscribed symbol set will survive restarts, and you will implement that persistence in multiple different ways behind a single interface.

The state involved is tiny, a set of strings and timestamps, and that is the point. Implementing persistence several ways for a small piece of state teaches you the shape of the problem. The shape does not change when the state gets big.

The high level overview for this assignment:

1. we will implement persistence for the subscribed symbol set in **finnhub-service**
2. we will define an interface for this persistence
3. we will start with a **NO-OP** (no operation) implementation, a common way to introduce new code in a small increment without impacting any functionality
4. we will implement the interface with a **file based** solution
5. we will implement the interface with a **relational database** solution using a very simple, local database called [SQLite](https://www.sqlite.org/)
6. we will run **finnhub-service** with both implementations and confirm that the set survives restarts

> **NOTE:** this assignment is a single Pull Request from a branch named exactly `hw9`. Per the [syllabus](../../SYLLABUS.md#homework-assignments-55), a PR from a branch with any other name will not be graded.

Parts of this assignment ask you to explain things in writing. Answer those in the description of your Pull Request (PR). **A template specific to this assignment is included at the very end.**

> **Oral examination:** this assignment includes a 10 minute oral examination about your work, and your submission score is multiplied by how well it goes. Details at the end of this document.

Good luck! And please ask any questions in our Slack **#hw9** room.

## AI / LLM Policy for this assignment

HW8 was a one-week exception, and it is over. On this assignment you write everything yourself: all Java, all SQL, all Gradle, and every word of your Pull Request description. The course [AI / LLM Policy](../../SYLLABUS.md#ai--llm-policy) applies in full, and the oral examination assumes you can explain every line you submit.

## Getting Ready Best Practice

Same as always. Start from a clean, current `main` (which should now contain your merged HW8 work) and branch from there:

1. `git fetch`
2. `git checkout main`
3. `git pull`
4. `./gradlew clean build` to confirm latest `main` builds for you

Then create your branch:

1. `git branch hw9`
2. `git checkout hw9`

Commit incremental progress with `git commit -a -m "description of incremental progress"`.

> **NOTE:** all of your changes for this assignment belong in **finnhub-service**. This is a pure Java week. `webapp/` does not change at all.

## Problem 1: Introduce Interface, No-Op Implementation and Scaffolding (25 points)

### 1.1 Interface definition (5 points)

Introduce a new package, `edu.bu.finhub.persistence`, and a new interface in it called **SymbolsPersistence**. The interface supports three operations:

1. **`void add(String symbol)`** adds a new symbol to be persisted. Your persistence must record the time the symbol was added, in whichever way is appropriate for that persistence.
2. **`void remove(String symbol)`** removes a symbol from the set of persisted symbols.
3. **`Set<StoredSymbol> readAll()`** retrieves all symbols that have been persisted.

Where **StoredSymbol** is:

```java
package edu.bu.finhub.persistence;

import java.time.Instant;

/** Simple representation of a stored symbol and its creation date in storage. */
public class StoredSymbol {
  public final String symbol;
  public final Instant createdAt;

  public StoredSymbol(String symbol, Instant createdAt) {
    this.symbol = symbol;
    this.createdAt = createdAt;
  }
}
```

### 1.2 No-Op implementation (5 points)

Create a NO-OP implementation of the interface. NO-OP implementations let you break a problem into small increments: you identify the seams where the real functionality will go and instantiate the classes, without impacting any existing functionality yet.

This problem is a perfect fit for one, because the NO-OP will:

1. ignore adds
2. ignore removes
3. return nothing for **readAll**

That is exactly how **finnhub-service** behaves today: no persistence.

### 1.3 Identifying seams and integrating new code (10 points)

Identify every place in the current **finnhub-service** where it makes sense to call the three methods of your interface, and make those calls.

Think about inversion of control and dependency injection, which you have been practicing since HW3. Use the interface for as many declarations as possible. Reference the concrete No-Op class only at the single point where you create the implementation that gets injected into its collaborators.

We will **not** introduce persistence to `MockFinhubClient`. It works well as is, and there is no reason to add a persistence interaction to it. Only the real `FinHubWebSocketClient` gets persistence.

When this part is done, your code should run exactly as it did before, with no change to functionality, but with all the seams in place for your real persistence implementations.

### 1.4 Required change to FinHubWebSocketClient: make WebSocket initialization blocking (5 points)

One more change prepares us to initialize a set of subscriptions with FinnHub when the real client starts.

Look at how `FinHubWebSocketClient` initializes its WebSocket connection with the FinnHub API.

Our `StockUpdatesClient` interface has a **connect** method that gets called on start-up. You probably have not noticed, but the current implementation is asynchronous: `FinHubWebSocketClient.connect` calls `super.connect()`, which starts the WebSocket initialization but returns control to the invoker before it completes. This is called non-blocking initialization.

We need it to be blocking, because we want to subscribe to the persisted symbols on start-up. If control returns before the connection is established, we cannot issue the subscription commands and do not know when initialization completes.

Make two changes:

1. Rename the **connect** method on `StockUpdatesClient` (and all of its implementors) to **init**. The superclass of `FinHubWebSocketClient` has its own `connect` method, and as we switch from non-blocking to blocking that collision will confuse things. IntelliJ does this in one shot: right-click the method name, **Refactor → Rename** (Shift+F6), and it updates the interface, every implementor, and every call site.
2. In `FinHubWebSocketClient`, call the blocking connect method on its super class: **`super.connectBlocking()`**.

With these two changes in place, you can initialize your symbol set from persistence once `FinHubWebSocketClient` has been initialized.

## Problem 2: Implement Stale Subscription Filtering & Unit Test (10 points)

Regardless of which persistence implementation is in use, initialization must skip symbols that were persisted **more than 10 days ago**.

A nice consequence of starting with a No-Op implementation: you can add this filtering now, and unit test it, before any real persistence exists.

### 2.1 Implement filtering (5 points)

Wherever you call **readAll** during initialization of `FinHubWebSocketClient`, filter out symbols that were persisted more than 10 days ago.

### 2.2 Unit test the filtering (5 points)

Add a unit test for your filtering logic. This is the only new code in this assignment that should have a unit test, because it is the only business logic. Everything else is file system and database interaction. Remember the lesson from HW7's back-off problem: a "which of these are stale?" decision that is a pure function, separate from the code that does the I/O, is easy to test. Design for testability.

## Problem 3: Implement File Per Symbol Persistence (20 points)

In class we covered two file based strategies for persistence. Implement only the **file-per-symbol** strategy: a **FileBasedSymbolPersistence** implementation of your interface where each persisted symbol is a file, named for the symbol, in a dedicated storage directory. Some things to help you along:

- Use the [Path](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Path.html) java class to interact with the file system.
- If you call **`Paths.get("symbol-storage")`**, the expectation is that a folder called **symbol-storage** exists in the same parent directory as the root folder of your **finnhub-service** module. Create this folder manually, once.
- Use the [Files](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/Files.html) java class to perform operations on the `Path`s you create.
- **`Files.readAttributes(path, BasicFileAttributes.class).creationTime().toInstant()`** reads the time a file was created in the file system.

Once it is implemented, try it out end-to-end manually:

1. in one terminal window: `./gradlew queue-service:runService`
2. in another terminal window, start finnhub-service in non-mock mode: `./gradlew finnhub-service:runService`
3. subscribe to a symbol: `curl -i http://localhost:8004/subscribe/ORCL`
4. stop finnhub-service (Ctrl-C)
5. start finnhub-service in non-mock mode again
6. request the set of subscribed symbols: `curl -i http://localhost:8004/subscribed-symbols`. Did the symbol you subscribed to get returned?

## Problem 4: Implement Persistence Integration Test (10 points)

The manual check above proves it works today. An integration test proves it still works in December. Add the following test as **`finnhub-service/src/test/java/edu/bu/finhub/PersistenceIntegrationTest.java`**.

**Your FinHubWebSocketClient may not look exactly like the one in this test. In particular, the names of your methods for adding, removing, and listing subscriptions are your own from HW5. Adjust the test to work with your implementation.**

```java
package edu.bu.finhub;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import edu.bu.finhub.persistence.FileBasedSymbolPersistence;
import edu.bu.finhub.persistence.SymbolsPersistence;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;

@Tag("IntegrationTest")
public class PersistenceIntegrationTest {

  EnqueueingFinnhubResponseHandler mockEnqueueingFinnhubResponseHandler;

  @BeforeEach
  public void setUp() {
    mockEnqueueingFinnhubResponseHandler = mock(EnqueueingFinnhubResponseHandler.class);
  }

  @Test
  public void createRestartConfirm() throws URISyntaxException, InterruptedException {
    SymbolsPersistence symbolsPersistence = new FileBasedSymbolPersistence();

    FinHubWebSocketClient firstFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinnhubService.WEBHOOK_URI + "?token=" + FinnhubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    firstFinHubWebSocketClient.init();

    Logger.info("Removing ORCL to ensure clean slate");
    firstFinHubWebSocketClient.removeSymbol("ORCL");

    assertFalse(firstFinHubWebSocketClient.subscribedSymbols().contains("ORCL"));

    firstFinHubWebSocketClient.addSymbol("ORCL");
    firstFinHubWebSocketClient.closeBlocking();

    FinHubWebSocketClient secondFinHubWebSocketClient =
        new FinHubWebSocketClient(
            FinnhubService.WEBHOOK_URI + "?token=" + FinnhubService.API_TOKEN,
            mockEnqueueingFinnhubResponseHandler,
            symbolsPersistence);

    secondFinHubWebSocketClient.init();

    assertTrue(secondFinHubWebSocketClient.subscribedSymbols().contains("ORCL"));

    secondFinHubWebSocketClient.close();
  }
}
```

Read the test before you run it. It is a restart in miniature: the first client removes ORCL to guarantee a clean slate, adds it, and shuts down. The second client is a brand new object whose only connection to the first is the persistence store. If the final assertion passes, your symbols are genuinely surviving a restart.

If `finnhub-service` does not already declare JUnit and Mockito test dependencies in its `build.gradle`, copy them from `monolith-service/build.gradle`. JUnit rode along when you moved your unit tests in HW7, but Mockito may not have.

You should now be able to run the integration test from IntelliJ to confirm that your file based persistence works. Additionally, add the following task to **`finnhub-service/build.gradle`**:

```groovy
task executePersistenceIntegrationTest(type: Test) {
    useJUnitPlatform {
        includeTags "IntegrationTest"
    }
    testLogging {
        showStandardStreams = true
    }
}
```

Now you can run the test from the command line as well: `./gradlew finnhub-service:executePersistenceIntegrationTest`. Make sure it passes before moving on.

## Problem 5: Database Based Persistence (30 points)

Now we add a second real implementation of `SymbolsPersistence`, this time backed by SQLite. SQLite is a very light-weight database that is most likely already installed on your machine. It is a great option for local development and database integration testing.

### Install SQLite

Confirm that SQLite is installed:

```
sqlite3 --version
```

If it is not, follow the instructions [here](https://www.sqlite.org/download.html) and install pre-compiled binaries for your operating system. If you are on Windows and having trouble, the instructions [here](https://www.tutorialspoint.com/sqlite/sqlite_installation.htm) work well. Make sure the command above works before proceeding.

### Add dependency to build.gradle

Add the following to the **dependencies** section of **`finnhub-service/build.gradle`**:

```groovy
implementation 'org.xerial:sqlite-jdbc:3.41.2.1'
```

### 5.1 Define your schema (10 points)

In class we discussed options for a relational schema for the subscribed symbols set. Decide on your schema and apply it to your local SQLite database:

From your **finnhub-service** directory, run **`sqlite3 symbolpersistence.db`** (`symbolpersistence.db` is the file where SQLite will store this data). You are now at the SQLite command prompt. Enter your SQL to define your table here.

Run **`.tables`** to confirm the table exists. You can also run **`SELECT * FROM <table-name>`**, or inspect your definition with **`.schema <table-name>`**.

**In your PR** (template below): include the SQL you used to define your table(s).

### 5.2 Implement SQLiteSymbolPersistence (20 points)

You are implementing the same three methods as the file based persistence, as enforced by the `SymbolsPersistence` interface. Once this implementation is in place, switching between the two persistences is a one line change, and the rest of your code works as is.

We will use Java's JDBC (Java DataBase Connectivity) classes to create and manage connections to the local SQLite database. Hints:

- Every method of your implementation will want to use [DriverManager.getConnection()](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DriverManager.html) with the connection string **`jdbc:sqlite:symbolpersistence.db`**. That string tells the generic JDBC code you are using SQLite, and which SQLite file holds the data.
- **getConnection** returns a JDBC [Connection](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/Connection.html). It implements **AutoCloseable**, and you must make sure it gets closed no matter what happens, just as we have been doing with our output streams.

Once it is implemented, switch your code to use it instead of the file based persistence and repeat the manual test:

1. in one terminal window: `./gradlew queue-service:runService`
2. in another terminal window, start finnhub-service in non-mock mode: `./gradlew finnhub-service:runService`
3. subscribe to a symbol: `curl -i http://localhost:8004/subscribe/ORCL`
4. stop finnhub-service (Ctrl-C)
5. start finnhub-service in non-mock mode again
6. request the set of subscribed symbols: `curl -i http://localhost:8004/subscribed-symbols`. Did the symbol you subscribed to get returned?

Then make the one line change in the Problem 4 integration test to use the database persistence, and make sure it also passes.

## Problem 6: Filtering Decision Question (5 points)

When you implemented your database persistence, did you filter stale symbols right in the SQL that reads all symbols, or did you leave the WHERE clause out and let the filtering logic you already have in code remove them?

**In your PR:** explain the pros and cons of each approach.

## Submitting this assignment

Same as previous assignments. Make sure everything is committed with `git commit -a -m "<description>"`, and check `git status` for anything outstanding.

Also check `git status` for files that should **not** be in your commit: `symbolpersistence.db` and the contents of your `symbol-storage` directory are local state, not source code. Add them to `.gitignore`. A database file committed to a repository is a classic mistake. Do not make it.

Before pushing, run `./gradlew preCommit`. It cleans, reformats, compiles, runs static analysis, and executes the unit tests across every module. If it passes, your branch is in good shape.

Push with `git push --set-upstream origin hw9`, or just `git push` if the branch already exists remotely.

Then create a Pull Request from GitHub by clicking **Compare & pull request** for your new branch.

> **IMPORTANT: make sure the base of the pull request is your fork and not the base for the class.** Throughout this course you only create Pull Requests against, and merge code into, your own repository.

Remember to invite the `staff` team as a reviewer.

### GitHub Pull Request Template for this Assignment

```markdown
## Problem 5: Database Based Persistence
### Problem 5.1: Database persistence schema

<put your SQL code for defining your symbols persistence table(s) here>

## Problem 6: Filtering trade-offs
When you implemented your database persistence, did you filter stale symbols right in the SQL that reads all symbols, or did you leave the WHERE clause out and let the filtering logic you already have in code remove them?

<explain the pros and cons of each approach here>
```

## Oral Examination

A **10 minute conversation** with the TA or the instructor during the week after the deadline, producing a **multiplier between 0.0 and 1.0** applied to your submission score.

HW9 oral examinations run **Tuesday, November 17 through Friday, November 20**. Claim a slot on the **[hw9.md sheet](https://github.com/BU-EC427-Fall2026/review-sign-ups/blob/main/hw9.md)** in the review-sign-ups repository, which is live now.

Same format as recent weeks: there is no published question bank. We will have your Pull Request open in front of us, we will pick **specific lines of your submitted code**, and we will ask you about them: what they do, why you wrote them that way, and what would happen under a condition the assignment did not spell out. This week that includes your schema. Expect to be asked about a column you defined and what breaks if it goes away.

You will be asked **three questions**. There is nothing to study beyond your own submission: if you wrote it and understand it, you are prepared.

### How your multiplier is calculated

You start at **1.0**.

- **Answer all three questions well → 1.0.**
- **If you cannot answer a question, we ask a follow-up**, a smaller, more concrete version of the same question. Answer that well and you lose only **0.05** for that part.
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

> **A note on AI assistance:** the no-AI expectation at the top of this assignment is enforced here. If you cannot explain code that appears under your name, the multiplier is where it shows up, regardless of whether the submission passes every automated check.
