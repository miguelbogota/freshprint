# Supporting Fixtures

These fixtures provide representative template metadata, engagement metadata, raw template diffs, and a small template fragment for the targeted Java and Angular implementations.

- `templates.json` lists published template versions and the latest version for each template.
- `engagements.json` contains lightweight metadata for several individual engagements. It is not the production storage format or a collection of complete engagement files.
- `template-diff-*.json` contains custom raw diff examples. These files are not RFC 6902 JSON Patch: `add` uses `value`, `replace` uses `oldValue` and `newValue`, and `remove` uses `oldValue`.
- `template-fragment-audit-ca-v5.json` shows a small example of structured template content.

For the targeted implementation, each engagement's `templateVersion` is its baseline because previous Apply/Decline history is outside the supplied data.
