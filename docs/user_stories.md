# Finance Tracker — User Stories

> Agile user stories for every service (feature module) and screen of the Finance Tracker MVP
> (Android ships as **Pro Expense**). Derived from the PRD
> ([`finance_tracker_product.md`](finance_tracker_product.md)) and the user journey captured in the
> screen specs ([`../design-system-spec/screens/`](../design-system-spec/screens/)).
>
> This document fulfils Stage 3 → *"Write user stories for all MVP use cases"* in the PRD roadmap.

---

## How to read this document

**Story format**

> **As a** \<persona>, **I want** \<goal>, **so that** \<benefit>.

Each story has a stable **ID** (`US-<SERVICE>-<n>`), a **priority**, and **acceptance criteria** (AC)
grounded in the screen behaviors and edge cases. ACs are written to be testable.

**Priority** (from PRD Feature List):

| Tag | Meaning |
|---|---|
| 🔴 Must | MVP-blocking — core value |
| 🟡 Should | High value, ships if capacity allows |
| 🔵 Phase 2 | Post-MVP per roadmap (Journal, Event Budget, Debt, Localization, Biometric) |

**Personas** (PRD): 🎓 Maya (Student) · 🏠 Siti (Housekeeper) · ✈️ Carlos (Traveler) ·
👫 Aiko (Cost Sharer) · 💼 Raj (Freelancer) · 🧳 Sophie (Expat) · 🎉 James (Event Organizer) ·
👴 Mr. Chen (Retiree) · 🛒 Amara (Vendor). "Any user" is used when the story is persona-agnostic.

---

## Service → Screen → Use Case map

| Service (`feature:*`) | Screens | PRD use case |
|---|---|---|
| **onboarding** | 01 Splash · 02 Onboarding · 02P Profile Setup | First-launch / setup |
| **logging** | 04 Add Expense | Quick Manual Logging |
| **history** | 05 Journal · 06 Journal Detail | Record History / Financial Journal |
| **currency** | 02P Currency picker · 13 More → Currency | Multi-Currency |
| **eventbudget** | 07 Event Budget · 08 Event Detail | Event Budget |
| **debt** | 09 Debt Tracker | Debt & Lending Tracker |
| **sharedcost** | 10 Shared Costs | Shared Costs |
| **categories** | 11 Category List | Category Management |
| **reports** | 12 Reports | Record History → Summary |
| **auth** | 14 PIN Setup · 15 PIN Entry | Auth Setup (PIN) |
| **importexport** | 13 More → Data export | Secure Import & Export |
| **app shell / settings** | 03 Home · 13 More | Central hub / Foundation |

---

## 1. Onboarding & First Launch — `feature:onboarding`

**Screens:** 01 Splash · 02 Onboarding · 02P Profile Setup

### US-ONB-1 — Discover what the app does · 🔴 Must
> **As a** first-time user, **I want** a short swipeable intro to the app's features, **so that** I
> understand the value before committing.

- AC1: On first launch (after Splash), a horizontally swipeable carousel shows Welcome → Quick Log → Shared Costs → Event Budget → Journal.
- AC2: A page-dot indicator tracks position; the active dot widens.
- AC3: There is **no** use-case selection — features are presented, not chosen.
- AC4: `Back` appears from slide 2 onward; `Next` is hidden on the last slide.

### US-ONB-2 — Skip the intro · 🟡 Should
> **As an** impatient user, **I want** to skip onboarding, **so that** I can start tracking immediately.

- AC1: `Skip` (top-right) is present on every slide except the last.
- AC2: `Skip` jumps straight to Profile Setup, then Home.
- AC3: The bottom-anchored `Get started` CTA is present on **every** slide, so the user can start from anywhere.

### US-ONB-3 — Personalize my profile · 🟡 Should
> **As** Maya 🎓, **I want** to enter my name during setup, **so that** the app greets me and labels my exports.

- AC1: Name personalizes the Home greeting ("Hi, Maya") and CSV exports.
- AC2: Name is **optional**; the field is pre-focused and the primary action is always enabled.
- AC3: The identity preview card updates live as the name is typed.

