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
- `InputField.kt`: single-line input with hint, used by search, find and the name dialog.
- `ConfirmDialog.kt`: yes/cancel dialog, used before deleting.

### ui/home/
- `HomeScreen.kt`: project list, search, New project button.
- `ProjectRow.kt`: one project row with remove button.

### ui/editor/
- `EditorScreen.kt`: editor screen. Wires drawer, top bar, tabs, find, output and body, and triggers drafts and disk checks.
- `EditorState.kt`: open files, current tab, and opening, saving (with the changed-on-disk check) and closing them.
- `Session.kt`: reopens last session's tabs and recovers unsaved text from app storage.
- `Drafts.kt`: copies unsaved tabs to app storage while typing and when the app leaves the screen.
- `Sync.kt`: reloads a tab from disk, overwrites the disk copy on request, and refreshes clean tabs changed elsewhere.
- `ConflictDialog.kt`: asks Overwrite or Reload when a save finds the file changed on disk.
- `Runner.kt`: saves the current file and runs it in Termux.
- `Doc.kt`: one open file, its unsaved text, read-only flag and disk stamp. Inserts at the cursor.
- `EditorBody.kt`: line numbers and the text field.
- `Highlighter.kt`: colors syntax spans and find matches in the text field.
- `FileDrawer.kt`: folder and file browser in the side drawer, with new, copy, move and paste.
- `FileRow.kt`: one drawer row with its rename, copy, move and delete menu.
- `FileManager.kt`: the drawer's file actions. Keeps open tabs pointing at the right file. Holds `Clip`.
- `NameDialog.kt`: asks for a file or folder name (new and rename).
- `TabStrip.kt`: row of open file tabs.
- `FindBar.kt`: find-in-file bar.
- `KeyBar.kt`: symbol key row above the keyboard.
- `OutputPanel.kt`: shows the last Termux run's output.

## data/
- `Project.kt`: a saved project (name and folder URI).
- `Projects.kt`: saves and loads the project list and each project's open tabs. Finds the project that holds a Termux path.
- `FileStore.kt`: lists, reads, writes and creates files through Android's file picker access. Gives each file a disk stamp. Opens big or non-UTF-8 files read-only.
- `FileOps.kt`: create, rename, delete, move and copy. Uses shell commands in Termux folders, because Termux's provider cannot do these.
- `Recovery.kt`: private copies of unsaved or half-saved text (with the disk stamp they were based on), kept until the file is written. Holds `Draft`.
- `TermuxRun.kt`: sends a command to Termux and waits for its result. Holds `ExecResult`.
- `RunResultReceiver.kt`: receives Termux's result and routes it to the waiting command or to `RunOutput`.
- `RunOutput.kt`: the last run's output and running flag.

## util/ (pure, unit tested)
- `Tokenize.kt`: syntax highlighting rules per language.
- `Find.kt`: finds all matches of a query.
- `RunSpec.kt`: picks the Termux command for a file by its extension. Holds the Termux bin folder.
- `TermuxPath.kt`: Termux provider name and the check that a path stays inside a project folder.
- `DocPath.kt`: checks whether a document id is under another, and rewrites it after a rename or move.
- `DraftFormat.kt`: the layout of a draft file (uri, stamp, text) and how to read it back.

## Tests (in `app/src/test/kotlin/com/seizethewind/lilycode/util/`)
- `TokenizeTest.kt`, `FindTest.kt`, `TermuxTest.kt` (covers `TermuxPath` and `RunSpec`), `DocPathTest.kt`, `DraftFormatTest.kt`.

## Other
- `termux/lily`: Termux command that opens a file in LilyCode.
- `termux/install.sh`: installs `lily` and optionally enables Termux external apps.
- `RULES.md`: contribution rules. `CLAUDE.md`: AI-assistant notes. `README.md`: setup and status.
