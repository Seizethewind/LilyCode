# Lily Code

A dark, mobile-first code editor for Android, written in Kotlin and Jetpack Compose, built to work with Termux.

## Status
Early version, not yet compiled or tested on a device (see Build).

- Home: project list (kept between launches), search, New project (pick a folder).
- Editor: file drawer with folders and New file, tabs, save, line numbers, find in file, syntax highlighting, symbol key bar that types at the cursor.
- Termux: open and save files that live in Termux, run them from the Run button with the output shown in the editor, and open files from Termux with `lily <file>`.

Highlighting covers py, sh, js, ts, kt, java, c, h, cpp, go, rs and json. Run covers py, sh, js, rb, lua, pl, php and go.

Not done yet: replace, search across files, rename or delete files, interactive programs (Run is non-interactive, so `input()` gets no input).

## Termux setup
1. Install Termux (0.109 or newer).
2. In Termux: `sh termux/install.sh` (copy the `termux` folder there first). It installs `lily` and, if you agree, sets `allow-external-apps=true` in `~/.termux/termux.properties`.
3. In LilyCode: New project, pick your Termux home folder (the picker opens at Termux's files provider), then open files from the drawer.
4. The first Run asks for the Termux permission ("Run commands in Termux environment"). If Android does not ask, grant it in App info, Permissions, Additional permissions.
5. From Termux: `lily notes.py` opens the file in LilyCode. It only works for files inside a folder you added as a project.

## Build
Needs JDK 17 and the Android SDK. Open in Android Studio, or:

```
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

The Gradle wrapper is not committed yet. Android Studio creates it on first sync.

## Layout
See RULES.md for the folder structure and contribution rules, and CLAUDE.md for AI-assistant notes.

```
app/src/main/kotlin/com/seizethewind/lilycode/
  ui/        App, home/, editor/, components/, theme/
  data/      FileStore (files), Projects (saved list), TermuxRun and RunOutput (running)
  util/      Tokenize, Find, RunSpec, TermuxPath (pure, unit tested)
termux/      lily command and installer for the Termux side
```

## License
See LICENSE.