### US-ONB-4 — Choose my home currency at setup · 🔴 Must
> **As** Carlos ✈️, **I want** to pick my home currency during setup, **so that** every entry uses the right currency from day one.

- AC1: A 2×2 quick grid offers the four most common currencies (USD default, selected).
- AC2: `More currencies` opens a searchable bottom sheet; selecting a row applies and closes in one tap.
- AC3: The identity card's "Tracking in … · CODE" line updates on selection.
- AC4: `Start tracking` completes setup and lands on Home.
- AC5: The chosen currency **persists** across relaunch (regression guard — see fix for currency persistence).

### US-ONB-5 — Be routed correctly on every launch · 🔴 Must
> **As** any returning user, **I want** the splash to send me to the right place, **so that** I don't navigate manually.

- AC1: Splash displays ~1.5–2s with no interaction (logo + wordmark only).
- AC2: Routing on dismiss: first launch → Onboarding; returning + PIN on → PIN Entry; returning + PIN off → Home.
- AC3: If an unfinished Add-Expense draft exists, the restore prompt shows **before** PIN Entry (no auth required to restore).

---

## 2. Quick Manual Logging — `feature:logging`

**Screen:** 04 Add Expense · **PRD:** Quick Manual Logging (🔴 core differentiator — "log in under 5s")

### US-LOG-1 — Log an expense in seconds · 🔴 Must
> **As** Amara 🛒, **I want** to log an amount and save in one move, **so that** recording cash flow takes seconds.

- AC1: Tapping `+` opens Add Expense with the numeric keypad already open and "Food" pre-selected.
- AC2: `Save` (quick-commit) stores the entry with the default category, slides back to Home, and fires a success toast.
- AC3: The amount is large and centered; entry requires no scrolling.

### US-LOG-2 — Be stopped from saving an empty amount · 🔴 Must
> **As** any user, **I want** the app to block a $0 entry, **so that** I don't create meaningless records.

- AC1: `Save` and `Next` are disabled until amount > $0 (`canProceed = value > 0`).
- AC2: Tapping a disabled action shakes the field (±4dp) and shows "Amount must be greater than $0".
- AC3: Input rules enforced: whole part ≤ 7 digits, fraction ≤ 2, single decimal, leading zeros stripped (except "0."), commas grouped live.

### US-LOG-3 — Add context to an expense · 🔴 Must
> **As** Siti 🏠, **I want** to set category, date, and a note, **so that** my records are meaningful later.

- AC1: `Next` opens Details with the amount read-only at top (tap to return and edit; value persists).
- AC2: Category is required; note is optional (≤ 200 chars).
- AC3: Date defaults to today and is editable via a date/time picker (past **and** future allowed).
- AC4: Saving from Details returns to Home with a success toast.

### US-LOG-4 — Backdate an expense · 🟡 Should
> **As** Maya 🎓, **I want** to set a past date on an expense I forgot to log, **so that** my history is accurate.

- AC1: The date field opens a date + time picker pre-filled with the entry's current timestamp.
- AC2: Choosing a past date stores that timestamp; the entry then groups under that day in Journal/Home, not today.

### US-LOG-5 — Link an expense to an event or debt · 🔵 Phase 2
> **As** Carlos ✈️, **I want** to tag an expense to an active event or debt, **so that** balances update automatically.

- AC1: The `@` tag field is hidden when there are no active events or debts; otherwise it is optional.
- AC2: Only **one** link is allowed (Event **OR** Debt). Picking an Event greys out and disables the Debts group, and vice-versa.
- AC3: `Clear` resets both groups.

### US-LOG-6 — Avoid runaway notes · 🟡 Should
> **As** any user, **I want** a clear limit on note length, **so that** the field stays manageable.

- AC1: Note is hard-capped at 200 chars; the counter turns to the error color at the limit.
- AC2: Input beyond 200 chars is ignored.

### US-LOG-7 — Never lose a half-typed entry · 🟡 Should
> **As** any user, **I want** my in-progress entry restored after a crash, **so that** I don't retype it.

- AC1: If the app force-closes mid-entry, the draft is auto-saved.
- AC2: On relaunch, before PIN, a `Continue / Discard` prompt is shown (no auth required).
- AC3: Back from Amount with no value navigates away silently — no save, no prompt.

