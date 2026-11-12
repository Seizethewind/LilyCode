# Lily Code

A dark, mobile-first code editor for Android, written in Kotlin and Jetpack Compose, built to work with Termux.

## Status
Early version, not yet compiled or tested on a device (see Build).

- Home: project list (kept between launches), search, New project (pick a folder).
- Editor: file drawer with folders, tabs (restored next time you open the project), save, line numbers, find in file, syntax highlighting, symbol key bar that types at the cursor.
- Files: new file, new folder, rename, copy, move and delete from the drawer. Names that already exist are refused, so nothing is overwritten.
- Safety: every save keeps a private copy until the write succeeds, and a crashed or failed save is restored as an unsaved tab on the next open. Big files (over 1 MB) and files that are not UTF-8 open read-only. Binary files are refused.
- Termux: open and save files that live in Termux, run them from the Run button with the output shown in the editor, and open files from Termux with `lily <file>`.

Highlighting covers py, sh, js, ts, kt, java, c, h, cpp, go, rs and json. Run covers py, sh, js, rb, lua, pl, php and go.

In a Termux folder, create, rename, copy, move and delete run as shell commands in Termux (Termux's file provider cannot do them), so they need the Termux setup below. In other folders they use Android's own file operations, if that provider supports them.

Not done yet: replace, search across files, interactive programs (Run is non-interactive, so `input()` gets no input).

## Termux setup
1. Install Termux (0.109 or newer).
2. In Termux: `sh termux/install.sh` (copy the `termux` folder there first). It installs `lily` and, if you agree, sets `allow-external-apps=true` in `~/.termux/termux.properties`.
3. In LilyCode: New project, pick your Termux home folder (the picker opens at Termux's files provider), then open files from the drawer.
4. The first Run asks for the Termux permission ("Run commands in Termux environment"). If Android does not ask, grant it in App info, Permissions, Additional permissions. File actions in a Termux folder need this too.
5. From Termux: `lily notes.py` opens the file in LilyCode. It only works for files inside a folder you added as a project.

## Build
Needs JDK 17 and the Android SDK. Open in Android Studio, or:

```
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

The Gradle wrapper is not committed yet. Android Studio creates it on first sync.

## Layout
See RULES.md for the folder structure and contribution rules, CLAUDE.md for AI-assistant notes, and docs/MAP.md for what every file holds.

```
app/src/main/kotlin/com/seizethewind/lilycode/
  ui/        App, home/, editor/, components/, theme/
  data/      FileStore and FileOps (files), Projects (saved list), Recovery, TermuxRun and RunOutput (running)
  util/      Tokenize, Find, RunSpec, TermuxPath, DocPath (pure, unit tested)
termux/      lily command and installer for the Termux side
```

## License
See LICENSE.
