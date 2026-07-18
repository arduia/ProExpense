# Retrospectives — Post-Mortem Guard Log

> Append-only. Written automatically when Step 8 triggers (large change + unexpected gate failure).
> Promote durable guards into `AGENTS.md` so lessons are enforced, not just logged.
>
> **Every entry's guard needs a `Verified:` line** — the command actually run after applying the
> fix, its result, and the date. A guard written as a prescription ("tag them and exclude...")
> without evidence it was carried out is not closed; it's a TODO wearing a "Guards:" label, and it
> can silently rot exactly like the 2026-06-20 entry below did (see the 2026-07-18 follow-up).
> When re-verifying an older entry, append a dated `Verified:` line rather than editing the
> original text — this file is append-only.

---

<!-- Entries below this line -->

## 2026-06-20 — Float money conversion + cache-masked test failure

**What slipped:** Three ViewModels (shared cost, event create, debt add) converted user
amounts to cents with `(text.toDouble() * 100).toLong()`, which mis-rounds values like
`4.35 → 434` and `0.29 → 28`. Existing tests only used whole-number inputs, so the bug was
invisible. Separately, `AmountInputLogicTest.applyKey` asserted a wrong expectation that
contradicted the 2-decimal rule; `./gradlew test` reported success anyway because the
`:feature:logging` test task was served from the build cache (UP-TO-DATE) and never re-run.

**Root cause:** (1) No shared, float-safe amount parser — each flow re-implemented conversion.
(2) Trusting a green `./gradlew test` whose results were cached, masking a real failure.

**Guards:**
- Parse money via `com.arduia.expense.domain.parseAmountToCents` (string-based, no floating
  point). Never use `(double * 100).toLong()` for currency.
- When verifying after a fix, force execution (`--rerun-tasks`) or use `./gradlew verifyAll`;
  do not trust a cached `./gradlew test` as proof a previously-failing test now passes.

## 2026-06-20 — PIN lockout bypass + release-variant screenshot tests

**What slipped (both masked by the build cache above):**
1. `PinEntryViewModel.onBiometricUnlock` only checked the UI `mode == LOCKOUT`, not the
   lockout repository, so a biometric unlock could bypass an active lockout — a brute-force
   protection hole. Lockout-trigger tests also used `advanceUntilIdle()`, which fast-forwards
   the 30s countdown ticker and resets the lockout before the assertion.
2. Roborazzi/Compose UI tests failed under `testDevReleaseUnitTest` ("Unable to resolve
   activity for ComponentActivity") because `compose-ui-test-manifest` is `debugImplementation`
   only and isn't merged into the release variant.

**Guards:**
- Auth guards must consult the authoritative repository state (lockout), never just UI state.
- For lockout/ticker assertions use `runCurrent()`, not `advanceUntilIdle()`.
- Robolectric Compose UI tests are debug-only; tag them `@Category(ComposeUiTests/ScreenshotTests)`
  and exclude those categories from the release unit-test variant.

**Verified: 2026-07-18 — was incomplete, now fixed.** Only the `ScreenshotTests` half of this guard
had actually landed — `app/build.gradle.kts` excluded it, but the `ComposeUiTests` marker named
above was never created, and no plain Compose-interaction test (as opposed to a Roborazzi
screenshot test) carried any category tag. 28 test classes across `app` (SharedCost, Journal,
Category, Debt, Reports, ...) were silently failing under `:app:testDevReleaseUnitTest` with the
exact "Unable to resolve activity for ComponentActivity" error this entry describes — invisible to
every documented gate because `verifyAll` never ran that variant either (only
`:app:testDevDebugUnitTest`). It surfaced by accident when an unrelated task ran the bare
`./gradlew test`. Root cause of *why it rotted*: this entry recorded the guard as a prescription
with no completion evidence, and no gate ever exercised the release variant to force that evidence
— see the 2026-07-18 entry below for the process fix (not just the code fix). Fixed by: creating
`ComposeUiTests` (`app/src/test/java/.../testing/ComposeUiTests.kt`), tagging all 28 classes,
adding it to `excludeCategories` in `app/build.gradle.kts`, and confirming
`:app:testDevReleaseUnitTest` — `BUILD SUCCESSFUL`, 25 tests run (pure-logic classes only), the 28
UI classes correctly absent from the results.

## 2026-07 — App-module string duplicates silently shadowed feature-module string updates

**What slipped:** `app/src/main/res/values/strings.xml` still carried a near-complete legacy copy
of almost every `feature:*` module's `strings.xml` (leftover from before the KMP module split —
~280 duplicate `<string>`/`<plurals>` names across Reports, Journal, Debt, Events, PIN, Categories,
Shared Costs, and more). Android's library-vs-app resource merge order makes the *app* module's
copy win for any resource name defined in both places. A Reports string update earlier in this
session (generalizing "this month" → "this period" copy for the new weekly-granularity feature,
plus fixing the chevron labels) passed `verifyAll` and Roborazzi screenshot verification cleanly —
but never actually reached the built app, because the stale app-module duplicate silently won the
merge. The defect was only caught by accident: a new Compose UI test asserted on the chevron's
*content description* (an accessibility label, invisible in a screenshot pixel diff), which is
exactly the kind of change Roborazzi cannot catch.