---

## 3. Record History & Journal — `feature:history`

**Screens:** 05 Journal · 06 Journal Detail · **PRD:** Record History (🔴) / Financial Journal (🔵)

### US-HIS-1 — Review spending by day · 🔴 Must
> **As** Siti 🏠, **I want** my entries grouped by day with daily totals, **so that** I can review like a notebook.

- AC1: Entries are grouped by expense date; days are always expanded (no collapsing).
- AC2: Within a day, newest-created appears first.
- AC3: Each day header shows the date plus the mono daily total.

### US-HIS-2 — Find a past entry · 🔴 Must
> **As** any user, **I want** to search by keyword, note, or amount, **so that** I can find a specific entry fast.

- AC1: A search field plus category filter chips sit at the top.
- AC2: When search is active, the list flattens (no day grouping); rows show amount, category, date, note.
- AC3: No matches → centered "No matches" illustration that echoes the query and suggests a different keyword/amount/note.

### US-HIS-3 — Filter by category · 🔴 Must
> **As** Maya 🎓, **I want** to filter the list to one category, **so that** I can see e.g. only food spending.

- AC1: Filter chips mirror the category catalogue; "All" is the default.
- AC2: Selecting a chip narrows the list to that category.

### US-HIS-4 — Jot a quick note without leaving the list · 🔵 Phase 2
> **As** Siti 🏠, **I want** to long-press an entry and add a note inline, **so that** I can annotate quickly.

- AC1: Long-press opens a Quick-note bottom sheet pinned to that entry.
- AC2: `Save` writes the note and dismisses without navigating away.

### US-HIS-5 — Open an entry's full detail · 🔴 Must
> **As** any user, **I want** to tap an entry to see everything about it, **so that** I can review or act on it.

- AC1: Tapping an entry opens Journal Detail showing amount (large), category icon + label, date & time, note, and any `@` tag link.
- AC2: From Home, tapping a recent transaction opens that same record's detail (consistent with Journal).
- AC3: `Back` returns to the list.

### US-HIS-6 — Edit a past entry · 🔴 Must
> **As** any user, **I want** to edit a logged entry, **so that** I can correct mistakes.

- AC1: The detail's action sheet offers Edit · Delete · Cancel.
- AC2: `Edit` opens Add Expense (Details) pre-filled with the record's values (amount, category, date/time, note, tag).
- AC3: Saving updates the **same** record (no duplicate) and returns to Journal; a changed date regroups it.

### US-HIS-7 — Delete a past entry safely · 🔴 Must
> **As** any user, **I want** a confirmation before deleting, **so that** I don't lose data by accident.

- AC1: `Delete` shows a confirmation dialog first, then removes the entry and returns to Journal.
- AC2: If the entry was tagged, the linked Event/Debt recalculates immediately after delete or edit.

---

## 4. Multi-Currency — `feature:currency`

**Screens:** 02P Currency picker · 13 More → Currency · **PRD:** Multi-Currency (🔴 core, not an add-on)

### US-CUR-1 — Set a default home currency · 🔴 Must
> **As** Sophie 🧳, **I want** to set my home currency, **so that** all entries use a single consistent currency (single-currency MVP).

- AC1: The home currency applies to every entry.
- AC2: It is selectable at setup (US-ONB-4) and from More → Currency later.

### US-CUR-2 — Change my currency later · 🔴 Must
> **As** Sophie 🧳, **I want** to change the default currency in settings, **so that** I can adjust after a move.

- AC1: More → Currency offers a selector for common currencies; the chosen one becomes the default applied to all entries.
- AC2: The change persists across relaunch.

### US-CUR-3 — Search for a less-common currency · 🟡 Should
> **As** Carlos ✈️, **I want** to search the currency list, **so that** I can quickly find a specific currency.

- AC1: The "More currencies" sheet is searchable; selecting a row applies and closes in one tap.

> **Planned (post-MVP, per PRD MC section):** per-record currency, manual exchange rate per entry, and
> "original + converted" display. Tracked here for traceability; **not** in MVP scope.

---

## 5. Event Budget — `feature:eventbudget`

**Screens:** 07 Event Budget · 08 Event Detail · **PRD:** Event Budget (🔵 Phase 2)

