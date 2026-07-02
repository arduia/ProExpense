# Retrospectives — Post-Mortem Guard Log

> Append-only. Written automatically when Step 8 triggers (large change + unexpected gate failure).
> Promote durable guards into `AGENTS.md` so lessons are enforced, not just logged.

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
