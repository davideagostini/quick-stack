# QuickStack

QuickStack is an Android fast-capture utility built around the Quick Settings Tile.
It lets you save a note, capture the current clipboard text, pin content as a persistent notification, schedule a minimal reminder or timer flow, and tweak a small set of app preferences without leaving the fast-capture workflow.

## Current implementation

What is implemented today:

- Quick Settings Tile entry point
- Full-screen quick capture activity
- Quick text note
- Pin note as persistent notification
- Save latest clipboard text
- Pin latest clipboard text as persistent notification
- Quick reminder in 1 hour
- Quick reminder tonight
- Quick timer for 10 minutes
- Local inbox/history backed by Room
- Delete items
- Dismiss/complete pinned items
- Triggered reminder/timer notifications with dismiss/complete actions
- Settings screen with language selection
- Settings screen for configurable time-action presets
- App version shown in settings

What is not implemented:

- custom reminder text
- snooze
- edit/reschedule flows
- reboot rescheduling
- exact-alarm permission flow
- widgets, sync, auth, analytics, cloud features
- full localization coverage for the new settings strings

## Screenshots

Add screenshots before the first public release. Suggested paths:

- `docs/screenshots/home.png`
- `docs/screenshots/quick-capture.png`
- `docs/screenshots/pinned-notification.png`
- `docs/screenshots/reminder-notification.png`

## Setup

Requirements:

- Android Studio recent stable
- Android SDK installed locally
- JDK 23 available to Gradle in this workspace

Project notes:

- package name: `com.davideagostini.quickstack`
- minimum SDK: 26
- compile SDK: 36

If needed, set the Android SDK path in `local.properties`:

```properties
sdk.dir=/Users/<your-user>/Library/Android/sdk
```

## Run

Build debug APK:

```bash
./gradlew assembleDebug
```

Run unit tests:

```bash
./gradlew testDebugUnitTest
```

Build release APK:

```bash
./gradlew assembleRelease
```

Install from Android Studio or with your usual `adb` flow after building.

The repository should not include `local.properties`; create it locally if needed.

## CI and Release Signing

The repository includes a GitHub Actions workflow at `.github/workflows/android-build.yml`.

What it does:

- runs `testDebugUnitTest`
- runs `:app:compileDebugKotlin`
- builds a release APK artifact
- signs the release APK automatically if signing secrets are configured
- publishes a rolling prerelease for pushes to `main`
- publishes a versioned GitHub Release when you push a tag like `v0.1.0`

Release signing is optional. If the signing secrets are missing, the workflow still builds a release APK, but it will not be signed with your private key.

### Required GitHub Secrets for signing

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

### How to add your keystore to GitHub Secrets

1. Convert your keystore file to base64 locally.

macOS/Linux:

```bash
base64 -i /path/to/your-release-key.jks | pbcopy
```

If `pbcopy` is not available:

```bash
base64 -i /path/to/your-release-key.jks > keystore.base64.txt
```

2. Open your GitHub repository.
3. Go to `Settings` → `Secrets and variables` → `Actions`.
4. Add these repository secrets:
   - `ANDROID_KEYSTORE_BASE64`: the base64 content of the keystore
   - `ANDROID_KEYSTORE_PASSWORD`: the keystore password
   - `ANDROID_KEY_ALIAS`: the alias name inside the keystore
   - `ANDROID_KEY_PASSWORD`: the key password for that alias
5. Run the `Android Build` workflow from the `Actions` tab, or let it run on push to `main`.

### Local release signing with the same variables

You can also build a signed release locally by exporting the same variables before running Gradle:

```bash
export ANDROID_KEYSTORE_PATH=/absolute/path/to/your-release-key.jks
export ANDROID_KEYSTORE_PASSWORD=your_keystore_password
export ANDROID_KEY_ALIAS=your_key_alias
export ANDROID_KEY_PASSWORD=your_key_password
./gradlew assembleRelease
```

### Versioned GitHub releases

To create a real GitHub Release for a version:

1. Update `appVersionName` in `app/build.gradle.kts`
2. Commit the change
3. Create and push a matching tag, for example:

```bash
git tag v0.1.0
git push origin v0.1.0
```

This triggers the workflow to:

- build the release APK
- sign it if secrets are configured
- create a GitHub Release named `QuickStack v0.1.0`
- attach `quickstack-0.1.0.apk`

For the best manual test pass, verify:

