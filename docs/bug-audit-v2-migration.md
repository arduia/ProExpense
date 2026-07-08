# Bug Audit — `refactor/v2-migration` vs User Stories & PRD

**Date:** 2026-07-03 · **Scope:** all 12 feature areas in `docs/user_stories/` plus
`docs/finance_tracker_product.md` (PRD), audited against the code on `refactor/v2-migration`
(head `28356c4b`). Every finding below was verified by reading the shipping code path;
file:line references point at the defect site.

Severity: 🔴 data loss / security / data corruption · 🟠 acceptance criterion broken · 🟡 partial / cosmetic deviation.

---

## 🔴 Critical

### 1. Restored draft corrupts home-currency totals for non-USD users (US-LOG-7, US-CUR-4)
`ExpenseDraftPrefs` persists only amount, category, note, currency and recorded-at —
**not** `homeCurrencyCode` or `exchangeRateRaw`
(`feature/logging/.../ui/ExpenseDraftPrefs.kt:20-45`). `load()` builds an
`ExpenseEntryState` whose `homeCurrencyCode` falls back to the default `"USD"`
(`ExpenseEntryPreviewData.kt:19`). For a user whose home currency is anything else (e.g. EUR):

- a draft logged in EUR is restored as *foreign* (`currencyCode="EUR"` vs `homeCurrencyCode="USD"`),
  so the user is forced to invent a EUR→USD rate; and
- the saved record's `homeCurrencyMoney` is stored **in USD** while every total in the app
  (Home, Journal day totals, Reports, budget) sums `homeCurrencyMoney` assuming it is the real
  home currency — permanently corrupted totals.

### 2. Cancelling an edit leaves a stale draft that duplicates the record (US-LOG-7, US-HIS-6)
`EditExpenseFlow` reuses the same inner `QuickLogFlow`, which continuously writes the
in-progress state to `ExpenseDraftPrefs` (`QuickLogFlow.kt:86-90`) and only clears it on save.
Backing out of an **edit** (US-HIS-6) therefore leaves the *existing record's* values behind as a
"draft". On next launch the Continue/Discard prompt appears; **Continue** routes through
`viewModel.save(...)` (create, not update — `LoggingFeatureEntry.kt:94-103`), inserting a
**duplicate** of a record that already exists. Violates US-HIS-6's "no duplicate rows, no
orphaned drafts" and makes the US-LOG-7 prompt appear after deliberate cancellation.

### 3. Pre-PIN draft flow exposes stored financial data (US-LOG-7 security NFR)
The draft branch renders the **full** Quick Log flow before the PIN gate
(`app/.../ExpenseApp.kt:330-342`), and `LoggingFeatureEntryImpl` always loads live tag options
and live category lists (`LoggingFeatureEntry.kt:74-79, 180-192`). From the pre-PIN Details step
anyone can open the `@` tag sheet and read **active event names/dates and debt person names +
amounts**, plus all custom category names — without ever entering the PIN. The story's rule is
that the draft prompt "exposes nothing beyond what the user already typed".

### 4. JSON export can never be re-imported (US-IE-1, US-IE-2)
JSON export always wraps `expenses.json` in a zip (`ExportSettingsFlow.kt:63-75` →
`ExportFileWriter.writeZip`), but the zip importer only extracts `expenses.csv`
(`ImportZipReader.kt:21-32` — `EXPENSES_ENTRY = "expenses.csv"`). Picking a JSON-export zip in
Data import ends in `NoExpensesCsv` → generic "couldn't read file" error. A **password-encrypted
JSON export is unrecoverable in-app** — the PRD's "Secure Import & Export" backup promise fails
exactly for the users who used the security option.

### 5. Multi-line notes corrupt CSV export/import (US-IE-1/2, US-LOG-3)
The note field is multi-line (`DetailNoteField` has no `singleLine`,
`shared/.../DetailFieldCard.kt:97-104`), but `toCsv` escapes only quotes — not newlines
(`SqlDelightImportExportRepository.kt:172-192`) — and `parseCsv` naively splits the whole file
on `\n` (`:224`). Any record whose note contains a newline exports as a broken row and is
**silently dropped** (or column-desynced) on re-import. JSON is also lossy: `escapeJsonString`
writes `\"`/`\n`, but `extractJsonString`'s regex `"note":"([^"]*)"` stops at the first escaped
quote and nothing ever unescapes — notes with quotes truncate, notes with newlines come back with
literal `\n` (`:390-398, 419-424`).

