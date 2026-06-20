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
