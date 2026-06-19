# Pro Expense — Detail Design Plan (Integration-Focused)

**Date started:** 2026-06-19
**Status:** Draft — core technical stack resolved. **Android UI/design system/components are already built.** This document now plans the **integration** of that existing Compose UI with the shared business logic, data layer, and state management locked in across the 18 technical solutions — not UI design itself.

**Platform state:**
- ✅ **Android:** Compose UI + design system components complete
- ⏳ **iOS:** SwiftUI UI not yet built — deferred to a later phase, **not part of this build**. However, the KMP architecture must be built so that adding iOS later is genuinely easy — not a rewrite. This means strict discipline on the commonMain/platformMain boundary starting now, even though only Android is being shipped in the near term. See **iOS-Readiness Principles** below.

**References:**
- `pro-expense-implementation-brainstorm.md` — the 18 resolved technical solution areas (A1–F18)
- Screen flow doc (15 screens, prior session) — Splash, Onboarding, Home, Add Expense, Journal, Journal Detail, Event Budget, Event Detail, Debt Tracker, Shared Costs, Category List, Reports, Settings, PIN Setup, PIN Entry
- `finance_tracker_product.md` (PRD) — 9 use cases, 6 scoped to MVP
- Existing Android UI/component library (location/source TBD — needs to be referenced directly once shared)

---

## iOS-Readiness Principles (apply during ALL Android integration work)

These aren't iOS tasks — they're constraints on how the Android integration work gets done, so that the eventual iOS phase is additive, not a rewrite. Per the E13 decision ("everything shared except UI"), the discipline is:

1. **No business logic in `androidMain`.** Anything that isn't literally Compose rendering or an Android-only system API call (BiometricPrompt, Android Keystore, SAF file picker) belongs in `commonMain` — even if, today, only Android consumes it. The temptation to write something "quickly" directly in an Android ViewModel because iOS isn't being built yet is exactly the shortcut that creates rework later.
2. **`expect/actual` declared at real platform seams from day one** — even though only the Android `actual` is implemented now, the `expect` interface should be written as if iOS exists, so the seam is already in the right place (Keystore/Keychain wrapping, BiometricPrompt/LocalAuthentication, file system access). Adding the iOS `actual` later becomes "implement this interface," not "find every place this assumption was baked in and extract it."
3. **ViewModels expose platform-agnostic state** (`StateFlow` of plain data classes/enums) — never Android types (e.g. no `Context`, no `Resources`, no Compose-specific types) leaking into commonMain ViewModels. This is what keeps Phase-6-style "iOS kickoff" limited to a UI layer + one interop bridging spike, rather than a logic rewrite.
4. **Repository pattern stays the only data access path** (per F17) — no screen/ViewModel talks to SQLDelight directly. This was already a decision; it's now also explicitly an iOS-readiness requirement, since it's the seam a future sync layer *and* a future iOS UI both rely on staying clean.
5. **Periodic self-check, not a one-time audit:** at the end of each phase below, do a quick pass — "could this phase's commonMain code run unmodified if we swapped in iOS tomorrow?" If the honest answer is no, that's a signal something leaked into androidMain that shouldn't have.

---

## How to read this document

Since UI components already exist (Android), each screen's work is now an **integration task**: connect existing Compose components to a ViewModel, which connects to shared repositories/business logic, which connects to the encrypted SQLCipher DB. Two cross-referenced views:

