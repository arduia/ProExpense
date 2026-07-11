# Lint Retrospective — recurring ktlint / detekt / Android-lint findings

> Append-only quick-lookup log, companion to [`SKILL.md`](SKILL.md). Purpose: stop the same
> lint mistake — or the same wasted diagnosis — from happening again in a future session.
>
> **Before diagnosing a new ktlint/detekt/Android-lint failure**, scan the table below for the
> rule ID. If it's already here, apply the documented response instead of re-deriving it.
>
> **After resolving any ktlint/detekt/Android-lint failure this session** — whether the fix was
> code, a baseline regen, or a config change — add a row if the rule/situation isn't already
> covered, or leave it alone if it is (don't duplicate). One row is enough; this is a lookup
> table, not a narrative log (see `.cursor/context/retrospectives.md` for narrative post-mortems
> of larger incidents).

| Rule (tool) | Trigger | Correct response | Do NOT do |
|---|---|---|---|
| `UnusedParameter` (detekt) | A function parameter detekt flags as never referenced in the body. | Treat as a real signal, not baseline noise — read the function body. It has caught genuine bugs here: a `homeCurrencySymbol` param that should have formatted a debt row's amount but the body used a different currency source instead (a param that "looks" used because a same-named sibling function nearby does use it, but this one doesn't). Fix the logic (use the param, or delete it if it's truly redundant with another format source), then re-run detekt to confirm the finding is gone — don't baseline it. | Don't assume it's a pre-existing baseline entry de-keyed by a signature edit (that pattern applies to `LongParameterList`/`LongMethod`/`FunctionNaming`/`CyclomaticComplexMethod`, not `UnusedParameter` — an unused param is either new or was always dead code, never "pre-existing and fine"). |
| `FunctionNaming` (detekt) on `@Composable` functions | Every `@Composable` function is PascalCase by Compose convention, which `FunctionNaming`'s default `functionPattern` (`[a-z][a-zA-Z0-9]*`) always flags. ktlint's `.editorconfig` exempts `@Composable` from its own naming rule; detekt has no built-in exemption. | This is permanent, expected noise — every new/changed `@Composable` needs its own baseline entry. When you add or edit a `@Composable` function (including adding a parameter), regenerate that module's baseline (see `SKILL.md`) rather than trying to silence the rule globally. | Do **not** try `ignoreAnnotated: ['Composable']` (or similar) on the `naming` rule in `config/detekt/detekt.yml` — verified against detekt 1.23.8's bundled `default-detekt-config.yml` (this project's pinned version): `FunctionNaming` has no `ignoreAnnotated` property in this version. `config.validation: true` will hard-fail the build on an unrecognized key. This would need a detekt upgrade to revisit, not a config tweak. |
| `LongParameterList` / `LongMethod` / `CyclomaticComplexMethod` (detekt) on a function whose signature you just edited | Adding/removing/reordering a parameter, or adding a `// comment` inside a multi-line param list, changes the baseline's signature-text key and de-baselines any pre-existing finding for that function. | Read the reported complexity number. If it's roughly what it was before (function was already over threshold, your edit didn't add real branching), regenerate the baseline and diff-verify only the touched signature changed (see `SKILL.md`). If `CyclomaticComplexMethod` newly appears (function was **not** flagged before your change) because you added real branching logic (e.g. a `when` dispatch) directly inside the function body, **extract that logic into a small private helper function instead** — don't baseline genuinely new complexity. Same rule applies to `LongParameterList` if you fixed a param sprawl by bundling related params into a data class (e.g. `HomeLinkNames`, `JournalLinkLabels`): the old finding correctly disappears rather than needing a baseline update. | Don't baseline-regen reflexively on any signature change without first checking whether the *number* moved because of genuinely new logic vs. just parameter-list churn. |
| `MaxLineLength` (detekt, 120 cols) | A `val x = condition1 \|\| condition2 \|\| condition3` (or similar single-expression) line grows past 120 columns after adding a clause. | Extract the condition into a well-named local `val` on its own line(s), or wrap into a multi-line boolean expression per `SKILL.md`'s formatting rules — don't just let the line run long. | — |
| `standard:import-ordering` / `standard:multiline-expression-wrapping` (ktlint) | Hand-editing imports or multi-line lambda/if-else assignments instead of running the formatter. | Run `./gradlew :module:path:ktlintFormat` after any edit that touches imports or reformats a multi-line expression — don't hand-order or hand-wrap and hope it matches ktlint's exact rules. | — |
| `MissingTranslation` (Android lint) | A new `<string>` added to `values/strings.xml` but not to every other `values-*/strings.xml` in that module (this repo ships `values-th/`, `values-my/`). | Add the same string key to every existing locale file in that module in the same change — best-effort translation matching that locale's existing terminology/register is enough; don't leave a locale file behind. | — |

## Config-tuning attempts considered and rejected

- **`ignoreAnnotated` on naming/complexity rules for `@Composable`** — not supported by the
  pinned detekt `1.23.8` (see `FunctionNaming` row above). Re-check `gradle/libs.versions.toml`'s
  `detekt` version before retrying this; if the project upgrades detekt, re-verify against the
  new version's `default-detekt-config.yml` (extract from the `detekt-core-<version>.jar`) before
  editing `config/detekt/detekt.yml` — `validation: true` will hard-fail on any unrecognized key.