### US-EVT-1 — Create an event budget · 🔵 Phase 2
> **As** James 🎉, **I want** to create an event with a budget and date range, **so that** I can track spend for a trip or party.

- AC1: Create sheet fields: name (req, ≤ 30), start date (today default, past allowed), end date (≥ start), total budget (req, > $0).
- AC2: `Save` is disabled until budget > $0 ("Budget must be greater than $0").
- AC3: Duplicate names are blocked inline ("An event with this name already exists."); name counter caps at 30.
- AC4: Empty state shows a jar illustration, "No active events…", and a single `Create event` CTA.

### US-EVT-2 — Track multiple events at once · 🔵 Phase 2
> **As** James 🎉, **I want** several events active simultaneously, **so that** I can run e.g. a trip and a wedding in parallel.

- AC1: Multiple events can be active at once.
- AC2: Each card shows name, date range, live remaining balance, and a mini progress bar.
- AC3: With overlapping active events, the Home header shows the most recently created one.

### US-EVT-3 — See when I'm over budget · 🔵 Phase 2
> **As** Carlos ✈️, **I want** clear over-budget signals, **so that** I can rein in spending.

- AC1: Progress color system: 0–100% soft blue (on track); 101–110% amber + "Over budget by $X (Y%)"; >110% soft red + bold warning.
- AC2: Over-budget cards show the bar in red and an "Over budget" chip; remaining flips negative.

### US-EVT-4 — Drill into an event · 🔵 Phase 2
> **As** James 🎉, **I want** an event detail with all linked expenses, **so that** I can see where the money went.

- AC1: Header shows name, date range, budget summary (total / spent / remaining), and progress bar.
- AC2: A linked expense list shows all `@`-tagged entries, plus an Add-expense shortcut that pre-tags this event.
- AC3: Deleting a linked expense from Journal recalculates remaining immediately.

### US-EVT-5 — Edit and close an event · 🔵 Phase 2
> **As** James 🎉, **I want** to edit and manually close an event, **so that** I control its lifecycle.

- AC1: Edit sheet allows name, dates, and budget (budget must stay > $0).
- AC2: End date passing does **not** auto-close — close is manual; end date is reference only.
- AC3: Lifecycle: Active (fully editable/linkable) → Closed < 24h (archived but editable, grace period) → Closed > 24h (read-only: fields locked, no edits, no new links).

---

## 6. Debt & Lending Tracker — `feature:debt`

**Screen:** 09 Debt Tracker · **PRD:** Debt & Lending (🔵 Phase 2)

### US-DEBT-1 — Switch between lent and owed · 🔵 Phase 2
> **As** Maya 🎓, **I want** to toggle "I Lent" / "I Owe", **so that** I can see each side separately.

- AC1: The toggle switches the list; `+` opens Add Record pre-set to the current side.
- AC2: Active records are on top (colored by type); Settled records are below (greyed).

### US-DEBT-2 — Record money lent or owed · 🔵 Phase 2
> **As** Raj 💼, **I want** to record a debt with person and amount, **so that** I remember who owes what.

- AC1: Fields: person (req, ≤ 30), amount (req, > $0), date (today default), optional due date (reference only — no reminders in MVP), optional note (≤ 200), optional `@`-linked expense.
- AC2: A detail view shows person, amount, dates, status, note, and any linked expense reference.

### US-DEBT-3 — Settle and clean up debts · 🔵 Phase 2
> **As** Maya 🎓, **I want** to mark a debt settled and later delete it, **so that** my list stays current.

- AC1: Active records offer Edit & Mark-as-settled; they are **not** deletable (settle first).
- AC2: Settled records offer Delete (with a confirm dialog); deleting keeps any linked expense, removing only the debt link.

### US-DEBT-4 — Be warned about conflicting records · 🔵 Phase 2
> **As** any user, **I want** a warning when a person exists on the opposite side, **so that** I don't double-record.

- AC1: Adding a person already on the other side shows a soft warning: "John already has a record on the other side. Continue?"
- AC2: `Yes` proceeds, `No` dismisses.

---

## 7. Shared Costs — `feature:sharedcost`

**Screen:** 10 Shared Costs · **PRD:** Shared Costs (🔴 MVP)

