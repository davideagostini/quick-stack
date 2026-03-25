# Contributing to QuickStack

Thanks for contributing.

QuickStack is intentionally narrow: fast capture, low friction, native Android behavior, and minimal scope. Keep changes aligned with that direction.

## Principles

- prefer small diffs
- avoid scope creep
- keep business logic out of composables
- keep Android integration inside repositories or dedicated feature classes
- update documentation when user-visible behavior changes

## Before opening a PR

- run `./gradlew assembleDebug`
- run `./gradlew testDebugUnitTest`
- manually verify the changed flow if it touches tile, notifications, clipboard, or scheduling

## PR title convention

Use:

- `feat: ...` for user-visible improvements
- `fix: ...` for bug fixes
- `refactor: ...` for internal cleanup without behavior change
- `docs: ...` for documentation-only changes
- `ci: ...` for workflow or release pipeline changes
- `chore: ...` for small maintenance tasks

Examples:

- `feat: add settings screen for time actions`
- `fix: restore pinned notification after swipe`
- `refactor: split settings UI into components`

## Branch name convention

Prefer:

- `feat/...`
- `fix/...`
- `refactor/...`
- `docs/...`
- `ci/...`
- `chore/...`

Examples:

- `feat/settings-screen`
- `fix/pinned-notification-swipe`
- `refactor/settings-components`

## Scope guardrails

Please avoid adding these unless explicitly discussed:

- backend or sync
- auth or accounts
- analytics or ads
- unrelated widget/platform experiments
- turning QuickStack into a generic notes app

