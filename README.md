# Lily Code

A dark, mobile-first code editor for Android, written in Kotlin and Jetpack Compose.

## Status
Early shell. Home and editor screens exist; most buttons do nothing yet.

- Home: project list (empty) and a New project button.
- Editor: file drawer, line numbers, text area, symbol key bar.

Not done yet: saving files, projects, tabs, syntax highlighting, run.

## Build
Needs JDK 17 and the Android SDK. Open in Android Studio, or:

```
./gradlew assembleDebug
```

The Gradle wrapper is not committed yet. Android Studio creates it on first sync.

## Layout
See RULES.md for the folder structure and contribution rules, and CLAUDE.md for AI-assistant notes.

## License
See LICENSE.
