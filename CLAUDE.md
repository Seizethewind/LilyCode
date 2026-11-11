# CLAUDE.md

Lily Code is an Android code editor written in Kotlin with Jetpack Compose. Dark UI, inspired by mobile code editors. Original code only.

Read RULES.md before changing anything. The short version:

- Max 120 lines per source file (markdown excluded).
- Reuse existing code. Grep before writing something new.
- Keep the folder layout in RULES.md. Put each file where its path explains it.
- Smallest change that fully solves the task. No extras nobody asked for.
- Add a small test for new non-trivial logic.
- Never copy code or assets from other apps.

## Build
`./gradlew assembleDebug` (needs Android SDK, JDK 17). Run `./gradlew testDebugUnitTest` for tests.

## Before finishing
1. Build it, or say plainly that you could not.
2. Check no file passed 120 lines.
3. Update README.md if structure or setup changed.
4. End the reply with what you skipped or did not check.