### US-SHC-1 — Split a bill equally · 🔴 Must
> **As** Aiko 👫, **I want** to split a total equally among people, **so that** everyone's share is clear.

- AC1: Enter total (large), set people count via stepper (min 2, max 20), optionally name people (default "Person 1…").
- AC2: Equal split is the default; the summary sub-screen shows per-person amounts.
- AC3: The keypad stays available while typing a multi-digit total (regression guard — keypad must not disappear after one digit).

### US-SHC-2 — Split a bill unequally · 🟡 Should
> **As** Aiko 👫, **I want** a custom split, **so that** I can reflect uneven shares.

- AC1: Custom mode lets each share be edited live (including $0).
- AC2: `Back` from the summary persists all values.

### US-SHC-3 — Stay within sane participant limits · 🟡 Should
> **As** any user, **I want** sensible min/max on people, **so that** the split stays usable.

- AC1: At count = 20 the `+` button is disabled and greyed (no error); at min 2 the `−` is disabled.
- AC2: Total $0 → `Save` disabled + "Total amount must be greater than $0."

### US-SHC-4 — Keep shared costs out of my personal journal · 🔴 Must
> **As** Aiko 👫, **I want** the saved total recorded as one expense (splits as reference), **so that** my journal isn't polluted by per-person rows.

- AC1: `Save` always stores the original **total** as the expense; splits are reference only (total is the source of truth).
- AC2: Saved splits appear in Shared Costs **history only** — not in Journal.

### US-SHC-5 — Review and remove past splits · 🟡 Should
> **As** Aiko 👫, **I want** a history of past splits I can delete, **so that** I can tidy old records.

- AC1: History rows tap to view the full split; swipe-left deletes (with confirm).
- AC2: Editing is not supported (reference only).

---

## 8. Category Management — `feature:categories`

**Screen:** 11 Category List · **PRD:** Category Management (🔴 Must)

### US-CAT-1 — Use sensible default categories · 🔴 Must
> **As** any user, **I want** ready-made categories, **so that** I can log without setup.

- AC1: Defaults (Food, Transport, Shopping, Bills, Health, Entertainment) are locked and always first.
- AC2: Deleting all custom categories still leaves defaults visible — no empty state.

### US-CAT-2 — Create my own category · 🔴 Must
> **As** Raj 💼, **I want** to add a custom category with an icon and color, **so that** it fits my spending.

- AC1: Add/edit via icon picker + color picker; name ≤ 20 chars with counter; duplicates blocked ("A category with this name already exists.").
- AC2: `Add` is disabled until valid.
- AC3: Custom categories follow defaults and are drag-to-reorder; their order mirrors the chip order in Add Expense.

### US-CAT-3 — Edit or delete a custom category · 🔴 Must
> **As** any user, **I want** to manage custom categories, **so that** I can keep them relevant.

- AC1: Edit / Delete is available via a bottom sheet (custom only).
- AC2: Deleting a category moves its expenses to **Uncategorized**.
- AC3: Uncategorized is a system category: not selectable when logging, but shown in Journal & Reports for reference.

---

## 9. Reports — `feature:reports`

**Screen:** 12 Reports · **PRD:** Record History → Summary View (🟡 Should)

### US-REP-1 — See where my money went this month · 🟡 Should
> **As** Maya 🎓, **I want** a monthly breakdown by category, **so that** I understand my spending.

- AC1: Total spent is the large headline number for the selected month.
- AC2: A donut chart breaks spending down by category, with a ranked Top-categories list (name + amount) below.
- AC3: Daily average = current month: total ÷ days elapsed; past month: total ÷ days in month.

### US-REP-2 — Move between months · 🟡 Should
> **As** any user, **I want** to switch reporting periods easily, **so that** I can compare months.

- AC1: A period selector is at the top (monthly only in MVP).
- AC2: The period can be changed by swiping left/right **and** by the selector controls (both must work).

### US-REP-3 — Get useful empty/edge behavior · 🟡 Should
> **As** a new user, **I want** the report to behave gracefully with little data, **so that** it's never confusing.

