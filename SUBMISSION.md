# Submission Notes

## Assumptions Made

- Template versions increase in order within a product template.
- A reliable JSON diff can be generated directly between any two versions of the same template.
- Template publication and engagement lifecycle actions can trigger hooks or events.
- Hooks provide durable, retryable delivery so the metadata index can be kept up to date without reopening engagement files. Event handlers are idempotent, failed events can be retried, and a periodic reconciliation process detects and repairs any missed updates.
- The engagement management system remains the source of truth for complete engagement files. The proposed metadata index stores only the small set of fields needed to determine update status.
- Existing engagements can be indexed asynchronously or when they are normally opened. Until their metadata is available, the system reports their state as `UNKNOWN`.
- The targeted implementation has no previous Apply/Decline history and treats the engagement's recorded template version as its baseline, as required by the exercise.
- The Java and Angular submissions are intentionally separate excerpts. They follow the same JSON contract but do not communicate over HTTP.
- Applying template content, migrating customer answers, supplying default values, and rolling back an update are outside scope.

## AI Usage

### Where AI helped

I used ChatGPT/Codex as a design and implementation assistant. So far, it has helped me:

- Break the problem into the template, engagement, metadata-index, diff, summary, and client responsibilities.
- Explore the effect of the one-minute engagement-loading constraint.
- Compare possible locations for the diff-to-summary transformation.
- Draft and refine the architecture diagram and JSON API examples.
- Identify edge cases such as accumulated versions, unavailable summaries, stale decisions, missed events, and existing unindexed engagements.
- Keep the design aligned with the limited Java and Angular excerpts requested by the exercise.

I plan to use AI during implementation for focused suggestions, test-case brainstorming, and code review. I will verify all generated code and keep only code I can explain and defend.

### Where I corrected, rewrote, or ignored AI output

The first proposed design was broader and more formal than this exercise needed. I asked for it to be rewritten in simpler language and reduced its scope so it fits the time limit and is easier to defend.

I also reviewed and changed several design choices during the discussion:

- I chose JSON examples instead of TypeScript definitions for the client/server contract.
- I clarified that `engagements.json` is fixture metadata representing multiple engagements, not the production storage format or one complete engagement file.
- I kept the Java and Angular excerpts disconnected instead of adding a Spring/HTTP layer that the instructions explicitly exclude.
- I chose backend transformation of raw diffs because it is shared business interpretation, while Angular remains responsible for presentation and interaction.
- I selected a precompute-plus-on-demand strategy for summaries rather than recalculating them on every request.
- I removed or postponed speculative features such as draft template migrations and detailed merge behavior because applying template content is outside scope.

Before submitting, I will update this section with any generated implementation that I rewrote or rejected and the reason for doing so.

### How I would guide other engineers using AI on this system

- Give the tool a small task with the relevant domain types, contract, and fixture rather than asking it to invent the whole system.
- Require deterministic tests for every generated business rule, especially version comparisons and diff summarization.
- Review suggestions against the source template schema and product terminology.
- Do not provide customer engagement content, answers, or other sensitive data to an unapproved AI service.
- Treat AI output as a proposal. The engineer remains responsible for correctness, security, tenant isolation, and operational behavior.
- Record important prompts or decisions when AI materially influences production code so the reasoning can be reviewed.

### Where AI should not be trusted in this domain

AI should not independently decide whether an update is professionally or legally significant, whether an accounting procedure can be removed, or whether a customer's existing work remains compliant. It may produce fluent but incomplete summaries, omit a raw change, or invent an implication that is not present in the template diff.

For that reason, the main summary path is deterministic and traceable to the raw diff. Unknown paths remain visible for review rather than being guessed or silently removed. Human domain experts remain responsible for template content and professional judgment.

## Approximate Time Spent

**TODO before submission:** Replace this note with the final total and a short breakdown for design, Java, Angular, testing, and documentation.

## What I Would Do Next

If I had more time after the targeted implementation, I would:

1. Implement the event-driven engagement metadata index and an asynchronous backfill for existing engagements.
2. Add the publication-triggered summary cache with on-demand generation for missing version combinations.
3. Expose the documented API and replace Angular's in-memory gateway with an HTTP implementation.
4. Add contract and integration tests for delayed or duplicate events, stale decisions, tenant isolation, and reconciliation.
5. Design the actual Apply workflow, including preserving customer answers, validation, default values, partial failure, and rollback.

**TODO before submission:** Reorder or replace these items based on what remains incomplete at the end of the exercise.
