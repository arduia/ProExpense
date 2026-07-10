# Requirements Sync — 2026-07-10

Design brief summarizing where the shipped app (`refactor/v2-migration`, ~377 commits since the
spec's `design-system-spec/` snapshot was captured 2026-06-20–22) has moved past the Hi-Fi
mockups and their extracted specs. Use this as the punch list before the next design pass —
each item below has already been applied to the relevant spec file; this doc is the "why," the
spec files are the "what."

Scope: genuine product/UX drift only (new or changed intended behavior). Known implementation
bugs where the code *doesn't yet match* an already-correct spec/user-story (e.g. missing event
progress amber tier, uncapped quick-note field, discarded category icon/color, unbounded Home
recent list) are tracked separately in `docs/bug-audit-v2-migration.md` — those are engineering
fixes, not design changes, and are intentionally **not** included here.

## 1. Income support (new, previously undocumented)

The app now supports logging **income**, not just expenses. Category chips on the Amount screen
show Income categories (Income / Salary / Gift) in the same row as Expense categories — there is
no separate expense/income toggle; picking a category sets the direction. Income amounts render
in success green (`#4CAF50` light / `#81C784` dark) everywhere an amount surfaces: Add Expense,
Home recent list, Journal.

**Design ask:** mock up the income category chips/icons (currently placeholder, no dedicated
icon/tint per category — accent/tint are unset in code) and confirm the green-amount treatment
reads well next to the existing budget-progress green (`sage` / `#4CAF50` is already used for
"on-track" and "I Lent" — three usages of the same hue may need differentiation).

**Updated:** `screens/04-add-expense.md`, `components/category.md`, `screens/03-home.md`.

## 2. Shared Costs now count toward Journal & Reports

Previously "reference only" — saved splits are now real linked expense records: they show up in
Journal and Reports, editing a split updates its linked record in place, deleting removes both
atomically. Custom per-person shares are also no longer force-rebalanced to sum to the total.

**Design ask:** Journal/Reports mockups should account for shared-cost-originated rows (need a
visual marker distinguishing "this came from a split," similar to the existing @-tag treatment
for events/debts).

**Updated:** `screens/10-shared-costs.md`.

## 3. Shared Costs input polish

Per-participant currency symbol prefix, decimal-only keyboard for custom shares, and editable
name fields (not static "Person N" labels) are all now real. No spec/mock currently shows the
currency-symbol-prefixed input style for this screen.

**Updated:** `screens/10-shared-costs.md`.

## 4. Currency change requires explicit confirmation

Tapping a currency in More → Currency only stages the pick; a Save button (disabled until the
pick differs) plus a confirm dialog ("Change home currency? New entries will use {currency}
going forward. Existing records keep their original currency.") gates the actual change. The old
spec implied instant-apply on tap.

**Updated:** `screens/13-more.md`.

## 5. Home quick-access "Customize" is live, and "Events" → "Goals"

The Customize affordance (previously spec'd as inactive) now opens a real picker: toggle tile
visibility (min. one required) and reorder via chevrons, persisted locally. Separately, the
Home tile and its create CTA were renamed **Events → Goals** / **New event → New goal** — but the
Event Budget screen's own title ("Budget") and its back-navigation label ("Events") were **not**
renamed, so there's now a naming inconsistency between entry point and destination worth
resolving in the next design pass (pick one term and rename everywhere, or intentionally keep
"Goals" as a friendlier marketing label for the same "Events" feature).

**Design ask:** mock the Customize picker sheet (toggle + reorder), and decide the Events/Goals
naming question above.

**Updated:** `components/quick-access.md`, `screens/03-home.md`, `screens/07-event-budget.md`.

## 6. PIN disable/change is a full re-verification sub-flow

What the old spec covered in one line each ("Disable PIN: enter current PIN to confirm", "Change
PIN: verify current → new → confirm") is now a dedicated overlay: both disable and change reuse
the same re-verification screen and share the main lock screen's lockout/attempt-counter state
(so it can't be used to brute-force around the lock), and both render as a tap-swallowing overlay
rather than a normal destination. Change PIN is a 3-step state machine (Verify current → Enter
new → Confirm new) with its own back/error handling per step.

**Design ask:** the current Hi-Fi likely only shows the end-state screens for these flows, not
the re-verification gate itself — worth mocking the gate screen explicitly since it's shared
between two entry points (disable, change) and reuses the primary lock screen's visual language.

**Updated:** `screens/14-pin-setup.md`.

---

## Not in scope here (see `docs/finance_tracker_product.md` for full MVP status)

Product-level roadmap items (Financial Journal as its own use case, Debt & Lending Tracker,
Localization, biometric auth, auto exchange rates) remain Post-MVP per the PRD and are unchanged
by this sync — the six items above are drift *within* already-MVP-shipped screens, not scope
changes.