- AC1: No data ever → empty state "No data yet…".
- AC2: No data this month → auto-switches to the last month with data (no empty state).
- AC3: If every expense is uncategorized → one full grey donut segment + tip "categorize your expenses for better insights." Uncategorized appears only when such expenses exist.

---

## 10. Auth & Security — `feature:auth`

**Screens:** 14 PIN Setup · 15 PIN Entry · **PRD:** Auth Setup (🟡 Should; Biometric 🔵 Phase 2)

### US-AUTH-1 — Protect the app with a PIN · 🟡 Should
> **As** Siti 🏠, **I want** to set a 6-digit PIN, **so that** my financial data is private on a shared device.

- AC1: Toggle to enable → enter 6-digit PIN → confirm (must match).
- AC2: Mismatch → dots clear and shake (±4dp), "PINs do not match. Try again.", original PIN preserved.
- AC3: Success message: "PIN is now active. You'll be asked to enter it on your next launch."

### US-AUTH-2 — See my digits while creating a PIN · 🟡 Should
> **As** Mr. Chen 👴, **I want** to reveal the digits while creating my PIN, **so that** I can confirm I typed it correctly.

- AC1: During PIN **creation** (set & confirm), an eye toggle reveals/hides the entered digits.
- AC2: Hidden is the default; revealing shows the typed digits in place of the dots.
- AC3: The reveal toggle is scoped to creation only — **not** the unlock/lock screen.

### US-AUTH-3 — Set a required recovery question · 🟡 Should
> **As** any user, **I want** to set a security question when enabling PIN, **so that** I can recover access if I forget it.

- AC1: A security question is **required**: pick from a predefined list + answer; PIN cannot be enabled without it.

### US-AUTH-4 — Unlock the app · 🟡 Should
> **As** any user with PIN on, **I want** to enter my PIN on launch/resume, **so that** only I can open the app.

- AC1: Six dot indicators fill as digits are entered; a full-screen numeric keypad is shown; no back navigation.
- AC2: Correct PIN → Home immediately.
- AC3: Incorrect → dots show danger outline + shake + "Incorrect PIN, try again".
- AC4: The app re-locks when sent to background (resume requires PIN again).

### US-AUTH-5 — Be protected against brute force · 🟡 Should
> **As** a security-conscious user, **I want** lockout after repeated failures, **so that** my PIN can't be guessed.

- AC1: 5 incorrect attempts → 30s lockout with a countdown; keypad disabled until it completes, then attempts reset.

### US-AUTH-6 — Unlock with biometrics · 🔵 Phase 2
> **As** Carlos ✈️, **I want** Face ID / fingerprint unlock, **so that** I can open the app faster.

- AC1: Biometric is offered but requires the PIN to be set first.
- AC2: If enabled, biometric auto-prompts on the lock screen; success → Home; failure falls back to PIN.
- AC3: Tapping Biometric while PIN is off → "Please enable PIN first to use biometric authentication."

### US-AUTH-7 — Change or disable my PIN · 🟡 Should
> **As** any user, **I want** to change or turn off my PIN, **so that** I stay in control of security.

- AC1: Change PIN: verify current → new → confirm (same mismatch handling).
- AC2: Disable PIN: enter current PIN to confirm; disabling also turns biometric off.

### US-AUTH-8 — Recover a forgotten PIN · 🟡 Should
> **As** Mr. Chen 👴, **I want** to recover access if I forget my PIN, **so that** I'm not locked out of my data.

- AC1: Forgot PIN → security question; correct answer sets a new PIN.
- AC2: Wrong answer → "Try again"; 5 wrong → 30s lockout, then attempts reset.
- AC3: Reset app (clear all data) is offered as a last resort.

---

## 11. Secure Import & Export — `feature:importexport`

**Screen:** 13 More → Data export · **PRD:** Secure Import & Export (🔴 Must)

### US-IE-1 — Export my data · 🔴 Must
> **As** Carlos ✈️, **I want** to export all my records to a file, **so that** I own my data and can back it up.

- AC1: Export produces separate CSVs (expenses / events / debts / shared_costs) zipped into one file.
- AC2: Nothing is uploaded — the file is created locally and shared via the OS sheet.

