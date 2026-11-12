# Map

One line per file: what it holds. Keep it current (see CLAUDE.md). Paths are under `app/src/main/kotlin/com/seizethewind/lilycode/` unless noted.

## Entry
- `MainActivity.kt`: entry point. Holds the incoming Termux path from `lily` and shows `App`.
- `AndroidManifest.xml` (in `app/src/main/`): launcher activity, Termux Run permission, result receiver.

## ui/
- `App.kt`: switches between Home and Editor, picks project folders, handles `lily` paths.

### ui/theme/
- `Theme.kt`: colors and `LilyTheme`.
- `Syntax.kt`: syntax and find-match colors.

### ui/components/ (shared)
- `InputField.kt`: single-line input with hint, used by search, find and the new-file dialog.

### ui/home/
- `HomeScreen.kt`: project list, search, New project button.
- `ProjectRow.kt`: one project row with remove button.

### ui/editor/
- `EditorScreen.kt`: editor screen. Wires drawer, top bar, tabs, find, output and body.
- `EditorState.kt`: open files, current tab, and every file operation: open, create, save, close, run.
- `Doc.kt`: one open file and its unsaved text. Inserts at the cursor.
- `EditorBody.kt`: line numbers and the text field.
- `Highlighter.kt`: colors syntax spans and find matches in the text field.
- `FileDrawer.kt`: folder and file browser in the side drawer.
- `NewFileDialog.kt`: asks for a new file name.
- `TabStrip.kt`: row of open file tabs.
- `FindBar.kt`: find-in-file bar.
- `KeyBar.kt`: symbol key row above the keyboard.
- `OutputPanel.kt`: shows the last Termux run's output.

## data/
- `Project.kt`: a saved project (name and folder URI).
- `Projects.kt`: saves and loads the project list. Finds the project that holds a Termux path.
- `FileStore.kt`: lists, reads, writes and creates files through Android's file picker access.
- `TermuxRun.kt`: sends a command to Termux.
- `RunResultReceiver.kt`: receives Termux's result and passes it to `RunOutput`.
- `RunOutput.kt`: the last run's output and running flag.

## util/ (pure, unit tested)
- `Tokenize.kt`: syntax highlighting rules per language.
- `Find.kt`: finds all matches of a query.
- `RunSpec.kt`: picks the Termux command for a file by its extension.
- `TermuxPath.kt`: Termux provider name and the check that a path stays inside a project folder.

## Tests (in `app/src/test/kotlin/com/seizethewind/lilycode/util/`)
- `TokenizeTest.kt`, `FindTest.kt`, `TermuxTest.kt` (covers `TermuxPath` and `RunSpec`).

## Other
- `termux/lily`: Termux command that opens a file in LilyCode.
- `termux/install.sh`: installs `lily` and optionally enables Termux external apps.
- `RULES.md`: contribution rules. `CLAUDE.md`: AI-assistant notes. `README.md`: setup and status.
