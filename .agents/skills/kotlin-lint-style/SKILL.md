---
name: kotlin-lint-style
description: Write and edit Kotlin so it passes ktlint formatting and detekt static analysis on the first try, and know how to fix it fast when it doesn't. Use whenever writing or editing any .kt file in this repo, and always before running verifyAll or pushing.
globs: ["**/*.kt", "**/*.kts"]
alwaysApply: false
---

# Kotlin lint & formatting (ktlint + detekt)

Every module runs **ktlint** (formatting) and **detekt** (code smells) as part of
`verifyAll` — see root `build.gradle.kts`. They are different tools with different failure
modes; both block push. This skill exists because both have bitten agent-authored diffs
repeatedly, almost always for the same handful of reasons below.

**Precedence:** `AGENTS.md` > this skill.

---

## The two gates, and how they differ

| | ktlint | detekt |
|---|---|---|
| Checks | Formatting/style only | Code smells (complexity, length, naming, unused code, …) |
| Config | `.editorconfig` + `android.set(true)` (Android Kotlin style guide) | `config/detekt/detekt.yml`, `buildUponDefaultConfig = true` |
| Safety net | **None** — every violation is a hard failure | Per-module `detekt-baseline-*.xml` — only *new* findings fail |
| Fix | Auto-fixable: `./gradlew :module:ktlintFormat` | Either fix the code, or regenerate the baseline (see below) |

Because ktlint has no baseline, **run `ktlintFormat` on any module you touched before
considering the change done** — don't hand-format and hope. Because detekt *does* have a
baseline, a "failure" there is very often not a new problem — see the next section before
trying to fix the code itself.

---

## detekt baselines are keyed by exact signature text — read this before "fixing" a detekt failure

`config/detekt/detekt.yml` sets strict thresholds project-wide:

- `LongParameterList.functionThreshold = 6`
- `LongMethod.threshold = 60`
- `CyclomaticComplexMethod.threshold = 15`
- `ReturnCount.max = 2` (`excludeLabeled: false`)
- `LoopWithTooManyJumpStatements.maxJumpCount = 1`
- `MaxLineLength.maxLineLength = 120` (this is the detekt line-length gate, not ktlint's)
- `FunctionNaming` — flags `@Composable` functions for being PascalCase (ktlint's
  `.editorconfig` exempts `@Composable` from its own naming rule; detekt has no such
  exemption, so every `@Composable` screen/preview function relies on its baseline entry)

Existing violations are whitelisted in `<module>/detekt-baseline-*.xml`, keyed by the
**full function signature text** (params, defaults, comments and all). **Any edit to a
function's parameter list — adding, removing, reordering, or even reformatting a param —
changes that key and de-baselines the finding**, even though nothing about the function
got worse. This is the single most common source of "detekt failure" on an agent diff that
only added one parameter.

**When you see a detekt failure after changing a signature:**

1. Read the failures. If they're genuinely new problems introduced by your change (a new
   `MaxLineLength`, a real complexity increase, an actually-unused new function), fix the
   code.
2. If they're pre-existing findings on a function whose signature you just changed
   (matches the pattern above — same rule, same file/function, was almost certainly already
   suppressed before your edit), regenerate that module's baseline:
   ```bash
   ./gradlew :module:path:detektBaselineAndroidDebug :module:path:detektBaselineAndroidRelease
   # KMP modules also need:
   ./gradlew :module:path:detektBaselineMetadataCommonMain :module:path:detektBaselineMetadataMain
   ```
3. **Always review the diff before committing a regenerated baseline** —
   `git diff --stat <module>/detekt-baseline-*.xml` should show only entries for the
   signatures you actually touched (a handful of changed `<ID>` lines), never a
   wholesale rewrite. If unrelated entries changed too, something else shifted (e.g. a
   nearby edit moved line numbers into an unrelated pre-existing finding) — worth a second
   look, not an automatic accept.
4. SqlDelight-generated query functions (`core/storage/build/generated/.../*Queries.kt`)
   are also subject to detekt via the KMP `detektMetadataCommonMain`/`detektMetadataMain`
   tasks. Adding a column to a `.sq` table grows the generated `insert*`/`select*`
   function's parameter list the same way — regenerate `core:storage`'s
   `MetadataCommonMain`/`MetadataMain` baselines too when that happens.

Don't run baseline-regeneration as a way to silence a genuinely new problem — only use it
to re-whitelist findings that were already accepted under the old signature.

---

## Concrete formatting rules that come up constantly

- **Import order:** alphabetical, one import per line, no wildcard imports. When adding an
  import mid-block, put it in alphabetical position — don't just append it, ktlint will
  flag the ordering.
- **Trailing commas:** multi-line parameter lists and call-site argument lists in this
  codebase consistently end with a trailing comma before the closing `)`. Match that when
  you convert a single-line call to multi-line.
- **Single-expression function bodies that don't fit on one line:** don't just wrap after
  `=` if the body itself is still too long — break the call's own arguments out one per
  line:
  ```kotlin
  // Wrong — still over 120 cols, and ktlint's "function signature body expression" rule
  // rejects breaking only after `=` when the body doesn't fit as a single wrapped line:
  private fun Category.toRowUi(): CategoryRowUi =
      CategoryRowUi(categoryId = id.value, label = name, iconId = iconId, type = type)

  // Right:
  private fun Category.toRowUi(): CategoryRowUi =
      CategoryRowUi(
          categoryId = id.value,
          label = name,
          iconId = iconId,
          type = type,
      )
  ```
- **Long single-line arguments inside a composable call** (e.g. a `listOf(stringResource(...),
  stringResource(...))` passed to `options =`) are a common way to blow past 120 columns —
  wrap the whole argument, not just the call:
  ```kotlin
  SegmentedToggle(
      options =
          listOf(
              stringResource(R.string.type_expense),
              stringResource(R.string.type_income),
          ),
      ...
  )
  ```
- `@Composable` functions are PascalCase by convention (ktlint-exempt via `.editorconfig`,
  detekt-exempt only via baseline — see above).

---

## Adjacent lint gate: new string resources need every locale

Not ktlint/detekt, but the same "new failure only on this change" shape: Android's
`lintDebug` (`MissingTranslation`) fails if a new `<string>` is added to
`values/strings.xml` but not to every other `values-*/strings.xml` in that module (this
repo currently ships `values-th/` and `values-my/` alongside default `values/`). Adding a
user-facing string means adding its translation to *every* existing locale file in that
module, not just the default one.

---

## Fast iteration loop

Full `verifyAll` is slow. While iterating on one module:

```bash
./gradlew :module:path:ktlintFormat                         # auto-fix formatting
./gradlew :module:path:ktlintCheck :module:path:detektAndroidDebug   # fast recheck
```

Run the full `./gradlew verifyAll` gate once before push, per `AGENTS.md` Step 6 — don't
substitute the fast loop for it.
