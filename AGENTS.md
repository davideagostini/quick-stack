# AGENTS.md

## Project

Build an Android app called **QuickStack**.

QuickStack is a fast-capture utility centered around the Android Quick Settings Tile.
The app lets users capture small pieces of information or trigger lightweight actions in a few seconds.

Primary use cases:

- write a quick text note
- pin a note as a persistent notification
- save the latest clipboard content
- pin clipboard content as a persistent notification
- create a quick reminder ("in 1 hour", "tonight")
- start a quick timer (10 minutes)

The product must feel:

- fast
- minimal
- low-friction
- reliable
- native to Android

---

## Product principles

When making decisions, optimize for:

1. speed of capture
2. minimal taps
3. clarity over feature breadth
4. stable Android-native implementation
5. easy future extensibility

Do not turn this into a generic notes app.
Do not add accounts, sync, backend, analytics, ads, or unnecessary complexity.

---

## MVP scope

Implement only this MVP unless explicitly requested otherwise:

### Core actions

- Quick Tile opens a lightweight action sheet / launcher UI
- Quick text note
- Pin text note as persistent notification
- Save latest clipboard content
- Pin latest clipboard content as persistent notification
- Quick reminder in 1 hour
- Quick reminder tonight
- Quick timer for 10 minutes

### Basic management

- Persist saved items locally
- Show a simple inbox/history screen
- Allow deleting items
- Allow marking pinned notifications as dismissed/completed

### Notification behavior

- Pinned items use ongoing/persistent notifications where appropriate
- Reminder/timer actions use standard actionable notifications
- Notifications should expose useful actions like dismiss, complete, or snooze when simple to support

---

## Non-goals for MVP

Do not implement these in the initial version:

- voice notes
- screenshot capture/import
- OCR
- cloud sync
- widgets
- natural language date parsing beyond small fixed presets
- multiple tiles
- complex tagging
- authentication
- online services

---

## Tech stack

Use:

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Room
- DataStore for lightweight preferences if needed
- WorkManager only if actually justified
- AlarmManager when more appropriate for time-based reminders
- Notification APIs
- TileService for Quick Settings Tile

Prefer current Android best practices and keep the architecture simple.

---

## Architecture guidelines

Organize code by feature and responsibility.
Suggested package structure:

- app/src/main/java/.../core
- app/src/main/java/.../data
- app/src/main/java/.../domain
- app/src/main/java/.../feature/home
- app/src/main/java/.../feature/capture
- app/src/main/java/.../feature/notifications
- app/src/main/java/.../feature/reminders
- app/src/main/java/.../tile

Minimum architectural rules:

- UI should be Compose-first
- business logic should not live directly in composables
- repositories abstract storage/system interactions
- state should be modeled explicitly
- avoid premature abstraction

---

## Data model

Design around a unified quick-action item model.

Suggested item categories:

- TEXT_NOTE
- CLIPBOARD
- PINNED_NOTE
- REMINDER
- TIMER

Each stored item should support most of:

- id
- type
- title or content
- createdAt
- scheduledAt (nullable)
- isPinned
- isCompleted
- source
- metadata as needed

Keep the schema simple and migration-friendly.

---

## UX guidelines

The app should feel extremely fast.

Rules:

- prefer one-tap or low-input actions
- avoid long forms
- avoid cluttered screens
- keep copy concise
- prioritize a clean action sheet and a usable inbox/history screen

The Quick Tile flow should be:

1. tap tile
2. choose action
3. complete action with zero or minimal input
4. return control quickly

If Android platform constraints prevent a true inline dialog, use the lightest acceptable Activity/Compose surface.

---

## Quality bar

Before considering work complete:

- project builds successfully
- no dead code
- no placeholder TODOs unless explicitly noted
- basic edge cases handled
- manifest entries are correct
- permissions are minimal
- notifications work consistently
- tile behavior is stable
- code is readable and reasonably documented

---

## Testing

Add tests where they provide value, especially for:

- reminder/timer scheduling logic
- item mapping/state logic
- repository behavior when practical

Do not over-invest in test infrastructure early.
Favor a few meaningful tests over many shallow tests.

---

## Delivery style

When implementing, Codex should:

1. inspect the repository structure first
2. propose a short plan
3. implement in small coherent steps
4. keep diffs focused
5. explain important tradeoffs briefly
6. avoid speculative large rewrites

If something is ambiguous, choose the simplest path consistent with the MVP.

---

## First milestone

The first meaningful milestone should include:

- app skeleton
- navigation baseline
- Room setup
- unified item model
- inbox/history screen
- Quick Tile entry point
- action sheet UI
- quick text note flow
- clipboard save flow
- persistent notification flow

Only after that, add reminders and timer support.

## Available Skills

The project includes reusable skills in `skills/`.

- For Android project scaffolding, follow `skills/android-compose-starter/SKILL.md`

## Reference

I have already a mobile app that use Quick Tile android, check it for reference.
`/Users/davideagostini/Documents/networth-app/mobile-app`

Use it also for create a trasparent dialog, for the themes an for others.
`/Users/davideagostini/Documents/networth-app/mobile-app/app/src/main/java/com/davideagostini/summ/ui/entry`

For the package name of the app use `com.davideagostini.quickstack`