- app launch and empty state
- tile launch
- note save and pin
- clipboard save and pin
- reminder in 1 hour
- reminder tonight
- timer for 10 minutes
- dismiss/complete actions from notifications and inbox

## Architecture overview

QuickStack follows a small MVVM + Room + Hilt structure.

Main areas:

- `domain/`
  Unified item model: `QuickItem`, `QuickItemDraft`, `QuickItemType`, `QuickItemSource`

- `data/`
  Room database, DAO, entity mapping, repositories

- `feature/capture/`
  Full-screen capture UI and `CaptureViewModel`

- `feature/home/`
  Inbox/history UI and `HomeViewModel`

- `feature/settings/`
  Preferences UI, small settings repository, and time-action/language configuration

- `feature/notifications/`
  Notification channel management and action receiver

- `feature/reminders/`
  `AlarmManager` scheduling and alarm receiver for reminders/timers

- `tile/`
  `TileService` entry point

Runtime flow:

1. User opens QuickStack from launcher or Quick Settings Tile.
2. `QuickCaptureActivity` shows the full-screen quick capture flow.
3. `CaptureViewModel` validates input and writes through `QuickItemRepository`.
4. Notes/clipboard can publish pinned notifications through `QuickStackNotificationManager`.
5. Reminders/timers are scheduled through `ReminderScheduler` and triggered by `ReminderAlarmReceiver`.
6. Settings are stored locally and fed back into capture/scheduling defaults.
7. `HomeViewModel` observes Room and keeps the inbox/history updated.

## Key implementation choices

- `AlarmManager` is used for reminders/timers because the app only needs simple time-based local actions.
- `Room` stores all quick items in one table with a unified model.
- Hilt keeps Android entry points and repositories wired without extra framework code.
- The tile launches a dedicated full-screen activity instead of trying to do inline capture in the tile itself.
- Settings use a lightweight local preferences repository instead of adding heavier configuration infrastructure.

## Feature list aligned to the codebase

Capture:

- note input field
- clipboard capture
- one-tap presets for reminder and timer actions
- settings-driven presets for language and time actions

Persistence:

- local-only storage
- item status for pinned, scheduled, triggered, completed

Notifications:

- persistent notifications for pinned notes and clipboard items
- high-priority notifications for triggered reminders/timers
- dismiss/complete actions handled by broadcast receiver

Inbox/history:

- newest-first list
- metadata for source and state
- delete
- dismiss/complete where applicable

## Known limitations

- reminders/timers are not rescheduled after device reboot
- reminder/timer actions use fixed presets only
- settings strings are only fully maintained in the default `values/strings.xml`; new settings copy may fall back to English in other locales
- reminder/timer notifications do not support snooze
- triggered reminder/timer items are stored as completed when dismissed or completed; those states are not distinguished yet
- reminder/timer delivery may be slightly delayed under doze or battery constraints because the app does not request exact alarm privileges
- screenshots are not in the repository yet

## Roadmap

Practical next steps for the current product direction:

1. Reboot rescheduling for pending reminders/timers
2. Better reminder/timer state semantics and optional snooze
3. More polished quick capture layout and feedback
4. Localize the new settings copy across all `values-*` folders
5. Small DAO/repository test expansion
6. Screenshot/docs polish

## Contributing

Keep contributions focused and aligned with the existing MVP.

Guidelines:

- do not add backend, auth, analytics, sync, or unrelated product scope
- prefer small diffs
- keep business logic out of composables
- keep storage/system interactions inside repositories or dedicated Android integration classes
- avoid premature abstraction
- update documentation when user-visible behavior changes

Before opening a change:

- run `./gradlew assembleDebug`
- run `./gradlew testDebugUnitTest`
- manually verify the changed flow if it touches tile, notifications, clipboard, or scheduling

## License

This repository is prepared with an `Apache-2.0` license, which is a practical default for an Android app intended for public collaboration.

## Repository Files

- `LICENSE`: public repository license
- `.gitignore`: Android/Gradle/IDE ignores
- `.github/ISSUE_TEMPLATE/`: starter bug and feature templates
- `.github/pull_request_template.md`: lightweight PR checklist
- `RELEASE_CHECKLIST.md`: first public release checklist

## Good first issues

- move the remaining inline user messages into `strings.xml`
- add screenshot assets and wire them into this README
- add tests for Room-backed item state transitions
- improve inbox formatting for scheduled timestamps
- add a small reboot-reschedule receiver for pending reminder/timer items
- localize the settings strings in all supported languages
