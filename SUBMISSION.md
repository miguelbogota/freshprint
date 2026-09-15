# Submission Notes

## Assumptions

- The recorded engagement version is the baseline; targeted Java has no decision history. Published version numbers increase but may skip values.
- The template system can quickly compare any two versions of the same template and produce a direct JSON diff.
- Four durable hooks exist: `CreateTemplate` initializes template metadata; `UpdateTemplate` publishes a version and precomputes summaries; `CreateEngagement` indexes the initial template/version; `UpdateEngagement` refreshes it after successful Apply. Handlers retry, deduplicate, and reconcile missed events.
- The one-minute limit is for loading **engagements**, not templates. Lists use cached engagement metadata; summaries use cached or newly generated template data.
- Engagement IDs/names can be listed without a full load. Old files are indexed in the background or during normal loads; until then they show `UNKNOWN`.
- Full files stay in customer-specific storage. The firm-scoped index is a copy; shared template data/caches hold no customer answers.
- Apply/Decline return a quick `ACCEPTED` receipt and finish asynchronously. The UI does not wait for completion; indexed metadata changes only after successful Apply.
- Java and Angular are separate, no-HTTP excerpts. Requiring a ready summary before a decision is my Angular demo safety choice, not a prompt requirement.

## AI Usage

### Where it helped

I used ChatGPT/Codex to discuss the one-minute loading constraint, sketch the metadata-index and summary-cache design, draft the JSON contract, and build the focused Java and Angular examples. It helped surface edge cases such as several accumulated versions, missing metadata, stale decisions, and delayed events. I checked the code with the Java and Angular tests and builds, then reviewed the wording against the domain problem.

### What I changed or rejected

I cut an early design that was too broad for a short exercise. In Java, I removed a Strategy pattern that made three simple summary rules harder to follow, and I changed pending-version counting to use the published-version list. I kept JSON loading in tests instead of mixing it into the domain code. In Angular, I removed generated starter UI and CSS, kept the app disconnected from Java as instructed, and replaced technical status codes on the screen with plain-language labels. I also kept `ACCEPTED` separate from an update actually being applied.

### How I would use AI with a team

I would give it small tasks with the contract and sample diff, ask for deterministic tests, and have an engineer review every business rule against real template content. We should not send customer answers or full engagement files to an unapproved AI service. AI output is a proposal; engineers own correctness, privacy, firm isolation, and operational behavior.

### Where I would not trust it

I would not let AI make final calls on payments, security controls, authentication, firm isolation, secrets, or production deployment. Even convincing-looking code can leak data, mishandle retries, or skip a failure path. Those decisions need human review, threat modeling, and tests; customer data should not be sent to an unapproved AI tool. Payments are not part of this take-home, but the same rule would apply if Freshprint became a real app.

## Approximate Time Spent

About **10 hours elapsed** from starting last night to finishing today, but that includes sleep and work at my current job. Active work was about **3 hours 15 minutes**: roughly 2 hours reading and validating the requirements and architecture, 1 hour implementing the Java and Angular excerpts, and 15 minutes cleaning up code and documentation.

## What I Would Do Next

1. Turn the plain Java logic into a real HTTP server with the metadata index, four hooks, summary cache, and async Apply/Decline operation tracking.
2. Replace Angular's hardcoded gateway with the API and make the screen feel like a real product: clearer loading/error states, better layout, accessibility, and visual polish.
3. Add end-to-end tests for firm isolation, stale decisions, missed events, and failed operations before building the actual template merge.
