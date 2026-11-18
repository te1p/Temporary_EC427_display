# EC427 - Software Engineering in Practice - Fall 2026

**Instructor:** Ed Solovey, PHO 531 — [esolovey@bu.edu](mailto:esolovey@bu.edu)

**Lectures:** Monday/Wednesday 12:20 - 2:05, PHO 205

## Staff

| Photo | Name | Role | Email | Office Hours |
|---|---|---|---|---|
| <img src="images/staff/ed-solovey.jpg" alt="Ed Solovey" width="100"> | Ed Solovey | Instructor | [esolovey@bu.edu](mailto:esolovey@bu.edu) | TBD |
| <img src="images/staff/hank-wang.jpg" alt="Hank Wang" width="100"> | Hank Wang | Teaching Assistant | [hankwang@bu.edu](mailto:hankwang@bu.edu) | TBD |

## Course Objectives

This class builds on students' software design skills by introducing them to an array of industry relevant concepts such as test driven development, effective understanding and debugging of existing codebases, use of IDEs, re-factoring, source control management, microservice architecture, intraservice communication, continuous deployment, data storage, fault tolerance and isolation, and system scaling.

Each week, students will get hands-on experience with putting these concepts to practice. We will start with an existing, monolithic application that reads data from a public API and computes analytics based on this infinite data stream and then exposes the results via a web interface. We will then use the concepts above to gradually tease apart and improve this application by separating it into smaller, single purpose microservices, all the while ensuring that functionality is maintained, and scalability and maintainability are improved.

Finally, we will cover product requirements and team collaboration as we add functionality to our microservices, culminating in the deployment of the microservices to a cloud environment. The programming assignments for this course will be primarily in Java.

Software engineering is undergoing a profound transformation driven by generative AI, and nowhere is that transformation more visible than in the day-to-day practices this course is about. For each topic we cover, we will examine how generative AI is changing the way working engineers engage with it — and why mastering the topic remains essential. AI assistants can produce code with remarkable speed, but they cannot take responsibility for it. Engineers who deeply understand testing, architecture, deployment, and the behavior of large systems are the ones equipped to direct these tools effectively, evaluate what they produce, catch what they get wrong, and make the judgment calls they cannot.

## Prerequisites

- EC 327
- Familiarity with Java is not required, but comfort coding in any language is

## Course Outcomes

As an outcome of completing this course, students will:

- Be comfortable in navigating, understanding, and modifying existing, large code bases as well as building new ones from scratch.
- Be able to re-factor code with confidence due to their appreciation for testing philosophies and code coverage metrics.
- Appreciate the benefits of Continuous Deployment and the need for Observability and Monitoring, and be able to implement these for large systems.
- Understand software engineering processes and learn how to best collaborate within software teams.
- Be comfortable in a distributed systems environment and learn to build & deploy microservices.
- Be able to articulate trade offs around software architecture choices and guide others towards decisions.
- Understand how generative AI is changing the practice of software engineering, topic by topic, and why deep foundations are what make those tools safe and effective to use.

## Topics

1. Testing Philosophies & Methodologies
2. Code Probing, Debugging, and Execution Environments
3. Source Control & Static Analysis
4. Observability & Monitoring
5. Writing self-documenting, easy to maintain code
6. Object Oriented Design Principles
7. Continuous Deployment
8. Software Processes
9. Software Architecture
10. Scalability
11. Software Performance
12. User Centric UX Design
13. Building UX with React
14. Technical Debt & Refactoring
15. Distributed Systems
16. Concurrency & Async Models
17. Cloud Computing
18. Software Engineering Interviews

## Textbooks & Readings

Because software engineering best practices evolve so quickly, there is no single textbook that we will be using. Rather, readings from a variety of sources will be assigned prior to each lecture.

