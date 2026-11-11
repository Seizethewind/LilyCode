# Rules

## File size
- Every source file is at most 120 lines. Markdown files are exempt.
- Over the limit? Split by responsibility, not by line count.

## Creating or updating files
- Reuse first. Search the repo for an existing composable, helper or color before writing a new one.
- Change the shared piece once, not each caller. Grep the callers before editing.
- One concern per file. One screen or one component per file.
- No code, options or abstractions nobody asked for. Delete dead code.
- Comment only the why, in one line.

## Layout: easy to locate
Package by feature, then by role. A file's path says what it is.

```
app/src/main/kotlin/com/seizethewind/lilycode/
  MainActivity.kt        entry point only
  ui/theme/              colors, typography, theme
  ui/components/         shared composables (reused everywhere)
  ui/home/               home screen and its parts
  ui/editor/             editor screen and its parts
  data/                  files, projects, storage
  util/                  small pure helpers
```

- Name files after the one thing they hold: `FileTree.kt` holds `FileTree`.
- A feature folder never imports from another feature folder. Shared code moves to `ui/components` or `util`.

## Quality
- New logic with a branch, loop or parser gets one small test.
- Validate input at boundaries (file paths, user text). Never lose data on error.
- No secrets in the repo.
- Compile before saying done. If you could not build, say so.

## Originality
- UI may be inspired by other editors. Never copy their code, icons, fonts or assets.

## Git
- Small commits, one purpose each. Message: short imperative line.
- Update README.md when the structure or setup changes.