**Root cause:** No automated check flags a resource name defined in both an app module and a
library module it depends on — AGP allows the override intentionally, so it fails silently rather
than erroring. Screenshot tests only catch *visible* text changes, not content descriptions,
`stringResource` calls that resolve to unreachable app-module copies with identical rendered
pixels by coincidence, or any string not currently exercised by a baseline.

**Guards:**
- When adding or changing a string in a `feature:*` module's `strings.xml`, grep
  `app/src/main/res/values/strings.xml` for the same resource name first — if present, the feature
  module's copy may be silently shadowed. Removed all ~280 stale app-module duplicates in this pass
  (`comm -12` diff between `app` and all `feature/*/src/androidMain/res/values/strings.xml` names);
  only one (`shared_new_split`) had a different value ("+ New split" → "New split") and needed a
  screenshot re-record.
- A Roborazzi-clean diff is not proof a string change reached the app — accessibility-only text
  (content descriptions, TalkBack labels) needs a Compose UI test asserting on the actual node,
  not just a screenshot.

## 2026-07-02 — Oversized date-range chip survived a dedicated product audit

**What slipped:** The Journal date-range chip rendered at 59dp next to 28dp FilterChips in the
same row — more than double their height — because its clear (X) icon used `proIconClickable`,
whose `minimumInteractiveComponentSize()` puts a 48dp floor on the icon's layout box and
inflates the whole chip. The defect shipped with the original date-range feature (`a89fff8`),
was recorded into Roborazzi baselines as truth, and then survived a full
`compose-product-auditor` review whose screenshots showed the mismatch plainly. It was only
fixed when the user reported it.

**Root cause:** (1) The audit's consistency dimension was applied by eye — sibling components
in the same container were never compared by measured size, and a 2× height difference hid in
plain sight. (2) `proIconClickable`'s layout-inflating minimum size was a known footgun
(`ProTextAction` in `Interaction.kt` already carries a comment about the same ballooning) but
was never promoted into a guard, so it kept being reached for as the default icon-click
modifier in compact containers. (3) Screenshot verification structurally cannot catch this
class of defect: a wrong baseline is self-consistent, and verify only detects change.

**Guards:**
- Never nest `minimumInteractiveComponentSize`-applying modifiers (`proIconClickable`,
  `proSelectable`) inside compact components (chips, pills, dense list rows). For a secondary
  micro-target inside an already-tappable surface, use `clip(CircleShape)` +
  `proCircularRippleClickable` (non-inflating) instead.
- Repeated/sibling components in one container (a chip row, a button group) must be asserted
  for equal rendered size with a Compose UI test on layout bounds (see
  `JournalChipRowConsistencyTest`) — pixels lie once a defect is recorded as the baseline.
- Product audits must **measure** sibling sizes in reviewed screenshots (pixel-measure the
  bounding boxes), not eyeball them — promoted into `compose-product-auditor` skill and G5.

## 2026-07-13 — Category icon picker's scrollable row shipped with a hard-cut trailing edge

