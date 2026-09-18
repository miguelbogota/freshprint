# Freshprint — Product Context

<!-- impeccable:product-schema 1 -->

## Platform

web

## Users

Firm staff are the primary users. They need to spot engagement files affected by newer product templates, understand the changes, and decide whether to Apply or Decline an update. Portfolio reviewers are a secondary audience for this implementation, not the people the in-app workflow is designed around.

## Product Purpose

Freshprint makes template updates visible and reviewable without opening every engagement file. A successful user can find pending files, read a plain-language summary of the effective changes, and record a decision against the versions they reviewed.

## Positioning

The useful mechanism is a fast metadata index and template-derived summaries: opening a full engagement can take about a minute, so the update list does not open full files. When several template versions have accumulated, the summary describes the net change from the engagement's recorded version to the latest one.

## Operating Context

An engagement is a firm's working file created from a shared product template. Templates can change after an engagement is created. Firm staff review pending updates in an Angular dashboard, then send Apply or Decline. The Spring Boot server returns an accepted operation quickly; the UI checks for its eventual result.

## Capabilities and Constraints

- The dashboard lists engagements, supports search and filtering, shows change summaries, and submits versioned decisions.
- Apply advances the indexed template version after successful processing. Decline retains the current version and suppresses the same offer until a newer template is published.
- The current app demonstrates the decision flow; it does not merge template content into an engagement or store customer-entered form values.
- The local demo uses bundled template fixtures and an H2 metadata/operation store. Authentication, firm isolation, durable hook consumers, and full-file merging are not implemented.
- The repository also contains the original take-home design and submission notes. Those describe the scoped exercise; this branch explores a connected app beyond it.

## Brand Commitments

The product name is Freshprint. Existing repository copy aims to explain the workflow in simple, casual language. No visual identity or binding aesthetic direction has been confirmed for future design work.

## Evidence on Hand

The repository includes JSON fixtures, an Angular dashboard, a Spring Boot API, tests, and architecture documentation. The displayed firms and engagements are demo data; they are not customer proof or testimonials.

## Product Principles

- Make pending work obvious without making users wait for full-file reads.
- Explain the effective change before asking for a decision.
- Keep accepted decisions distinct from completed decisions.
- Be honest about missing, stale, or unavailable information.
- Keep demo behavior and production claims clearly separated.
