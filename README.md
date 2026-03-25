# QuickStack

QuickStack is an Android fast-capture app built around the Quick Settings Tile.

The goal is simple: capture something useful in a few seconds, then get out.

QuickStack currently supports:

- quick text notes
- clipboard capture
- pinned note or clipboard notifications
- quick reminder presets
- a quick 10-minute reminder flow
- a local inbox/history
- lightweight settings for language and time-action presets

## Status

This repository contains a working MVP foundation.

Implemented today:

- Quick Settings Tile entry point
- full-screen quick capture flow
- quick note save
- pinned note notifications
- clipboard save
- pinned clipboard notifications
- reminder preset: in 1 hour
- reminder preset: tonight
- quick 10-minute reminder preset
- Room-backed inbox/history
- delete, dismiss, and complete flows
- triggered reminder/timer notifications
- settings screen for language and time-action presets

Not implemented:

- custom reminder text
- snooze
- edit or reschedule flows
- reboot rescheduling
- exact alarm permission flow
- sync, backend, accounts, analytics, widgets

## Product Principles

QuickStack is intentionally narrow.

- fast capture over feature breadth
- minimal taps over rich editing
- native Android behavior over custom complexity
- local-first data over cloud features

## Screens

- `Home`: inbox/history of saved items
- `Quick Capture`: note, clipboard, reminder, and timer entry point
- `Settings`: language, time actions, app version

Suggested screenshot paths:

- `docs/screenshots/home.png`
- `docs/screenshots/quick-capture.png`
- `docs/screenshots/settings.png`
- `docs/screenshots/reminder-notification.png`

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Hilt
- Room
- AlarmManager
- Android notification APIs
- TileService

Package name:

- `com.davideagostini.quickstack`

## Project Structure

- `app/src/main/java/com/davideagostini/quickstack/core`
- `app/src/main/java/com/davideagostini/quickstack/data`
- `app/src/main/java/com/davideagostini/quickstack/domain`
- `app/src/main/java/com/davideagostini/quickstack/feature/capture`
- `app/src/main/java/com/davideagostini/quickstack/feature/home`
- `app/src/main/java/com/davideagostini/quickstack/feature/settings`
- `app/src/main/java/com/davideagostini/quickstack/feature/notifications`
- `app/src/main/java/com/davideagostini/quickstack/feature/reminders`
- `app/src/main/java/com/davideagostini/quickstack/tile`

## Architecture

QuickStack uses a small MVVM structure.

- `domain` defines the unified quick item model
- `data` owns Room, DAOs, entity mapping, and repositories
- `feature/*` owns screen-specific state and UI
- `notifications` handles pinned and triggered notifications
- `reminders` handles AlarmManager scheduling and alarm receivers
- `tile` exposes the Quick Settings Tile entry point

Runtime flow:

1. The user opens QuickStack from the launcher or the Quick Settings Tile.
2. `QuickCaptureActivity` shows the capture flow.
3. `CaptureViewModel` validates input and creates items through `QuickItemRepository`.
4. Pinned items are shown through `QuickStackNotificationManager`.
5. Reminder and timer presets are scheduled through `ReminderScheduler`.
6. `HomeViewModel` observes local storage and updates the inbox/history.

## Setup

Requirements:

- Android Studio recent stable
- Android SDK installed locally
- JDK 23 available to Gradle

If needed, create `local.properties` locally:

```properties
sdk.dir=/Users/<your-user>/Library/Android/sdk
```

`local.properties` should not be committed.

## Run

Build debug:

```bash
./gradlew assembleDebug
```

Run unit tests:

```bash
./gradlew testDebugUnitTest
```

Build release:

```bash
./gradlew assembleRelease
```

## Known Limitations

- reminders and timers are not rescheduled after device reboot
- reminder and timer presets are fixed, not free-form
- some new settings strings may still fall back to English in non-default locales
- reminder/timer dismiss vs complete are not stored as distinct final states
- delivery can be slightly delayed under doze or battery restrictions

## Roadmap

Short, practical next steps:

1. Reboot rescheduling for pending reminders and timers
2. Better reminder/timer state semantics and optional snooze
3. More polish on quick capture and settings UI
4. Complete localization coverage for settings
5. Expand a few repository and state tests

## Contributing

See [CONTRIBUTING.md](/Users/davideagostini/Documents/quick-stack/CONTRIBUTING.md) for contribution guidelines, branch naming, and PR title conventions.

## License

This repository is published under `Apache-2.0`.

## Good First Issues

- localize the settings strings in all supported languages
- add screenshots to `docs/screenshots`
- move remaining inline user messages into `strings.xml`
- add more tests for Room-backed item state transitions
- add reboot rescheduling for pending reminder/timer items