---

## 🟠 High — acceptance criteria broken

### 6. Home "Spend this month" is actually the all-time total (US-HOME-1)
`ExpenseApp.kt:210-213` sums **every** record ever (`records.sumOf { … }`) into the header
labelled "Spend this month" (`HomeScreen.kt`, `home_spend_this_month`). The budget summary two
lines below *does* filter to the current calendar month (`ExpenseApp.kt:222-226`), so the header
contradicts itself once any prior-month record exists.

### 7. Home sparkline mixes currencies (US-CUR-4)
`buildSparklinePoints` sums `it.money.amount.valueInCents` — the **original** per-record
currency — instead of `homeCurrencyMoney` (`ExpenseApp.kt:510-520`). US-CUR-4 FR: "totals sum
`homeCurrencyMoney`, never the original per-record amount". A ¥5,000 lunch dwarfs a $50 one.

### 8. Event pre-tagging never works ("Add expense" from Event Detail / Active Event card) (US-EVT-4, US-HOME-1)
`LoggingFeatureEntryImpl.QuickLogFlow` resolves `initialLinkedEventId` against
`uiState.tagOptions` **at first composition** (`LoggingFeatureEntry.kt:80`), but
`LoggingViewModel` is a Koin `factory` (`di/LoggingModule.kt:21`) whose tag options load
asynchronously — they are always empty on first composition. The inner flow then freezes
`startState` in `remember { mutableStateOf(startState) }` (`QuickLogFlow.kt:77`), so the
pre-selected event link is silently lost every time: the expense saves **untagged** and event
spend/remaining never updates. (Contrast: `EditExpenseFlow` correctly defers composition until
data is loaded.)

### 9. Journal Detail: `@` tag never shown, custom category shows raw id (US-HIS-5)
`detailStateFor` hardcodes `linkedTag = null` and builds the category label from the **id**
(`(row?.categoryId ?: "food").uppercase()`), ignoring the `categoryNames` map that the flow
already receives (`feature/history/.../JournalFlow.kt:268-279`). Result: a record linked to an
event/debt shows no tag in Detail (AC 1 requires it), and a custom category renders like
`COFFEE-1751234567890`. Related: Journal **row** meta uses the static `expenseCategoryLabel`
instead of the live name (`HistoryFeatureEntry.kt:143`), so custom categories are wrong in the
list too.

### 10. Change-PIN flow does not exist (US-AUTH-7 Scenario 1)
More → "PIN authentication" only branches to *enable* (`PinSetupFlow`) or *disable*
(`MoreFlow.kt:206`: `if (pinEnabled) showDisablePinConfirm else onPinClick()`). There is no
verify-current → enter-new → confirm path anywhere; the only way to rotate a PIN is disable +
re-enable (which also silently drops biometric enrollment and requires re-entering the security
question). Disable is correctly verified (`MoreFlow.kt:316-330`), but Scenario 1 is unimplemented.

### 11. Theme and Language settings are dead rows (US-MORE-3, US-MORE-1)
The hub renders "Language" and "Theme" rows (`MorePreviewData.kt:53-54`), but
`MoreFlow.onSettingClick` has no `"theme"`/`"language"` handler and no corresponding `MoreStep`
exists (`MoreFlow.kt:52, 197-217`) — tapping them does nothing. US-MORE-3 requires Light/Dark/
System selection applying immediately; no theme preference exists anywhere (the app only follows
the OS). `LocaleRepository`/`AppMetaLocaleRepository` exist in storage but have no UI.

