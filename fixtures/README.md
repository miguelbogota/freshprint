# Fixtures

These JSON files are sample data, not a production database:

- `templates.json` lists product templates and their published versions.
- `engagements.json` holds lightweight metadata for several engagements. Each array item represents one engagement; the file itself is not one giant engagement.
- `template-diff-*.json` shows raw changes between template versions. Some files compare consecutive versions; others compare an older version directly with the latest one.
- `template-fragment-audit-ca-v5.json` is a small peek at real template content.

An engagement's `templateVersion` is the baseline for an update check. We compare it with the latest version in `templates.json`. A direct baseline-to-latest diff shows the final effect of several accumulated updates without making users read every intermediate change.

The diff format is simple: `add` has a new `value`; `replace` has `oldValue` and `newValue`; `remove` has `oldValue`. These records are examples, not full customer engagement files.
