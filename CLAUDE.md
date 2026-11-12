# CLAUDE.md

Lily Code is an Android code editor written in Kotlin with Jetpack Compose. Dark UI, inspired by mobile code editors. Original code only.

Read RULES.md and docs/MAP.md before changing anything. The short version:

- Max 120 lines per source file (markdown excluded).
- Reuse existing code. Grep before writing something new.
- Keep the folder layout in RULES.md. Put each file where its path explains it.
- docs/MAP.md lists every source file and what it holds. Look there first to find where code lives.
- Smallest change that fully solves the task. No extras nobody asked for.
- Add a small test for new non-trivial logic.
- Never copy code or assets from other apps.

## Build
`./gradlew assembleDebug` (needs Android SDK, JDK 17). Run `./gradlew testDebugUnitTest` for tests.

## Keep the map current
When you add, rename, move or delete a file, change its line in docs/MAP.md in the same commit. A new file with no map entry is unfinished.

## Before finishing
1. Build it, or say plainly that you could not.
2. Check no file passed 120 lines.
3. Update docs/MAP.md for every file you added, renamed, moved or deleted.
4. Update README.md if structure or setup changed.
5. End the reply with what you skipped or did not check.