### 12. Lockout counter never resets after the countdown (US-AUTH-5)
AC: "the attempt counter resets once the countdown completes." The countdown completion only
clears **UI** state (`PinLockFlow.kt:96-116`); the persisted `failedAttemptCount` stays at 5+.
Combined with the escalating `lockoutDurationMs` (5→30s, 6→60s, 7+→5min,
`PinAuthRepositoryImpl.kt:250-255`), one more wrong digit after waiting 30s locks the user out
for 60s, then 5 minutes — instead of the documented fresh 5 attempts.

### 13. Saved shared-cost splits are editable (US-SHC-5 Scenario 2)
"No edit entry point exists for a saved split" — but tapping a history row opens the Summary with
a fully active **Save** button and a "Switch to custom" path back into the input screen
(`SharedCostsFlow.kt:249-284`), wired to a real `UpdateSharedCostUseCase`
(`SharedCostFeatureEntry.kt:62-68`). Either the story or the code is wrong; as specified, this is
a violation of "saved splits are immutable except for deletion".

---

## 🟡 Medium

### 14. Details date field shows a hardcoded "Today, May 25" (US-LOG-3/4)
Fresh entries inherit preview defaults `dateLabel = "Today, May 25"`, `timeLabel = "12:30 PM"`
(`ExpenseEntryPreviewData.kt:10-11`); nothing recomputes them from `recordedAtEpochMillis`
(which *is* correct) until the picker is opened. Every user sees a wrong date/time label on
Details on any day other than May 25. Restored drafts show it too (labels aren't persisted).

### 15. Quick-note sheet: no 200-char cap, no counter, no prefill (US-HIS-4, US-LOG-6)
`JournalQuickNoteSheetContent`'s `BasicTextField` is uncapped (`JournalSheets.kt:64-86`) —
US-LOG-6's "200 characters everywhere a note is entered" is not enforced — and long-press always
starts from an empty field (`JournalFlow.kt:135-137`), silently **overwriting** any existing note
on save.

### 16. Custom category icon is discarded; color picker missing (US-CAT-2)
The form collects an icon (`CategoryNewSheetContent.onIconSelected`) but the save callback passes
only the name (`CategoryListFlow.kt:94-99` → `SaveCategoryUseCase` — `Category` has no
icon/color fields at all, `core/domain/.../Category.kt`). There is no color picker anywhere.
AC 1 ("pick an icon and a color") is two-thirds unimplemented; custom categories all render with
the generic fallback badge.

### 17. Blank profile name greets the user as "Maya" (US-ONB-3)
Business rule: no name → generic greeting. Implementation: Home falls back to the preview
fixture's `greetingName = "Maya"` (`ExpenseApp.kt:203, 262` + `HandoffPreviewData.kt:53`) and the
More profile card falls back to `previewMoreHub.profile.name` ("Maya") (`MoreFlow.kt:155-160`).

### 18. Success toast never visible; failed saves show "Expense saved" (US-LOG-1/3)
The inner flow sets `toastMessage = savedMessage` **before** the async save result
(`QuickLogFlow.kt:157-159, 200-203`); on success the flow is immediately unmounted
(`ExpenseApp.onExpenseSaved`) so the toast host disappears, and the `LoggedExpenseHandoff` that
could drive a Home toast is discarded (`ExpenseApp.kt:274`). On `Failed` the outcome is silently
swallowed (`LoggingFeatureEntry.kt:98-100`) while the flow keeps showing the stale success toast.

### 19. Recent list shows the entire history (US-HOME-2)
"Recent shows the last 5–10 entries" — `ExpenseApp` passes **all** day groups and
`HomeRecentSection` renders them in a full-height `LazyColumn` with no cap
(`ExpenseApp.kt:236-260`, `HomeScreen.kt:459-520`).

### 20. Event progress has no amber tier (US-EVT-3)
Spec: 0–100% blue, 101–110% amber, >110% red bold. `ComputeEventProgressUseCase` exposes only a
binary `isOverBudget` (`EventBudgetUseCases.kt:20-33`) and `EventBudgetCard` renders exactly two
states (blue/danger, `shared/.../EventBudgetCard.kt:47`). The 101–110% warning band cannot exist.

### 21. Debt Add Record is missing fields (US-DEBT-2)
The sheet has person, amount, date and due date only — **no note field** (≤200) and **no `@`
linked-expense field** (`DebtAddSheet.kt`; no `note` anywhere in the file). Amount entry is
whole-dollar only (`input.filter { it.isDigit() }.take(9)`, `DebtAddSheet.kt:268`) so cent
amounts can't be recorded.

### 22. Uncategorized can't be filtered in Journal (US-CAT-3 Scenario 3)
FR: "Uncategorized is included in Journal filters … when expenses exist under it." The filter
chips are built solely from `CategoryRepository` rows (`HistoryFeatureEntry.kt:80-83`) and
Uncategorized is deliberately never seeded as a row — so reassigned records exist but no chip can
select them.

### 23. Recovery attempt counter resets by leaving and re-entering the flow (US-AUTH-8)
`onForgot` zeroes `recoveryAttempts`/`recoveryExhausted` every time (`PinLockFlow.kt:209-213`) —
the in-memory 5-attempt budget (and the "Reset app" last-resort gate behind it) resets on each
Back → Forgot round trip. The shared *persisted* lockout still escalates, which mitigates but
doesn't restore the documented behavior.

---

## 🟡 Low / notes

- **Dangling links after delete** — deleting a debt (or event) removes only that row
  (`DebtUseCases.kt:60-64`, `SqlDelightDebtRepository.kt:48-50`); linked `FinanceRecord`s keep a
  `RecordLink.ToDebt/ToEvent` pointing at nothing. Display degrades gracefully
  (`RecordLink.tagLabel` → null) but exports carry the dead `link_id`, and US-DEBT-3 says "only
  the debt link is removed" — the link is in fact kept.
- **Backup is expenses-only** (US-IE-2 / PRD Use Case 6) — events, debts and shared-cost CSVs are
  export-only reference files; a device migration silently loses all three (documented in the
  story notes as intentional, but at odds with the PRD's "import from a backup or another
  device" and "no record type silently excluded").
- **Export screen file list is fake** — `ExportSettingsFlow` always renders
  `previewMoreExportFiles` fixtures (`ExportSettingsFlow.kt:51`), regardless of real data or the
  chosen format (still shows CSV rows in JSON mode).
- **Shared-cost saved toast is never shown** — `savedToastMessage` defaults to null and the app
  entry never supplies it (`SharedCostsFlow.kt:108`, `SharedCostFeatureEntry.kt`).
- **PIN setup success message missing** (US-AUTH-1 Scenario 3) — setup just dismisses; the
  documented "PIN is now active…" confirmation is never rendered.
- **Possible PIN-gate bypass on API 24–28** — `unlocked` is `rememberSaveable`
  (`ExpenseApp.kt:93`) and is cleared in an `ON_STOP` observer; on API < 29
  `onSaveInstanceState` runs *before* `onStop`, so process death after backgrounding can restore
  `unlocked = true` and skip the lock screen (minSdk is 24). Needs on-device confirmation.
- **US-EVT-5 24h grace period** — known, documented gap: closed events lock immediately
  (`readOnly = status == CLOSED`), no `closedAtEpochMillis` exists yet.
- **Home header variants** (US-HOME-1) — spec says exactly one of three header variants; the
  build renders month-spend and budget-progress together rather than switching.
- **Journal in-day ordering** (US-HIS-1 Scenario 2) — "newest-*created*-first" is approximated by
  `recordedAtEpochMillis` (no creation timestamp exists); a backdated entry created later sorts
  by its backdate, not creation order.
- **Draft survives deliberate ✕-close** (US-LOG-7) — closing Quick Log with a typed amount keeps
  the draft (comment says intentional), so the next launch shows a restore prompt for input the
  user consciously abandoned. Spec only promises drafts for crash/force-close.

## Clean areas

Reports (US-REP-1…4) matched their stories in all checked paths (period math, top-5 + Other
rollup, all-uncategorized flag, granularity anchoring, empty/loading states). Currency settings
persistence (US-CUR-1/2/3), PIN hashing/lockout persistence, CSV quote/empty-field parsing, the
shared-cost split math (no rebalancing), and category delete → Uncategorized reassignment also
verified clean.