1. **Screen → Solution Map** — for each of the 15 screens, which technical solutions (by ID, e.g. A2, C6) need to be wired into the *existing* UI for that screen. This is the "what needs connecting" view, not "what needs designing."
2. **Build Sequence** — the order to do the integration work in, organized in phases that respect dependencies (e.g. the encrypted DB and repository layer must exist and be stable before any screen's ViewModel can be wired to real data).

A third section, **Per-Screen Integration Detail**, will hold the actual ViewModel contracts, state shapes, and component-to-data-binding specifics as we work through each screen.

---

## 1. Screen → Solution Map

| # | Screen | Primary Technical Solutions | Notes |
|---|--------|------------------------------|-------|
| 01 | Splash | C6 (key/DB unlock check), F17 (repository init seam) | Decides routing: first launch vs. PIN Entry vs. Home |
| 02 | Onboarding | A1 (initial schema bootstrap) | First-run only |
| 03 | Home | A1, D9 (Add button entry point), D10 (budget summary), C6/A2 (PIN banner) | Banner logic from the PIN-rotation decision lives here |
| 04 | Add Expense | A1, D9 (full <5s stack), A2 (live DB write) | Two sub-screens: Amount Input, Details |
| 05 | Journal | A1, D12 (search), E13 (ViewModel/state) | Grouped-by-date list + flat search results |
| 06 | Journal Detail | A1 | Edit/delete of a single expense |
| 07 | Event Budget | A1, D10 (budget engine, color thresholds) | |
| 08 | Event Detail | A1 | |
| 09 | Debt Tracker | A1 | $0 validation edge case already locked in screen flow doc |
| 10 | Shared Costs | A1, D11 (split calculator) | Max 20 people, rounding remainder logic |
| 11 | Category List | A1 | 20-char name limit |
| 12 | Reports | D10 (budget/avg engine), F17? (future export of report data) | |
| 13 | Settings | B4/B5 (export/import entry), C6/C7 (PIN/biometric mgmt), F16 (no-migration messaging lives here), E14 (language/currency) | Heaviest cross-reference of any screen |
| 14 | PIN Setup | C6, C7, A2 (key rotation trigger point) | Biometric enrollment MUST happen here per A2/C7 — only moment raw key is in memory. Integration task: wire existing PIN Setup UI to trigger rotation + Keystore wrap on completion |
| 15 | PIN Entry | C6, C7, C8 (lockout + biometric restriction) | |

**Solutions with no single owning screen** (cross-cutting, span everything): A2 (DB encryption itself), A3 (migration — N/A per F16), E13 (module boundaries), E15 (theming), F17 (sync seam), F18 (build/CI).

---

## 2. Build Sequence (Integration)

Organized in phases — each phase assumes the prior phase's foundations exist. Since Android UI already exists for all 15 screens, every phase below is "wire the existing screen to real data," not "build the screen."

### Phase 0 — Foundation (no screen wiring yet)
*Nothing in Phase 1+ can really integrate without this working first.*
- A1: finalize schema (entities, relationships, the @ tag polymorphic link)
- A2/C6: SQLCipher + SQLDelight wired up, random-key-at-first-write, `PRAGMA rekey` path tested
- E13: KMP module structure scaffolded — **commonMain/androidMain split, with the `iosMain` source set also created (empty/stub) from the start**, not bolted on later. This costs almost nothing now and is the single biggest lever for "iOS later = easy": the Gradle/module structure already expects three targets, so adding real iOS code later is additive, not restructuring. Repository pattern in place per F17.
- E15: shared theme token file (colors, spacing, typography) — **note:** since Android UI/components already exist, this step is really a *reconciliation* — confirm the existing Compose components already use these token values (or close to them) rather than hardcoded ones, and adjust if they've drifted from the brand sheet

### Phase 1 — Core Loop Integration
- Screens: **03 Home, 04 Add Expense, 05 Journal, 06 Journal Detail, 11 Category List**
- Task shape: wire existing Compose screens to new ViewModels → ViewModels call shared repositories (A1/A2) → confirm UI reflects live DB state
- D9: full quick-add performance stack — integration concern here is making sure the *existing* Add Expense UI components support optimistic update + background write without needing UI changes
- D12: search (SQL LIKE, including amount search) wired into existing Journal search UI
- This phase produces a usable, loggable expense tracker — the minimum "casual records" product, now running on real (not mock) data

### Phase 2 — Security Layer Integration
- Screens: **14 PIN Setup, 15 PIN Entry**
- C6/C7: PIN→key rotation, biometric OS-level gating wired into existing PIN screens
- C8: lockout timer (encrypted prefs, biometric restricted during lockout)
- Home banner nudge (from A2/C6 decision) — **check whether existing Home UI already has a banner/notice component to reuse, or whether this is new UI** (the one possible exception to "no new UI" in this whole plan — flagged as a question below)

### Phase 3 — Budget & Planning Integration
- Screens: **07 Event Budget, 08 Event Detail, 12 Reports**
- D10: full budget engine — daily average, auto-reset, color thresholds, UTC storage/local display, wired into existing progress bar/chart components

### Phase 4 — Social/Shared Features Integration
- Screens: **09 Debt Tracker, 10 Shared Costs**
- D11: split calculator (even/custom, rounding remainder) wired into existing Shared Costs UI
- @ tag linking (A1) gets exercised fully here, since both Event and Debt are tag targets

### Phase 5 — Data Portability & Settings Integration
- Screens: **13 Settings**
- B4/B5: encrypted ZIP export/import, signature, password — wired into existing Settings UI's export/import controls
- E14: localization (language/currency selectors) wired to actual locale-aware formatting logic
- F16: no-migration messaging surfaced in existing Settings/release-notes UI

### Phase 6 — Android Closeout + iOS-Readiness Check
- F18: Android build/CI hardening
- Full regression pass across Phases 1-5 on Android with real encrypted data, real export/import, real PIN rotation — this is the actual MVP candidate build
- **iOS-readiness audit (not iOS UI work):** walk back through Phases 0-5 and verify the iOS-Readiness Principles actually held — confirm no business logic leaked into `androidMain`, confirm `expect/actual` seams are clean, confirm ViewModels expose no Android-specific types. This is the checkpoint that proves "iOS later will be easy" rather than just asserting it.
- The E13 KMP↔Swift interop bridging spike (NativeCoroutines/Skie, etc.) still happens **once an iOS phase is actually scheduled** — it's a real piece of work, but it's bridging-library work, not architecture rework, *if* the principles above were followed throughout

---

## Sequencing Notes (Updated)

1. **iOS UI is deliberately deferred to a later phase** — this is a scope decision, not an architecture gap. The architecture itself (commonMain-first, `expect/actual` seams, repository pattern, empty `iosMain` stub) is built now so that the later iOS phase is "add a UI layer + bridge state," not "extract logic from Android code first." See **iOS-Readiness Principles** above — this is the mechanism that makes "supported easily" true rather than aspirational.
2. **Phase 2 (Security) before Phase 4 (Shared Costs/Debt)** — still flexible, no unique dependency forces this order.
3. **Export/Import (Phase 5) depends on Phase 0-4 schemas being stable** — same risk as before, building the export format too early risks redefining `manifest.json`'s schema-version field repeatedly.
4. **Every phase should end with the Phase 6-style self-check applied locally** (not just saved for the very end) — e.g. after Phase 1, briefly confirm the Add Expense ViewModel doesn't reference anything Android-specific. Catching a leak after one phase is cheap; catching it after five is not.

---

## 3. Per-Screen Integration Detail

*(To be filled in screen-by-screen — ViewModel contracts, state shapes, and how each existing Compose component binds to shared data. Starting with Phase 1 screens since they're first in the build sequence.)*

### Screen 03 — Home

**Relevant solutions:** A1 (schema), A2/C6 (PIN banner from key-rotation decision), D9 (Add button entry point — ViewModel pre-warming), D10 (budget summary display)

**What this screen needs wired:**

1. **Budget summary (D10)**
   - Pull current month's total spent + active budget amount from the repository (A1, scoped by the UTC-stored/local-displayed date range from D10)
   - Compute `progressPercent` and map to one of the three locked color thresholds (0–100% blue / 101–110% yellow / 110%+ red)
   - Daily average shown here uses the **revised rule**: total spent ÷ total days in month (not elapsed days) — if the existing Home UI's average label still says or implies "so far," that copy may need a small update per the open UI-label-wording question

2. **Recent expenses list / quick view**
   - Read-only feed from the Journal repository — likely just the most recent N entries, not the full Journal logic (D12 search doesn't apply here, that's Screen 05)

3. **Floating Add (+) button → Add Expense pre-warming (D9)**
   - Per the D9 decision, the Add Expense ViewModel should be instantiated **on button press**, not on navigation completion — so Home's button handler needs to trigger ViewModel creation immediately, then navigate. This is a Home-screen integration detail, not just an Add Expense one.

4. **PIN setup banner (A2/C6 decision)**
   - Dismissible banner, shown only while no PIN has been set (i.e., DB is still on the random first-launch key, not yet rotated)
   - Tapping → navigates to PIN Setup (Screen 14)
   - Dismiss behavior: hides for the session, reappears after some interval if no PIN is set yet (per our earlier decision) — needs a small piece of state (e.g. `lastBannerDismissedAt` in the same secure/local prefs used for lockout, C8) to track this
   - **This is the one place flagged as possibly needing new UI** rather than pure integration — open question, see below

**Suggested shared ViewModel shape (commonMain):**
```
data class HomeUiState(
    val currentMonthSpent: Money,
    val currentBudget: Money?,
    val progressPercent: Float,
    val budgetColor: BudgetColorTier,      // enum: ON_BUDGET, SLIGHTLY_OVER, SIGNIFICANTLY_OVER
    val dailyAverage: Money,
    val recentExpenses: List<ExpenseSummary>,
    val showPinSetupBanner: Boolean
)

class HomeViewModel(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val securityState: SecurityStateRepository  // exposes whether PIN/rotation has happened
) {
    val uiState: StateFlow<HomeUiState>
    fun onAddExpenseClicked()      // pre-warms Add Expense ViewModel per D9
    fun onPinBannerDismissed()
    fun onPinBannerTapped()        // navigates to PIN Setup
}
```

**I need from you:** *(resolved)*
- ✅ **Banner is confirmed new UI** — this is the one deliberate exception to "integration only" for Phase 1. Needs a small new Compose component (dismissible banner/notice card), styled per the existing design system (use E15's shared theme tokens, don't introduce new ad-hoc colors/spacing for it).
- ✅ **No existing Home ViewModel stub** — the `HomeViewModel`/`HomeUiState` shape above is the actual spec going forward, not a draft to reconcile against something else.

**Action items unlocked by this:**
- Add "new banner component" as an explicit task in Screen 03's integration work — small scope, but worth tracking separately from the rest since it's UI work, not wiring
- The `HomeUiState.showPinSetupBanner: Boolean` field now has a real consumer to build against

### Screen 04 — Add Expense

**Relevant solutions:** A1 (schema, @ tag polymorphic link), A2 (live encrypted DB write), D9 (full <5s performance stack)

**Two sub-screens per the PRD/screen flow:** Amount Input → Details. Each needs its own state slice, but they likely share one ViewModel instance across the flow (instantiated early per D9, point 7).

**What this screen needs wired:**

1. **Amount Input (sub-screen 1)**
   - Default category pre-selected (Food, per PRD/D9 point 2) — pulled from a Settings-configurable default, not hardcoded
   - Numeric keypad — if this is a custom in-layout component already built (per D9 point 3), it just needs binding to the ViewModel's amount field, not building
   - "Save" here alone (no Details) must succeed — only Amount is required (D9 point 5) — so the ViewModel needs a direct save path that defaults note/tag/date-override to empty/today

2. **Details (sub-screen 2, optional)**
   - Note field (200-char limit, per PRD)
   - @ tag field — links to **one** active Event or Debt record (A1's polymorphic link, single-type in MVP). Needs a lookup/picker against both Event and Debt repositories, but stores a single `tagType + tagId` pair, not two separate optional fields
   - Date override (defaults to today)
   - Category override (if user wants something other than the pre-selected default)

3. **Save flow (D9 points 1, 4, 6)**
   - DB connection assumed already open/warm (per A2's "keep-alive connection" decision) — this screen doesn't manage that lifecycle itself, just assumes it
   - On Save: **optimistic UI** — navigate back to Home immediately, write happens on a background coroutine. Per the open question still unresolved from the brainstorm doc, confirm whether silent-toast-on-failure is still the agreed behavior before wiring this up for real
   - Category chips pre-loaded in memory at app start (D9 point 6) — this screen just reads from that in-memory list, doesn't query the DB for categories on open

**Suggested shared ViewModel shape (commonMain):**
```
data class AddExpenseUiState(
    val amount: String,                    // raw input string, parsed/validated separately
    val selectedCategory: Category,        // defaults to configured default (Food)
    val note: String,                      // max 200 chars
    val tag: ExpenseTag?,                  // nullable; holds (tagType: EVENT|DEBT, tagId)
    val date: LocalDate,                   // defaults to today
    val isSaving: Boolean,
    val saveError: String?                 // for the optimistic-write-failure case
)

class AddExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryProvider: CategoryProvider,    // in-memory, app-start-loaded
    private val eventRepository: EventRepository,      // for @ tag lookup
    private val debtRepository: DebtRepository          // for @ tag lookup
) {
    val uiState: StateFlow<AddExpenseUiState>
    fun onAmountChanged(value: String)
    fun onCategorySelected(category: Category)
    fun onNoteChanged(value: String)
    fun onTagSelected(tag: ExpenseTag?)
    fun onDateChanged(date: LocalDate)
    fun onSaveFromAmountOnly()     // the "single tap from sub-screen 1" path
    fun onSaveWithDetails()        // full save from sub-screen 2
}
```

**I need from you:** *(resolved)*
- ✅ **Numeric keypad is already a custom-built component** — D9 point 3 is pure wiring (bind to `uiState.amount`), no UI work needed.
- ✅ **@ tag picker UI already exists and already distinguishes Event vs. Debt** — confirms the `ExpenseTag?` shape above (tagType + tagId) maps cleanly onto an existing component; just need to confirm the exact prop/callback names once the component source is available.

**Result:** Screen 04 is fully integration-only, no new UI exceptions — unlike Screen 03's banner.

**Phase 1 UI work summary (the two small exceptions to "integration only"):**
- Screen 03: new dismissible PIN-setup banner component
- Screen 05: date-grouped section header rendering for the Journal list (logic + UI both new, since neither existed)

Everything else across Screens 03, 04, 05 is wiring existing components to real shared ViewModels/repositories.

---

### Screen 05 — Journal

**Relevant solutions:** A1 (schema/repository read), D12 (search — SQL LIKE, including amount), E13 (shared ViewModel/state pattern, same discipline as other screens)

**What this screen needs wired:**

1. **Main Journal list (grouped view)**
   - Per the existing screen-flow decision: entries **grouped by expense date**, with created-date ordering within each group — this is a repository query concern (A1), the grouping itself can be done in the shared ViewModel from a flat list, or the SQLDelight query can return pre-grouped results — worth deciding which based on how the existing UI component expects its data (flat list with section headers, vs. a nested grouped structure)
   - Pagination/lazy-loading: not yet decided — worth flagging since a casual user's full history could still be a few thousand rows over time; confirm whether the existing list component expects all data at once or supports incremental loading

2. **Search (D12)**
   - Triggered from a search entry point (icon/field) — switches the screen (or a sub-view) into **flat list** mode per the locked decision, no date grouping in search results
   - Debounced (~200-300ms) query against notes, category name, and amount (per the D12 decision, all three are in scope)
   - Empty state for "no results" — check if this matches the existing pastel/illustrated empty-state pattern from the brand sheet, or if search needs its own variant

3. **Tap-through to Journal Detail (Screen 06)**
   - Each row navigates with the expense ID — straightforward, no special logic beyond passing the ID

**Suggested shared ViewModel shape (commonMain):**
```
data class JournalUiState(
    val groupedEntries: List<JournalDateGroup>,   // normal browsing mode
    val searchResults: List<ExpenseSummary>?,     // non-null only when actively searching, flat
    val searchQuery: String,
    val isSearchActive: Boolean,
    val isLoading: Boolean
)

data class JournalDateGroup(
    val date: LocalDate,           // expense date (local display, per D10's UTC-storage/local-display rule)
    val entries: List<ExpenseSummary>
)

class JournalViewModel(
    private val expenseRepository: ExpenseRepository
) {
    val uiState: StateFlow<JournalUiState>
    fun onSearchQueryChanged(query: String)   // debounced internally
    fun onSearchActivated()
    fun onSearchDismissed()
    fun onEntryTapped(expenseId: String)       // triggers navigation to Journal Detail
}
```

**I need from you:** *(resolved)*
- ✅ **Grouping is still open** — the existing Compose UI is just a list shell; date-grouping/section logic hasn't been built yet. This means the `JournalDateGroup` shape proposed above can be the actual source of truth — the repository/ViewModel decides the grouping, and the Compose UI just needs to render `List<JournalDateGroup>` with section headers. This is a small but real UI task (section header rendering), not pure wiring — flagging it alongside Screen 03's banner as Phase 1's second "needs a bit of UI" item, not a big build.
- ✅ **Pagination/infinite-scroll is NOT built yet** — confirmed gap. For MVP, given the casual-records scale (thousands, not millions, of rows — same reasoning as the D12 SQL LIKE decision), it's reasonable to **defer real pagination** and load the full Journal list at once for now, revisiting only if real usage shows a performance problem. This keeps Phase 1 scope tighter; flagging as a deliberate "good enough for MVP" call rather than a silent gap.
- ✅ **Search entry point already exists** — D12 wiring is pure integration here, no new UI.

---

## Phase 2 — Security Layer Integration Detail

### Screen 14 — PIN Setup

**Relevant solutions:** C6 (PIN→key derivation), C7 (biometric enrollment — must happen here, only moment raw key is in memory), A2 (the actual key rotation operation), C8 (lockout state initialization)

**This is the single most consequential screen in the app** — it's where the DB transitions from a random first-launch key to a user-controlled one, and where biometric protection gets wrapped around that key for the first time. Per the A2/C6 decision, this is a one-shot operation: it can't be easily "redone" later without the raw key passing through memory again.

**What this screen needs wired:**

1. **PIN entry + confirmation (standard 6-digit, twice)**
   - Straightforward UI concern, likely already built per the screen flow doc's existing PIN Setup spec — confirm if true once component source is available

2. **Key derivation (C6)**
   - On confirmed PIN: derive a key via PBKDF2/Argon2 + per-install salt (algorithm choice still an open question from the brainstorm doc — needs resolving before this is actually implementable, not just specified)
   - Salt generation/storage: salt itself isn't secret, but needs a stable storage location (likely alongside the encrypted prefs used for C8, or a small dedicated table/pref)

3. **Key rotation (A2)**
   - Execute `PRAGMA rekey` against the live SQLCipher connection, swapping from the random first-launch key to the new PIN-derived key
   - **Crash-safety:** per the still-open question on rekey atomicity, this needs a backup-before-rekey safeguard until SQLCipher's rekey is confirmed atomic on both target platforms — for Android-only MVP, this means confirming on Android specifically first
   - On success: discard/overwrite the old random key in Android Keystore

4. **Biometric enrollment (C7)**
   - Offered as a step in this same flow (likely right after PIN confirmation, before or after rotation completes) — "Would you like to use fingerprint/Face ID instead of typing your PIN?"
   - If accepted: wrap the new PIN-derived key in Android Keystore with `setUserAuthenticationRequired(true)`, per the C7 mechanism — this must happen while the raw key is still in memory from step 3, not as a separate later step
   - If declined: skip wrapping, PIN-only going forward (biometric can still be enabled later from Settings, but per A2/C7's constraint, enabling it later requires the key to pass through memory again — a smaller, separate re-wrap operation, not a rotation)

5. **Lockout state initialization (C8)**
   - Ensure `failedAttemptCount` and `lockoutUntil` exist in the secure key-value store, initialized to 0/null, ready for PIN Entry to use immediately

**Suggested shared ViewModel shape (commonMain):**
```
data class PinSetupUiState(
    val enteredPin: String,
    val confirmPin: String,
    val step: PinSetupStep,            // ENTER, CONFIRM, BIOMETRIC_OFFER, IN_PROGRESS, SUCCESS, ERROR
    val biometricAvailable: Boolean,   // platform check result
    val errorMessage: String?
)

class PinSetupViewModel(
    private val securityRepository: SecurityRepository,  // owns key derivation + rekey + Keystore wrap
    private val biometricCapability: BiometricCapability  // expect/actual platform check
) {
    val uiState: StateFlow<PinSetupUiState>
    fun onPinEntered(pin: String)
    fun onPinConfirmed(pin: String)
    fun onBiometricOffered(accepted: Boolean)
    fun onSetupComplete()   // triggers rotation + optional biometric wrap, reports success/failure
}
```

**Blocking technical decision — ✅ resolved: PBKDF2**
- PBKDF2 chosen over Argon2 — simpler, no native library dependency on either platform (both Android's `javax.crypto` and iOS's `CommonCrypto`/`CryptoKit` have PBKDF2 built in natively), which matters given the iOS-readiness principle of keeping platform-specific dependencies minimal until that phase actually starts.
- `securityRepository`'s derivation function: `PBKDF2(pin, salt, iterations, keyLength) -> ByteArray` — iteration count still needs a concrete number (industry guidance commonly lands in the 100,000+ range for PBKDF2-SHA256 as of recent recommendations, but worth a quick gut-check against current OWASP guidance before hardcoding it, since recommended minimums do drift upward over time).

**UI status — ✅ confirmed already built**
- PIN Setup Compose screen (6-digit entry/confirm, biometric offer prompt) already exists per the original screen-flow spec. This screen is **pure integration**, no new UI — same category as Screen 04, not Screen 03/05's exceptions.

---

### Screen 15 — PIN Entry

**Relevant solutions:** C6 (verification = successful DB open), C7 (biometric auto-trigger, OS-level gated), C8 (lockout — including the "biometric fully restricted during lockout" decision)

**What this screen needs wired:**

1. **Lockout check on screen load (C8)**
   - Read `lockoutUntil` from secure prefs *before* deciding whether to show the keypad or a countdown
   - **If locked out: biometric prompt must NOT be triggered at all** — this is the decision we locked in, and it's the most important behavioral rule on this screen. The auto-trigger logic needs an explicit guard, not just "try biometric first, fall back to PIN" — that ordering would defeat the lockout.

2. **Biometric auto-trigger (C7)** — only when not locked out
   - On screen load (if not locked out and biometric is enrolled): automatically invoke BiometricPrompt
   - Success → OS releases the wrapped key from Keystore → DB opens → reset `failedAttemptCount`/`lockoutUntil` (C8) → navigate to Home
   - Failure → fall back to PIN keypad, but **the failure still counts toward `failedAttemptCount`** (per the C8 decision)

3. **PIN keypad path**
   - User types PIN → derive key (same algorithm as Setup) → attempt DB open
   - Success → same reset + navigate as biometric success
   - Failure → increment `failedAttemptCount`, check if it hits 5 → if so, set `lockoutUntil` and show countdown UI; otherwise show "Incorrect PIN" per existing screen-flow spec

4. **Forgot PIN → recovery flow**
   - Existing screen-flow spec covers this at a product level (security questions) — technical integration concern: recovery success must also produce the correct key to reopen the DB, which raises a question not yet resolved — **does recovery re-derive the key from a reset PIN (i.e., recovery leads to a forced PIN reset, which is really another rotation), or is there some other recovery mechanism?** This needs answering before Screen 15's recovery path can be specified, separate from the PIN Setup question above.

**Suggested shared ViewModel shape (commonMain):**
```
data class PinEntryUiState(
    val enteredDigits: Int,             // count for dot indicator, not the actual PIN value held longer than needed
    val isLockedOut: Boolean,
    val lockoutSecondsRemaining: Int,
    val showIncorrectPinError: Boolean,
    val biometricPromptShouldTrigger: Boolean   // false whenever isLockedOut is true — the critical guard
)

class PinEntryViewModel(
    private val securityRepository: SecurityRepository,
    private val lockoutRepository: LockoutRepository   // wraps the C8 secure key-value store
) {
    val uiState: StateFlow<PinEntryUiState>
    fun onScreenLoaded()            // checks lockout, decides whether to trigger biometric
    fun onPinDigitEntered(digit: Char)
    fun onBiometricResult(success: Boolean)
    fun onForgotPinTapped()
}
```

**"Forgot PIN" recovery — ✅ resolved (reproducing the original decision from the screen-flow session):**

**Q:** What identity verification method should recovery use?
- Option A: Predefined security question list
- Option B: Custom question
- Option C: Both
**A:** Option A — predefined list, for simplicity and to avoid trivially guessable custom questions.

**Q:** What's the overall recovery mechanism?
- Option B: Security question set during PIN setup, used to verify identity on recovery
- Option C: Reset app only (no identity verification, full data wipe)
**A:** Option B — security question and answer set during PIN setup.

**Locked recovery flow (from the screen-flow doc):**
1. User taps "Forgot PIN" on PIN Entry
2. Security question shown — user enters answer
3. Correct answer → prompts user to **set a brand-new 6-digit PIN**
4. Confirm new PIN — must match (mismatch error state explicitly added as a fix during the loophole-review pass)
5. PIN reset — navigates to Home
6. Wrong answer → "Incorrect answer. Try again or reset app"
7. 5 wrong answers → locked for 30 seconds

**Predefined security questions (5 options, set during PIN Setup):**
1. What was your first pet's name?
2. What city were you born in?
3. What was the name of your first school?
4. What is your mother's maiden name?
5. What was your childhood nickname?

**Technical implication for Screen 15 — confirmed:** my earlier assumption was correct. Step 3 ("set a brand-new PIN") means successful recovery routes back into **Screen 14's setup flow** (or a near-identical sub-flow of it) — which means recovery is functionally another full key rotation (A2/C6), not a separate recovery-specific mechanism. The `securityRepository`'s rotation logic built for Screen 14 gets reused here, not duplicated.

**New integration detail this resolves:** the security question's answer itself needs secure storage (likely hashed, similar treatment to the PIN — stored via the same secure key-value store used for lockout state, C8) so it can be verified without ever being a meaningful secret-recovery mechanism for the encryption key itself (the answer only gates *access to re-do PIN setup*, it never derives the DB key directly).

---

## Phase 3 — Budget & Planning Integration Detail

### Screen 07 — Event Budget

**Relevant solutions:** A1 (schema — Event entity, @ tag target), D10 (budget engine — daily average, color thresholds, UTC storage/local display)

**What this screen needs wired:**

1. **Event list/overview**
   - Read active + past Events from the repository (A1) — likely needs a status field (active/closed) since the PRD treats "active Event" as the @ tag target in Add Expense (Screen 04), implying closed Events exist and stop being taggable
   - 30-character event name limit (per PRD) — validation concern on create/edit, not really a "wiring" task since it's just a max-length constraint on a text field

2. **Per-event budget calculation (D10)**
   - Same engine as Home's overall budget summary, but scoped to **this event's tagged expenses only**, not the whole month — the underlying `calculateBudgetProgress`-style shared function (commonMain) should already be generic enough to take any (expenses, budgetAmount) pair, whether that's "this month" (Home) or "this event" (here). Worth confirming the function signature was built generically in Phase 0/shared logic, not Home-specific.
   - Same three-tier color thresholds apply (0–100% blue / 101–110% yellow / 110%+ red)
   - **Open question this raises:** does "daily average" mean anything for an Event budget, or is that purely a monthly-budget concept? Per the PRD, Event Budget is typically a fixed-amount-for-a-fixed-purpose use case (e.g. a trip, a party) — daily average may not apply here at all. Worth confirming scope: this screen likely only needs *total spent vs. budget* and the color tier, not the daily-average piece of D10.

3. **Linked expenses list**
   - Shows all expenses tagged to this Event (via the A1 polymorphic @ tag) — straightforward repository query filtered by `tagType = EVENT, tagId = thisEvent.id`

**Suggested shared ViewModel shape (commonMain):**
```
data class EventBudgetUiState(
    val event: EventDetails,            // name, dates, budget amount, status (status field is NEW, see below)
    val totalSpent: Money,
    val dailyAverage: Money,            // ✅ confirmed needed — same D10 logic, scoped to event date range not calendar month
    val progressPercent: Float,
    val budgetColor: BudgetColorTier,   // reuses the same enum as Home's D10 implementation
    val linkedExpenses: List<ExpenseSummary>
)

class EventBudgetViewModel(
    private val eventRepository: EventRepository,
    private val expenseRepository: ExpenseRepository
) {
    val uiState: StateFlow<EventBudgetUiState>
    fun onExpenseTapped(expenseId: String)
}
```

**I need from you:** *(resolved)*
- ✅ **Daily average DOES apply to Event Budget** — contrary to my inference, this screen needs the same D10 averaging logic as Home/Reports, scoped to the event's date range rather than a calendar month. This means `EventBudgetUiState` above needs a `dailyAverage: Money` field added, and the shared averaging function needs to support an arbitrary date range (event start→end), not just "this calendar month" — worth confirming the shared D10 function was built generically enough for this, or if it needs a small generalization.
- ✅ **Event status (active/closed) is a NEW field** — needs to be added to the A1 schema now, it didn't exist from earlier sessions. This is a real schema change to track in Phase 0/A1, not just an assumption to confirm.

---

### Screen 08 — Event Detail

**Relevant solutions:** A1 (schema — Event entity read/edit)

**What this screen needs wired:**

1. **Event metadata display/edit** — name (30-char limit), date range, budget amount, status — straightforward CRUD against the Event repository (A1)
2. **Linked expenses** — same data as Screen 07's linked-expenses list, but this is likely the canonical/full view (Screen 07 may show a summary, this shows everything) — worth checking against the screen-flow doc which screen owns "the real list" vs. "a preview"
3. **Delete/close Event** — per the existing screen flow doc, this likely has an edge case around what happens to linked expenses when an Event is deleted (the doc already covers a similar case for Debt Tracker's linked-expense-deletion warning — confirm Event Detail has the analogous rule, or if it needs to be added)

**Suggested shared ViewModel shape (commonMain):**
```
data class EventDetailUiState(
    val event: EventDetails,
    val linkedExpenses: List<ExpenseSummary>,
    val isEditing: Boolean
)

class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val expenseRepository: ExpenseRepository
) {
    val uiState: StateFlow<EventDetailUiState>
    fun onEditSaved(updatedEvent: EventDetails)
    fun onDeleteRequested()       // should surface the linked-expense warning before confirming, if that rule applies here
}
```

**I need from you:** *(resolved)*
- ✅ **Event deletion DOES need the same linked-expense warning as Debt Tracker** — confirms the `onDeleteRequested()` method above should surface a "this will affect N linked expenses" confirmation before proceeding, mirroring the existing Debt Tracker pattern from the screen-flow doc. This is a screen-flow doc update needed (the rule exists for Debt Tracker but needs to be explicitly added for Event Detail too) — worth tracking as a delta to apply there, same category as the Home banner addition from earlier.

---

### Screen 12 — Reports

**Relevant solutions:** D10 (the full budget/average engine — this is likely where most of D10 actually surfaces visually, more than Home)

**What this screen needs wired:**

1. **Monthly daily average (D10's main consumer)**
   - This is almost certainly the screen the original "days elapsed vs. total days" decision was really about — Reports is where a user reviews past months' spending patterns, so the **revised total-days-in-month rule** (now consistent for current and past months) applies directly here
   - UI label wording — still an open question from the brainstorm doc — matters most on this screen specifically, since this is likely the most detail-oriented view of the average figure

2. **Past month browsing**
   - Needs a month-selector/navigation concern — querying the repository for any given month's UTC-stored date range (per D10), converting to local-display before rendering
   - Confirm whether Reports shows category breakdowns, trend charts, or just the average + total — the screen-flow doc should have this spec; worth re-checking it directly since Reports wasn't discussed in as much depth as other screens during this session

3. **Color thresholds** — likely also shown here if Reports has any budget-vs-actual visual (chart/bar), reusing the same `BudgetColorTier` enum as Home and Event Budget

**Suggested shared ViewModel shape (commonMain):**
```
data class ReportsUiState(
    val selectedMonth: YearMonth,
    val totalSpent: Money,
    val dailyAverage: Money,          // total-days-in-month rule, per D10
    val budgetColor: BudgetColorTier?,  // nullable if no budget set for that month
    val categoryBreakdown: List<CategoryTotal>?   // ⏸️ DEFERRED — future addition, not in this phase's integration scope, kept here only as a documented placeholder
)

class ReportsViewModel(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) {
    val uiState: StateFlow<ReportsUiState>
    fun onMonthChanged(month: YearMonth)
}
```

**I need from you:** *(resolved)*
- ✅ **Reports currently shows totals + average only** — no charts or category breakdowns exist yet today. The `categoryBreakdown` field in the ViewModel shape above is **planned but not yet built**, meaning:
  - The totals/average part of Reports is **pure integration** (consistent with most of this plan) — wire the existing UI to the real D10 engine.
  - Charts/breakdowns are a **future addition**, not in scope for this integration pass — flagging this the same way as Screens 03/05's small UI exceptions, except this one is explicitly deferred rather than needed now. Recommend tracking it as a clearly separate post-MVP or later-phase item, not bundled into Phase 3's current integration work, so Phase 3 stays scoped to "make the existing totals/average UI real."
- ✅ **No chart library decided yet** — confirms this is a future decision, not something to resolve now. When it becomes relevant, it'll need its own small technical-solution discussion (similar to how we resolved A2/B4/etc.) — e.g. a KMP-compatible charting approach if charts need to render identically on both platforms eventually, or platform-native charting (Compose's own canvas/charting vs. SwiftUI's `Charts` framework) if visual parity isn't required.

**Revised scope for Screen 12 in this phase:** drop `categoryBreakdown` from the active integration target — keep it in the ViewModel shape as a documented future field, but Phase 3's actual deliverable here is just `selectedMonth`, `totalSpent`, `dailyAverage`, and `budgetColor` wired to real data.

---

## Phase 4 — Social/Shared Features Integration Detail

### Screen 09 — Debt Tracker

**Relevant solutions:** A1 (schema — Debt entity, @ tag target, the $0 validation edge case already locked in the screen-flow doc)

**What this screen needs wired:**

1. **Debt list/overview**
   - Read Debt records from the repository (A1) — per the screen-flow doc, debts have a direction (owed to you / you owe), a person name (30-char limit), and an amount
   - Per the already-locked edge case: $0 amount blocked at input with inline error — this is a ViewModel-level validation rule, not new product logic, just needs wiring into whatever input component already exists

2. **Same-person-both-sides soft warning**
   - Per the existing screen-flow doc: if a person already has a record on the opposite side, show a soft warning ("John already has a record on the other side...") — this is a repository lookup (check for existing records by person name) triggered on save, not a hard block

3. **Linked expenses (A1 @ tag)**
   - Same polymorphic pattern as Event — expenses tagged `tagType = DEBT, tagId = thisDebt.id`
   - Per the existing screen-flow doc: deleting a linked expense from Journal shows a warning that the debt reference will be removed — this is already a locked rule, just needs the actual repository-level cascade/cleanup logic wired in (when an expense is deleted, does the debt record's "linked expense" pointer get nulled, or does deleting the expense get blocked entirely? The screen-flow doc says the expense deletion proceeds and the *reference* is removed — so this is a "null out the link, don't block the deletion" implementation)

**Suggested shared ViewModel shape (commonMain):**
```
data class DebtTrackerUiState(
    val debts: List<DebtSummary>,        // each with direction, person, amount, linked expense count
    val isSaving: Boolean,
    val saveError: String?               // surfaces the $0 validation message
)

class DebtTrackerViewModel(
    private val debtRepository: DebtRepository,
    private val expenseRepository: ExpenseRepository
) {
    val uiState: StateFlow<DebtTrackerUiState>
    fun onSaveDebt(person: String, amount: Money, direction: DebtDirection)
    fun onDebtTapped(debtId: String)
}
```

**I need from you:** *(resolved)*
- ⚠️ **"Direction" was my inference, not a confirmed field** — this needs to actually be defined now, not assumed. Recommend a simple enum: `DebtDirection { OWED_TO_ME, I_OWE }` — but the exact field name, and whether the screen-flow doc's existing UI already implies this distinction visually (e.g. two tabs, a toggle, color-coding), needs to be checked against the real screen rather than assumed further. Flagging as a genuine schema decision still open, not closed by this answer — it just confirms *I* hadn't verified it, not that it's settled.
- ✅ **Linked-expense deletion: confirmed "null the reference, allow deletion freely"** — no warning/confirmation blocks the delete. This actually **changes one of the locked screen-flow rules** — the original doc said deleting a linked expense shows a warning ("This expense is linked to a debt record. Deleting it will remove the reference" — Yes/No). If deletion should now proceed freely with no warning at all, that's a product-level change to the screen-flow doc, not just a technical detail. Worth confirming explicitly: should the warning dialog be removed entirely, or does "delete freely" just mean the deletion itself isn't blocked (the warning could still inform the user, just without stopping them)?

---

### Screen 10 — Shared Costs

**Relevant solutions:** A1 (schema — SharedCost entity, Person list, max-20 constraint), D11 (the split calculator — even/custom modes, rounding remainder logic)

**This is the screen where D11's pure shared function actually gets consumed.** The calculator itself (`calculateSplit`) was specified as a standalone, fully-tested commonMain function with no platform dependencies — this screen's job is purely to feed it inputs and render its outputs, not reimplement any of the math.

**What this screen needs wired:**

1. **People list management**
   - Add/remove people (30-char name limit, per PRD), enforced max of 20 — per the D11 decision, this is enforced at the "add person" step itself (input disabled/hidden past 20), not just a save-time validation
   - Per the D11 decision, switching Even → Custom mode pre-fills custom fields with the current even-split values rather than clearing — this is a real UI-state transition the ViewModel needs to handle explicitly, not just "recompute on mode change"

2. **Split mode toggle (Even / Custom)**
   - Even: call `calculateSplit(total, people, EVEN)` — returns the rounding-remainder-aware shares (first N people get the extra cent, per the locked decision)
   - Custom: user-entered amounts, validated against the total with the locked inline-difference message ("Amounts are $5.00 short of the total")

3. **Save → creates the SharedCost record + likely a linked Expense entry**
   - Per the PRD's use case flow (`Home → Add Expense → Split Summary`, flagged as a loophole back in the original screen-flow session since "Split Summary" wasn't a defined screen), worth confirming: does saving a Shared Cost here create one new Expense (the payer's share or the total?), or does it just record the split without touching the Expense ledger at all? This was flagged as unresolved navigation back in the original session and may still need a clear answer before this screen's save logic can be fully specified.

**Suggested shared ViewModel shape (commonMain):**
```
data class SharedCostUiState(
    val totalAmount: Money,
    val people: List<Person>,           // max 20, enforced at add-time
    val splitMode: SplitMode,           // EVEN or CUSTOM
    val shares: List<PersonShare>,      // output of calculateSplit — drives the displayed amount per person
    val customAmountError: String?      // "Amounts are $X short/over the total"
)

class SharedCostViewModel(
    private val sharedCostRepository: SharedCostRepository,
    private val expenseRepository: ExpenseRepository    // pending clarification on save behavior, see above
) {
    val uiState: StateFlow<SharedCostUiState>
    fun onPersonAdded(name: String)
    fun onPersonRemoved(personId: String)
    fun onSplitModeChanged(mode: SplitMode)     // triggers the pre-fill behavior per D11
    fun onCustomAmountChanged(personId: String, amount: Money)
    fun onSave()
}
```

**I need from you:** *(resolved)*
- ✅ **"Split Summary" is confirmed as a separate screen** — not the same as Shared Costs itself. This is a real gap: **Split Summary is now a 16th screen** that doesn't exist yet in the 15-screen flow doc, needs its own definition (purpose, layout, navigation entry/exit points) before it can be integrated. This should be tracked as a new addition to the screen-flow doc, not just a detail-design footnote — flagging it the same way as the Home banner and Event-deletion-warning deltas earlier.
- ✅ **Shared Cost has NO effect on the Expense ledger — it's a completely separate data store.** This is an important architectural clarification: `SharedCostRepository` and `ExpenseRepository` are fully independent in A1's schema — no foreign key, no shared total, no appearance in Reports/Home's budget calculations. The `expenseRepository` dependency I'd drafted into `SharedCostViewModel` above is now confirmed unnecessary and should be removed.

**Revised ViewModel (removing the now-unnecessary expense dependency):**
```
class SharedCostViewModel(
    private val sharedCostRepository: SharedCostRepository
    // expenseRepository removed — Shared Cost is a fully separate store, no ledger interaction
) {
    val uiState: StateFlow<SharedCostUiState>
    fun onPersonAdded(name: String)
    fun onPersonRemoved(personId: String)
    fun onSplitModeChanged(mode: SplitMode)
    fun onCustomAmountChanged(personId: String, amount: Money)
    fun onSave()    // writes only to SharedCostRepository, navigates to the new Split Summary screen
}
```

**Open follow-on:** since Shared Cost data is separate and invisible to Reports/Home, what's its actual long-term value to the user — is it purely a "record what was split, for reference" feature, with no budgeting tie-in at all? Worth a quick gut-check that this matches the original product intent from the PRD session, since it changes what "Shared Costs" actually accomplishes for the user (bookkeeping reference only, not contributing to their tracked spending).

---

## Phase 5 — Data Portability & Settings Integration Detail

### Screen 13 — Settings

**Relevant solutions:** B4/B5 (export/import — password-protected ZIP, signature, manifest), C6/C7 (PIN change/disable, biometric toggle), F16 (no-migration messaging), E14 (language/currency selectors)

**This screen has the heaviest cross-reference of any single screen in the app** — it's the UI surface for nearly every security and data-portability decision made across the whole brainstorm doc.

**What this screen needs wired:**

1. **Export (B4)**
   - Trigger: user taps Export → prompted for a **separate per-export password** (not the app PIN, per the locked decision)
   - Behind the scenes: generate `manifest.json`, compute the HMAC-SHA256 signature over manifest + all CSVs, package into a password-protected ZIP (AES) — this is where the **still-open AES-ZIP-on-iOS library question** would matter eventually, but for Android-only MVP, Zip4j (or equivalent JVM-compatible library) is viable now
   - File handoff: Android SAF / share sheet for the user to save the resulting `.peexport` file — a platform-specific (androidMain) concern, not shared logic

2. **Import (B5)**
   - Trigger: user picks a file → prompted for the export's password → decrypt → verify signature → check schema version → parse CSVs → show import summary (counts per feature) → user confirms → write to DB
   - Failure handling: generic "Incorrect password or corrupted file" message (per the locked decision, doesn't leak which failure occurred) for password/signature failures; a separate, more specific message for schema-version incompatibility (newer export than current app understands) — **this case is still an open question from the brainstorm doc and needs a real answer before Import can be fully specified**

3. **PIN management**
   - **Change PIN:** re-derive + rekey (same operation as PIN Setup, Screen 14) — likely just routes back into that flow
   - **Disable PIN:** per the A2/C6 decision, this means a **reverse rotation** — generate a fresh random key, rekey to it, store it unwrapped (no biometric gate) in Keystore. This is a real operation, not just "turn off a flag."
   - **Biometric toggle:** enable = wrap the current key in Keystore with `setUserAuthenticationRequired(true)` (re-wrap operation, raw key must be in memory briefly); disable = remove the biometric-gated wrapping, fall back to PIN-only unlock

4. **Clear All Data**
   - Per the existing PRD rule: disables PIN. In light of the A2/C6 rotation model, this specifically means: wipe all entity tables, **and** perform the reverse rotation (back to a fresh random key) since PIN is now disabled as a side effect — this connects Clear-All-Data directly to the PIN-disable operation above, they're not independent
   - Selective clears (e.g. just Journal) preserve PIN, per existing PRD rule — no key rotation involved, just table-level deletes

5. **Localization (E14)**
   - Language selector: swaps the active locale, native formatters handle date/number re-rendering
   - Currency selector: single default currency for MVP (no multi-currency per entry yet) — just a stored setting + native currency formatter

6. **No-migration messaging (F16)**
   - Some explicit notice ("Note: previous app data isn't automatically transferred") — still needs the actual communication plan/copy decided (open question from the brainstorm doc), but the Settings screen is likely one place this should appear, alongside release notes

**Suggested shared ViewModel shape (commonMain) — split into sub-concerns given the screen's size:**
```
data class SettingsUiState(
    val language: Locale,
    val currency: CurrencyCode,
    val isPinEnabled: Boolean,
    val isBiometricEnabled: Boolean,
    val exportInProgress: Boolean,
    val importInProgress: Boolean,
    val importSummary: ImportSummary?    // counts shown before final confirm
)

class SettingsViewModel(
    private val securityRepository: SecurityRepository,   // PIN/biometric/rotation, shared with Screens 14/15
    private val exportImportRepository: ExportImportRepository,  // B4/B5
    private val localeRepository: LocaleRepository,        // E14
    private val dataRepository: DataRepository              // Clear All Data / selective clears
) {
    val uiState: StateFlow<SettingsUiState>
    fun onExportRequested(password: String)
    fun onImportFileSelected(filePath: String, password: String)
    fun onImportConfirmed()
    fun onChangePinRequested()       // routes to Screen 14
    fun onDisablePinRequested()      // triggers reverse rotation
    fun onBiometricToggled(enabled: Boolean)
    fun onClearAllDataRequested()    // wipe + reverse rotation
    fun onSelectiveClearRequested(feature: String)
    fun onLanguageChanged(locale: Locale)
    fun onCurrencyChanged(currency: CurrencyCode)
}
```

**I need from you:** *(resolved)*
- ✅ **Settings Compose screen is already built** — same category as Screens 04, 14, 15: pure integration, no new UI needed here.
- ✅ **Import schema-version mismatch: block with an "update the app" message.** This resolves a real open gap — when `manifest.json`'s schema version is newer than the current app understands, Import should refuse to proceed and show a clear message (e.g. "This file was exported from a newer version of Pro Expense. Please update the app to import it.") rather than attempting any partial/best-effort import. This is the safer, simpler choice — partial imports of unknown-schema data risk silent data corruption, which would be a serious trust issue for a finance app.
- ⏸️ **F16 no-migration messaging copy: deferred to later** — confirmed as a separate task, not part of this integration pass. The Settings screen still needs *a slot* for this message once the copy exists, but the actual wording isn't being drafted now.

**Updated import failure handling (full picture):**
| Failure type | User-facing message |
|---|---|
| Wrong password / corrupted file | "Incorrect password or corrupted file" (generic, doesn't distinguish — per the locked B5 decision) |
| Signature mismatch (tampered) | Same generic message as above — signature failure is folded into "corrupted file" rather than its own message, consistent with not revealing *why* something failed |
| Schema version newer than app supports | ✅ **NEW — distinct message:** "This file was exported from a newer version of Pro Expense. Please update the app to import it." — this is the one case that gets its own specific message, since "update the app" is actionable guidance, unlike the security-sensitive failures above which intentionally stay vague |

---

## Phase 6 — Android Closeout + iOS-Readiness Check (Detail)

Unlike Phases 1-5, this isn't screen integration — it's verification that everything built across Phases 0-5 actually holds together as one coherent app, and that the iOS-readiness discipline (stated up front) was actually followed rather than just asserted.

### 6.1 — Full Regression Pass (Android, real data)

A walk through the entire app with the encrypted DB doing real work end-to-end, not mocked:

- [ ] Fresh install → random key encrypts DB from first write (A2) → log expenses via Phase 1 screens → confirm Home/Journal/Reports all reflect real data
- [ ] Set a PIN (Screen 14) → confirm `PRAGMA rekey` succeeds → confirm biometric wrap (if enrolled) → kill app mid-flow once deliberately to sanity-check the still-open rekey atomicity question (C6/A2) before shipping
- [ ] PIN Entry (Screen 15): trigger 5 wrong PIN attempts → confirm lockout fires → confirm biometric is fully blocked during the 30s window (not just PIN typing) — this is the specific loophole we closed earlier, worth a deliberate test, not just a code read-through
- [ ] Forgot PIN recovery: trigger it, answer the security question correctly, confirm it routes into a full PIN re-setup (i.e., another rotation), not some separate path
- [ ] Event Budget (Screen 07): confirm the per-event daily average uses the event's own date range, not calendar month — this was a late correction to my own draft, worth explicit verification
- [ ] Shared Costs (Screen 10): confirm split totals reconcile exactly (no off-by-one-cent display bug) using the locked remainder-distribution rule; confirm Shared Cost data does NOT appear anywhere in Reports/Home totals (per the "separate store" decision)
- [ ] Debt Tracker (Screen 09): delete an expense linked to a debt → confirm it deletes freely with the reference nulled, consistent with the "delete freely" decision (pending the still-open question on whether the warning dialog itself should be removed or just stop blocking)
- [ ] Settings (Screen 13): full export → full import on a fresh install → confirm round-trip data integrity; attempt import with wrong password (generic error), then with a deliberately corrupted file (same generic error), then simulate a future schema version (should show the distinct "update the app" message)
- [ ] Clear All Data: confirm it both wipes tables AND performs the reverse key rotation (disabling PIN as a side effect), not just one or the other

### 6.2 — iOS-Readiness Audit

Per the principles stated at the top of this document — go back through Phases 0-5's actual implementation (not just this design plan) and check:

- [ ] No business logic landed in `androidMain` that should be in `commonMain` — spot-check the ViewModels for Screens 03-15 specifically, since these were drafted before any real code existed
- [ ] Every `expect/actual` seam (Keystore wrapping, BiometricPrompt, SAF file access) has its `expect` interface cleanly defined in commonMain, even though only the Android `actual` exists right now
- [ ] No ViewModel exposes Android-specific types (`Context`, `Resources`, etc.) in its public `StateFlow`/state classes
- [ ] The `iosMain` empty source set from Phase 0 still exists in the module structure (hasn't been accidentally removed/never created if Phase 0 was rushed)
- [ ] Repository pattern held throughout — confirm no screen/ViewModel ended up calling SQLDelight queries directly, bypassing the repository layer, anywhere in Phases 1-5

### 6.3 — Outstanding Items Before This Can Be Called "Done"

Pulled together from everything still open across the whole plan — **these are real blockers or decisions, not just nice-to-haves**, and should be resolved before Phase 6 sign-off, not after:

| Item | From | Status |
|---|---|---|
| Remainder-distribution order in Shared Costs (random vs. fixed first-N) | D11 | Still open |
| Amount-search boundary style (substring vs. precise) | D12 | Still open |
| Rekey atomicity/crash-safety confirmed on Android specifically | A2/C6 | Needs explicit testing in 6.1, not just assumed |
| UI label wording for current-month average | D10 | Still open |
| Debt Tracker "direction" field — name/enum, confirm against real schema | Phase 4 | Still open |
| Debt Tracker linked-expense deletion — remove warning dialog entirely, or keep informing without blocking? | Phase 4 | Still open |
| Split Summary — the new 16th screen — needs definition | Phase 4 | **Blocking** — Shared Costs (Screen 10) integration can't fully complete without this screen existing |
| F16 no-migration messaging copy | Phase 5 | Deferred (acceptable to ship without, per your decision) |

### 6.4 — F18 Scope Confirmation

Per the original F18 decision (Android-only CI for now) and this session's clarification (iOS UI is deliberately a later phase, not paused indefinitely) — Phase 6's CI hardening work is scoped to **Android only**. No iOS build pipeline work happens in this phase; that begins whenever the iOS UI phase is scheduled, starting with the E13 interop spike as its first task (per the iOS-Readiness Principles).

---

## Open Questions Carried Forward
*(From the brainstorm doc — still unresolved, relevant to integration)*
- ~~Argon2 vs. PBKDF2 for PIN → key derivation~~ → ✅ resolved: PBKDF2 (Phase 2)
- Remainder-distribution order in Shared Costs splits (D11)
- Amount-search boundary style — substring vs. precise (D12)
- F16 communication plan for existing Play Store users (no migration) — ✅ decision made: defer the actual copy/messaging to a later task; Settings (Phase 5) just needs a slot reserved for it once drafted
- Rekey atomicity/crash-safety on SQLCipher (A2/C6)
- UI label wording for the current-month average (D10)
- **Does the existing Home UI already include a banner/notice component** that the PIN-setup nudge (from the A2/C6 decision) can reuse, or does that require adding new UI — the one place this "integration-only" plan might need actual UI work?
- Is there an existing component library reference (Figma file, Storybook-equivalent, or component documentation) we should pull from when confirming E15's theme token reconciliation, or should we inspect the Compose source directly?
- Debt Tracker's "direction" field — name, enum values, and whether existing UI already implies this — still needs explicit confirmation, not just inference (Phase 4)
- Does Shared Costs being a fully separate, ledger-invisible data store match the original product intent, or should it eventually tie into Reports/Home's totals? (Phase 4)

## Screen-Flow Doc Deltas to Apply
*(Product-level changes/additions surfaced during this integration pass — these affect `finance_tracker_screen_flows.md`, not just this design plan, and should be applied there once confirmed)*
- **Home (Screen 03):** add PIN-setup dismissible banner — new UI, not previously specified
- **Journal (Screen 05):** add date-grouped section header behavior — grouping logic/UI didn't exist yet
- **Event Detail (Screen 08):** add the same linked-expense deletion warning pattern Debt Tracker already has
- **Debt Tracker (Screen 09):** clarify whether the existing "linked expense deletion" warning dialog should be removed entirely (deletion now proceeds freely with no block) or merely stop blocking while still informing the user
- **NEW SCREEN — Split Summary:** confirmed as a real, separate screen (not the same as Shared Costs) — referenced in the original use case flow (`Home → Add Expense → Split Summary`) but never defined. This is a genuine 16th screen needing its own purpose/layout/navigation spec before Phase 4's Shared Costs integration can be fully completed.
