# Fixtures

This folder has the sample JSON used by the project.

- `templates.json` has the templates and their published versions.
- `engagements.json` has a small record for each engagement and the template version it uses.
- `template-diff-*.json` shows what changed between two template versions.
- `template-fragment-audit-ca-v5.json` is a small example of actual template content.

The engagement records are only lightweight metadata. They are not complete engagement files and they are not meant to be a real database format.

The diff format is custom and simple:

- `add` has a new `value`
- `replace` has an `oldValue` and a `newValue`
- `remove` has an `oldValue`

An engagement's `templateVersion` is the version we start from. We compare it with the latest version in `templates.json`.

Some examples skip across multiple versions. That is useful when an engagement is a few updates behind and we only want to show the final difference between its version and the newest one.