### US-IE-2 — Import from a backup · 🔴 Must
> **As** Carlos ✈️, **I want** to import records from a CSV/JSON file, **so that** I can move to a new device with no cloud.

- AC1: Import reads a CSV/JSON backup from the local file system.
- AC2: Imported records appear in Journal/History after import.

> **Planned (PRD):** encrypted export for sensitive data. Tracked for traceability.

---

## 12. App Shell & Settings — `app` (Home + More)

**Screens:** 03 Home · 13 More · **PRD:** Central hub / Foundation (🔴 Must)

### US-HOME-1 — See a home that fits how I use the app · 🔴 Must
> **As** any persona, **I want** a contextual home header, **so that** the most relevant number is front and center.

- AC1: Header switches by persona context: Casual → total spent this month; Budget Planner → spent vs. budget + progress; Event Organizer → active event name + remaining.
- AC2: An Active Event card appears only when an event is running.

### US-HOME-2 — Glance at recent activity · 🔴 Must
> **As** any user, **I want** my recent transactions on Home, **so that** I can review at a glance.

- AC1: Recent shows the last 5–10 entries, grouped by day with a per-day header (Today / Yesterday / date) and badge, note, meta, amount.
- AC2: `See all` opens Journal.
- AC3: Tapping a recent row opens that record's Journal Detail (same behavior as tapping in Journal).

### US-HOME-3 — Get started when empty · 🔴 Must
> **As** a fresh user, **I want** a clear first action, **so that** I know what to do.

- AC1: Empty Home shows an illustration, "No expenses yet…", and a single CTA "Log your first expense".

### US-HOME-4 — Reach features quickly · 🟡 Should
> **As** any user, **I want** quick-access tiles, **so that** I can deep-link to key features.

- AC1: Tiles deep-link to Reports / Debt / Split / Events.
- AC2: Bottom nav (Home active) + raised center `Add` are always present on top-level screens.

### US-MORE-1 — Navigate settings and features · 🔴 Must
> **As** any user, **I want** a single More hub, **so that** I can find features and settings.

- AC1: Feature links: Debt Tracker, Shared Costs, Reports, Category List.
- AC2: Settings: PIN auth, Biometric (greyed until PIN on), Currency, Monthly budget, Default category, Language, Theme (Light/Dark/System), Data export, Clear data, App version.

### US-MORE-2 — Set a monthly budget · 🟡 Should
> **As** Siti 🏠, **I want** to set a monthly budget, **so that** the Home header tracks spend against it.

- AC1: Monthly budget drives the Budget-Planner Home header and resets on the 1st.

### US-MORE-3 — Adjust appearance and language · 🔵 Phase 2
> **As** Mr. Chen 👴, **I want** theme and language options, **so that** the app suits me and my region.

- AC1: Theme offers Light / Dark / System.
- AC2: Language is selectable (localization is Phase 2 in the roadmap).

### US-MORE-4 — Clear my data deliberately · 🔴 Must
> **As** any user, **I want** selective, confirmed data wipes, **so that** I never lose data by accident.

- AC1: Clear data is selective — the user picks what to wipe.
- AC2: Each option requires a confirmation dialog; the action is irreversible.

---

## Traceability summary

| Service | Stories | MVP (🔴/🟡) | Phase 2 (🔵) |
|---|---|---|---|
| onboarding | ONB-1…5 | 5 | — |
| logging | LOG-1…7 | 6 | 1 |
| history | HIS-1…7 | 6 | 1 |
| currency | CUR-1…3 | 3 | (per-record planned) |
| eventbudget | EVT-1…5 | — | 5 |
| debt | DEBT-1…4 | — | 4 |
| sharedcost | SHC-1…5 | 5 | — |
| categories | CAT-1…3 | 3 | — |
| reports | REP-1…3 | 3 | — |
| auth | AUTH-1…8 | 7 | 1 |
| importexport | IE-1…2 | 2 | (encryption planned) |
| app shell / settings | HOME-1…4, MORE-1…4 | 7 | 1 |

> Stories trace to PRD use cases and the screen-spec user journey. Phase tags follow the PRD roadmap:
> Journal, Event Budget, Debt & Lending, Localization, and Biometric are Phase 2, but their screens
> exist in this build, so their stories are documented here for completeness.