**What slipped:** Expanding the category icon picker from 4 to 20 icons required making its row
scrollable (`horizontalScroll`) instead of a fixed inline row. The fix was applied and screenshots
were recorded/verified green, but the row's overflow edge was a flat, abrupt clip through the
last tile with no fade/peek/indicator — reading as broken/cut-off content, not "swipe for more."
The user caught it by eye from a shared screenshot; neither the planning-phase nor the pre-push
visual-verification pass caught it, because `compose-product-auditor` was never actually invoked
in either phase — the agent reasoned informally ("add `horizontalScroll`, done") instead of
running the skill, and at pre-push only confirmed the code change rendered as intended (right
icons, right order, scrollable), which is a different check from auditing whether the result
would confuse a user. A first attempted fix (a `drawWithContent` + `BlendMode.DstIn` fade
modifier) also silently failed to appear on-screen because the fade modifier was nested *inside*
`horizontalScroll` in the chain, so it measured the full unclipped content width (~1070dp for 20
tiles) and drew its gradient off-screen near the last tile instead of at the visible viewport
edge — confirmed by pixel-sampling the recorded PNG before and after reordering the modifiers.

**Root cause:** (1) The `compose-product-auditor` skill was never invoked as a tool call in
either mandated phase — "consult the skill" was satisfied by informal reasoning, not by running
it, so its own documented dimension ("audit transient states... check drag, fling, and overscroll
edges too") never actually got applied. (2) Pre-push, "the screenshot looks right" was checked
only against "did my change take effect," not against "would this confuse a user" — two
different questions that were conflated. (3) The `drawWithContent`/`graphicsLayer` fade fix has a
non-obvious modifier-ordering requirement (must wrap `horizontalScroll`, not nest inside it) with
no compiler error when done wrong — it just silently draws off-screen.

**Guards:**
- `AGENTS.md` Step 3 and Step 6/G5 UI/UX audit gates now require literally invoking the `Skill`
  tool for `compose-product-auditor` in both phases (planning and post-implementation) and
  stating its verdict in-session — informal reasoning or summarizing the skill from memory no
  longer satisfies the gate.
- New G5 bullet: any scrollable row's clipped edge must fade/peek, never a hard cut; verify by
  pixel-sampling the recorded baseline near the clip boundary (abrupt color jump = defect, gradual
  ramp = correct), not by eyeballing a shrunk screenshot.
- New G5 bullet: an edge-fade/affordance modifier must wrap the scroll modifier (apply outside it
  in the chain) — nested inside, it measures the unclipped content size and renders off-screen.

## 2026-07-18 — A logged guard regressed silently because nothing re-verified it

**What slipped:** The 2026-06-20 entry above ("PIN lockout bypass + release-variant screenshot
tests") prescribed tagging Robolectric Compose UI tests with a `ComposeUiTests`/`ScreenshotTests`
category and excluding both from the release unit-test variant. Only the `ScreenshotTests` half
was ever implemented. `ComposeUiTests` was never created, and 28 plain Compose-interaction test
classes (not Roborazzi screenshot tests) were left completely untagged. This was invisible for an
unknown length of time and was found by accident — an agent session ran the bare `./gradlew test`
(not a documented command) while double-checking an unrelated schema change, and 54 of 79 tests
failed with the exact "Unable to resolve activity for ComponentActivity" error the 2026-06-20 entry
already diagnosed.

**Root cause:** (1) This log's own header says "Promote durable guards into `AGENTS.md` so lessons
are enforced, not just logged" — that promotion never happened for this guard. `AGENTS.md`'s
Testing Contract said nothing about a `ComposeUiTests` category or the debug-only
`compose-ui-test-manifest` constraint, so no future session had any reason to apply it to a new
test file. (2) No gate in the documented 8-step workflow ever runs `testDevReleaseUnitTest` —
`verifyAll`'s `dependsOn` only lists `:app:testDevDebugUnitTest`. A guard about release-variant
behavior has no way to be re-confirmed if no gate ever exercises the release variant. (3) The
retrospective entry itself recorded the guard as a prescription ("tag them... and exclude...")
with no `Verified:` line proving it was carried out — so a half-applied fix and a fully-applied fix
were indistinguishable just by reading the log.

**Guards:**
- Every retrospective entry's "Guards:" section now needs a dated `Verified:` line (command run +
  result) before it counts as closed — see the file header. A guard without evidence is a TODO,
  not a completed fix.
- `AGENTS.md` Testing Contract Core Rule 9 now states the `ComposeUiTests`/`ScreenshotTests`
  tagging requirement directly, so it's discoverable without reading this log.
- `verifyAll` now depends on `:app:testDevReleaseUnitTest` (root `build.gradle.kts`), so an
  untagged Compose UI test breaks the standard gate immediately instead of only surfacing when
  someone happens to run the undocumented bare `./gradlew test`.