- **Common Reading Sources:**
  - [Software Engineering at Google, O'Reilly — free online](https://abseil.io/resources/swe-book)
    - Note: not everything in this book is relevant to our class or necessarily a best practice. We'll focus on selective readings from this book.
  - [Martin Fowler](https://martinfowler.com/) — a blog with a collection of great posts on software engineering topics over the last 20+ years.
- **Reference Materials:**
  - Java: [CodeAcademy Java intro class (free)](https://www.codecademy.com/learn/learn-java), [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
  - IntelliJ IDE: [JetBrains Getting Started](https://www.jetbrains.com/help/idea/getting-started.html)
  - Gradle Build System: [Gradle Tutorial](https://docs.gradle.org/current/userguide/getting_started_eng.html)

## Assignments, Announcements & Course Material

- [**This repository**](https://github.com/BU-EC427-Fall2026/assignments) — everything for the class in one place: this syllabus, the StockApp code you will spend the semester evolving, and the homework assignments in [hw/](hw/). Before the first class, you will receive a form collecting your GitHub username; we will use it to invite you to the class organization so you can fork this repository. New assignments arrive here, so each week you will pull them down from this repository into your own fork — a habit that is itself part of what you are learning.
- **The [review-sign-ups repository](https://github.com/BU-EC427-Fall2026/review-sign-ups)** — a separate, small repository that every student can write to, hosting the weekly oral examination sign-up sheets. You will be invited to it at the same time. See [Oral Examination Logistics](#oral-examination-logistics).
- **Blackboard** ([learn.bu.edu](http://learn.bu.edu)) — assigned readings, lecture materials, grades, and the updated schedule.

## Evaluation

Before we get to how grades break down, an honest disclaimer: we are well aware that today's LLMs can easily produce the code that the assignments in this class require. That does not make the assignments pointless — but it does change how we evaluate them.

The full ground rules for LLM use are collected in the [AI / LLM Policy](#ai--llm-policy) section below. The short version: **unless an assignment says otherwise, the default expectation is that you are writing the code by hand.**

Because of this, every homework assignment in this class consists of two parts:

1. a **submission** of material — most often code. This part is graded by an automated test suite. Testing is a central theme of this class, some of the test are published as part of each assignment, you will also be responsible for writing additional tests, reading and understanding the tests is part of the overall comprehension that you are building.
2. a short **oral examination** — a roughly 10 minute conversation with the TA or the instructor about your submission (details below). HW1, an environment setup assignment, is the one exception and has no oral examination. This part is graded according to a very detailed rubric that will be transparent to you.

Your grade for each assignment will be the score for your submitted material **multiplied by** a value between **0.0** and **1.0** that reflects the understanding you demonstrate in the oral examination. In other words, a perfect submission you cannot explain is worth close to nothing — in this class, and in your career.

### Grade Breakdown

- **15%** - in-class responses to interactive Poll Everywhere questions
- **55%** - homework assignments
- **30%** - closed book midterm

You will notice that there is **no final exam** this semester. Closed book exams are not representative of how you will work as an engineer after graduation, so we are keeping only one — the midterm — as an individual checkpoint. In exchange, the homework assignments carry most of your grade and are more ambitious than in past semesters: with no final to study for, the last weeks of the class belong to the most challenging and most industry-like assignments.

#### In Class Poll Everywhere questions (15%)

To keep our lecture conversations interactive and to check understanding in real time, we will pause for about 5-10 minutes twice per session and go through two or three multiple-choice questions covering the material that we just went over during that lecture. Each of you will respond to these questions via any internet connected device. After each question, we will ask any volunteer who is comfortable sharing their correct answer with the class to explain the answer.

Your grade on these questions will be determined as follows:

- Throughout the course, we will have a total of roughly 75 such questions.
- You will start out with 25 points.
- You will get 1 point for each correct answer.
- We will also post weekly feedback surveys that should take you no more than a few minutes to fill out. Honest answers on these surveys will also contribute 1 point per survey towards this score.

15% of your grade will be determined by the number of points you have at the end of the course out of 75. For example: answer 50 of the 75 questions correctly, and with your 25 base points you have 75/75 — a full 100% for this part of your grade. No extra credit will be given if you go over 100%. If you miss a lecture and don't get to participate in the questions during that lecture, that is ok - the intent of the base 25 points is to acknowledge the fact that you might miss a lecture during the semester and to reduce the pressure of getting all of these interactive questions correct.

We will use Poll Everywhere for these questions. The first time you go to the address for our questions - https://pollev.com/edsolovey024, you will be prompted to create a **screen name**. Your screen name will be your **BU Student ID**.

#### Homework Assignments (55%)

Homework assignments run on a **Monday-to-Sunday cycle**: each assignment is posted on Monday morning and due at **11:59 PM that Sunday**. Both the Monday and Wednesday lectures land while the assignment is open, so bring your questions to class after you have attempted the work — and the weekend sits at the *end* of the cycle, giving you uninterrupted time to finish.

Some of the more involved assignments will be given **two weeks** instead of one. Every assignment states its due date when it is posted — the due date on the assignment is always the authority.

##### Submission: GitHub

Each assignment will be submitted via a GitHub Pull Request against your fork of this repository — setup instructions will be covered in the first week of class. **Your Pull Request is your submission** — there is nothing to turn in anywhere else, and the staff finds submissions by looking for Pull Requests on branches named exactly for the assignment (`hw1`, `hw2`, `hw3`, ...). A Pull Request from a branch with any other name will not be graded. No exceptions.

The Pull Request must be created prior to 11:59 PM on the Friday that it is due. It may be edited as many times as you would like prior to the deadline. Edits made after the deadline will disqualify the submission from being graded and will result in a zero for the assignment. Your feedback and grades are delivered as comments on your Pull Requests. Because the assignments in this class build on one another — each week you continue evolving the same application — you should **merge each Pull Request once it has been approved**. Merging brings everything you have built so far into your `main` branch, which is exactly where the next assignment picks up.

You do not need to wait for a merge to keep moving, though. It is perfectly fine — and exactly how engineers work in industry — to start the next assignment before the previous Pull Request has been merged. Create the next assignment's branch from `main` as usual; most assignments do not have hard dependencies on the previous week's changes. Once the earlier Pull Request merges, simply merge `main` into your new branch to pick up that work.

##### Automated Feedback and Grading

Every assignment will execute automated tests against your submission. Because testing is a central theme of this class, the grading test suite is fully transparent: it is published as part of each assignment, and you can (and should) run it yourself against your code before you push. Some assignments will go further and ask you to write tests of your own as part of the submission. The grading suite also runs automatically against your Pull Request, and its results will be posted to your submission Pull Request within a few minutes of you making changes to it. The results will contain either an "All Success!" message, which means you will score a 100% on the automated grading part of the assignment, or feedback about which tests are failing. Each assignment will indicate how many points are deducted for each failing test. You are allowed to fix your code and make changes to your Pull Request as many times as you would like before the deadline.

##### Oral Examination

To receive credit for an assignment, you will have a **10 minute conversation** with the TA or the instructor about your submission during the **week immediately after it is due** — any slot from Monday through Friday.

**HW1 is the single exception.** It is an environment setup assignment with no code to discuss, so it is graded purely on completion and has no oral examination. Every assignment from HW2 onward does.

Your grade for each assignment is the score for your submitted material **multiplied by** a value between **0.0** and **1.0** that reflects the understanding you demonstrate during this oral examination. Expect to be asked to walk through your code, explain why it is structured the way it is, and reason about how it would behave under conditions the assignment did not test.

The whole week is open, but **earlier is better, and that part is up to you**. An examination early in the week gets you your examiner's feedback with days to spare before the next assignment is due on Sunday — enough time to adapt how you are working, not just what you submitted. Wait until the end of the week and you will still get full credit for the examination, but the feedback will arrive too late to be of much use on the assignment you are already working on. Pick your slot accordingly.

**Scheduling:** oral examinations are scheduled through a weekly sign-up sheet in the **[review-sign-ups repository](https://github.com/BU-EC427-Fall2026/review-sign-ups)**. Each assignment's sheet is posted there during the week the assignment goes out, listing every available 10 minute slot across the instructor's and the TA's examination blocks for the week after the Sunday deadline; claiming one is a single commit, first come, first served. Step-by-step instructions and the sign-up rules live in that repository's README. See also [Oral Examination Logistics](#oral-examination-logistics) at the end of this document.

If you do not complete your oral examination by the end of that week, your multiplier is **0.0** for that assignment. No exceptions will be made on this policy — but as noted below, your single lowest homework grade is dropped automatically.

##### Late Policy and Lowest Grade Drop

No late assignments will be accepted for any reason. However, the lowest grade that you received on a homework assignment throughout the semester will be dropped at the end of the semester. You won't have to do anything for the lowest grade to be dropped. Please don't ask for extensions on any of the assignments as none will be granted. The intent of the lowest grade being dropped is to acknowledge the fact that unexpected things come up in life and to give you a pass on your worst week of the semester.

#### Closed Book Midterm (30%)

The midterm is the one closed book exam in this class. It will be held in class around the midpoint of the semester, and the exact date will be announced well in advance, in lecture and on Blackboard.

Why keep one closed book exam after saying they are not representative of how you will work in industry? Because the concepts this course builds have to live in your head, not in a browser tab. When you are debugging a production incident or evaluating an architecture an LLM just proposed, you do not get to stop and re-learn the fundamentals — you either have them or you do not. The midterm is our one individual, no-tools checkpoint that they are there.

There will be no surprises on the midterm: it covers the same material as the lectures, the Poll Everywhere questions, and the homework assignments. If you have been writing your homework code yourself and your oral examinations have been going well, you are already doing the best possible preparation.

Closed book means closed book: **being caught with a phone out at any point during the exam will result in an automatic zero — no questions asked.** Leave it in your bag, powered off.

## AI / LLM Policy

You will use AI assistants throughout your career — many of you already do. This class is not going to pretend otherwise, and it is not going to treat these tools as the enemy: how generative AI is changing software engineering is one of our central themes, and we will return to it topic by topic all semester. But we do need to be clear about how these tools fit into your graded work in this class, and more importantly, why.

The rules are short:

- **Unless an assignment explicitly says otherwise, you are writing the code by hand.** Each homework assignment will state clearly when, and for which parts, LLM use is permitted.
- **Every line of code and every word you submit must be understood by you.** This holds no matter what an assignment permits: where LLM use is allowed, you are just as responsible for that code as you would be if you had typed it yourself. The weekly oral examinations are where you demonstrate this — remember that your submission score is multiplied by your understanding multiplier, so a perfect submission you cannot explain is worth close to nothing.
- **Cite your tools.** Where an assignment permits AI assistance, acknowledge it in your submission, just as you would credit a classmate or a website you borrowed from. Submitting LLM-produced code outside of what an assignment explicitly permits is plagiarism under the academic integrity policy below.

Here is the why. This policy is not about catching you — it is about making sure you leave this class with the instincts this course exists to build. The practices we cover — testing, debugging, refactoring, architecture — only become instincts when you work through problems yourself. There is no shortcut through the struggle, and the struggle is where the learning happens. AI assistants can produce code with remarkable speed, but they cannot take responsibility for it; the engineers who thrive alongside these tools are the ones who can direct them, evaluate what they produce, and catch what they get wrong.

Skip that work by having an LLM blindly do your assignments, and the gap will catch up with you — in the oral examinations every single week, on the midterm, and eventually at a job. The assignments that are meant to be done without LLM aid are deliberately sized so that you can do them by hand. When you do, the oral examination becomes ten easy minutes of describing something you built and understand.

Where we genuinely encourage LLMs is as a tutor. Outside of writing your graded code, use them freely: ask for another explanation of something from lecture, have them generate extra practice problems, ask why your build is failing or your test won't pass. Keep in mind that they are sometimes confidently wrong — check what they tell you against the lecture material, the assigned readings, your own understanding, or a classmate's understanding. But keep your struggle for yourself: if you ask for the answer before wrestling with the problem, the answer won't teach you anything.

One last note: generative AI is moving quickly, and this policy may need to move with it during the semester. If it does, we will announce the change in lecture and on Blackboard, and this document will always contain the current version.

## Course Policies

The most successful software engineers are also the most collaborative ones. No one innovates by themselves in a vacuum. We are not going to sacrifice collaboration in this class. However, it is important to evaluate your individual progress in this class. To that end, students are encouraged to collaborate with all of their classmates as much as possible, brainstorm ideas, help each other with set up, and getting around bugs. Additionally, certain assignments will encourage/require peer code reviews.

On LLMs: the ground rules are laid out in the [AI / LLM Policy](#ai--llm-policy) section above, and the universal rule bears repeating: **every line of code and every word you submit must be understood by you.** That is exactly what the oral examinations verify, and it is the bar that matters in this class and in your career.

- **Academic integrity**
  - The homework assignments must be the result of your individual work. You may discuss the content, general approach, and challenges of the assignments with your classmates, but not the detailed solution. You are expected to formulate your approach and write your solutions yourself. Copying a solution or answer from another student or source is cheating — and understanding code you copied does not make it yours. Code produced by an LLM may be submitted only where an assignment explicitly permits it, and you must understand it just as deeply as code you wrote yourself.
  - **You may not collaborate in any way on the midterm exam.**
  - Clearly reference any sources you used in your work: books, internet, and your collaborators! This includes websites (e.g. Stack Overflow) and, where an assignment permits their use, AI assistants. **Note that copying code or answers from such sources, or from another student, outside of what an assignment explicitly permits, is considered plagiarism.**
  - Boston University's academic code of conduct, https://www.bu.edu/academics/policies/academic-conduct-code/, will be strictly applied.
- **Copyright**
  - All class material is copyrighted, and may not be shared publicly online by any means. This includes your own solutions to assignments.

## Inclusion

We consider this classroom to be a safe and supportive place where you will be treated with respect, and we welcome individuals of all ages, backgrounds, beliefs, ethnicities, genders, gender identities, gender expressions, national origins, religious affiliations, sexual orientations, ability — and other visible and nonvisible differences. All members of this class are expected to contribute to a respectful, welcoming and inclusive environment for every other member of the class.

## Accommodations for Students with Documented Disabilities

If you believe you might have a disability that requires accommodations, requests for accommodations must be made in a timely fashion to Disability & Access Services, 25 Buick St, Suite 300, Boston, MA 02215; 617-353-3658 (Voice/TTY). See established policies and procedures: http://www.bu.edu/disability/accommodations/

## Oral Examination Logistics

Everything you need to know about the weekly oral examinations, in one place:

- **When:** any slot during the **week immediately after each assignment's Sunday deadline** — Monday through Friday. HW1 is the exception: it has no oral examination. Earlier slots get you your feedback while it can still change how you work on the next assignment; a Friday slot still earns full credit for the examination, but the feedback arrives too late to help. The trade-off is yours to make.
- **How long:** 10 minutes, one-on-one, with either the TA or the instructor.
- **Where:** in person, in the examiner's office or an announced room. (If an individual week requires it, a slot may be held over Zoom at the examiner's discretion.)
- **Sign-up:** sign-up sheets live in the dedicated **[review-sign-ups repository](https://github.com/BU-EC427-Fall2026/review-sign-ups)** — separate from this one — that every student in the class has write access to. Each assignment's sheet is posted there during the week the assignment goes out; claiming a slot is a single commit, first come, first served. **Your commit is your confirmation.** Step-by-step instructions and the sign-up rules are in that repository's README.
- **Examination blocks:** the instructor and the TA will each hold recurring blocks spread across the week, including blocks on the afternoons when you are already in the building for lecture. Exact times will be announced in the first week of class. Between the two examiners there will always be more slots than students.
- **What to bring:** nothing but yourself. Your submitted Pull Request will be on the examiner's screen.
- **What is asked:** questions about *your* submission — why you wrote what you wrote, what would happen if conditions changed, and how the week's concepts show up in your code. The rubric is published with each assignment.
- **If you miss your slot:** your multiplier for that assignment is 0.0. Sign up early in the week and treat the slot like a standing work meeting.
