# First Release Checklist

## Repository

- [ ] Confirm `.gitignore` excludes `local.properties`, build outputs, IDE files, and signing files
- [ ] Confirm no local-only paths or machine-specific files are committed
- [ ] Confirm `README.md` matches the shipped feature set
- [ ] Confirm `LICENSE` is the intended public license
- [ ] Confirm issue and PR templates are present

## App identity

- [ ] Confirm app name is `QuickStack` everywhere
- [ ] Confirm package name is `com.davideagostini.quickstack`
- [ ] Confirm `versionCode` and `versionName` are ready for the first public build
- [ ] Confirm launcher icon, tile icon, and notification copy are final enough for release

## Functional verification

- [ ] Launcher flow opens normally
- [ ] Quick Settings Tile opens capture reliably
- [ ] Save note works
- [ ] Pin note notification works and survives swipe as intended
- [ ] Save clipboard works when clipboard has text
- [ ] Clipboard failure state is clear when clipboard is empty
- [ ] Reminder preset scheduling works
- [ ] Timer preset scheduling works
- [ ] Triggered notifications show dismiss and complete actions
- [ ] Inbox/history reflects create, complete, dismiss, and delete actions
- [ ] Settings persist language and time action presets

## Quality

- [ ] `./gradlew assembleDebug`
- [ ] `./gradlew testDebugUnitTest`
- [ ] No obvious placeholder copy remains
- [ ] No debug-only comments or TODOs remain in source
- [ ] README screenshots are either added or the screenshots section is intentionally left minimal

## Store / public repo follow-up

- [ ] Add screenshots to `docs/screenshots/`
- [ ] Prepare release notes
- [ ] Prepare privacy wording if publishing on a store
- [ ] Decide whether reboot rescheduling should land before first public release

