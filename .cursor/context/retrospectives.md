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
