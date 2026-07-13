# Requirements Sync — 2026-07-12

Follow-up to `REQUIREMENTS-SYNC-2026-07-11.md`. Two navigation changes shipped on
`refactor/v2-migration` since (`d033bc9c`, "Fix Journal/Home deep-link back navigation for Debt
and Split, and jump Split quick access straight to New Split"). Each item below has already been
applied to the relevant spec file; this doc is the "why," the spec files are the "what."

Scope: genuine product/UX drift only (new or changed intended behavior), same convention as prior
syncs.

## 1. Debt Detail / Split Summary back navigation is now origin-aware

Previously, opening Debt Detail or Split Summary via a deep link (tapping a Split/Debt row in
Journal, or in Home's Recents list) always took two back-presses to actually leave: the first
landed on the feature's own list (Debt list / Shared Costs History), the second finally exited.
Back now returns in **one press**, straight to wherever the deep link came from, and the back
button's label names that origin instead of always reading the feature's own name:

- Opened from **Journal** → back label reads **"Journals"**, returns straight to the Journal tab.
- Opened from **Home Recents** → back label reads **"Home"**, returns straight to Home.
- Opened any other way (Debt/Split tab via **More**, or Home's own quick-access tiles) → unchanged:
  back label still reads "Debt" / "History" / "Split", and still returns to that feature's own
  list first.

**Updated:** `screens/09-debt-tracker.md`, `screens/10-shared-costs.md`.

## 2. Home's Split quick-access tile opens directly on New Split

The Split tile previously landed on the Shared Costs History list, requiring a second tap
("+ New Split") to reach the amount-input screen. It now opens directly on that amount-input
screen. Back from there returns straight to Home (the History list is skipped entirely for this
entry point). The Debt quick-access tile is unchanged — it still opens the Debt list.

**Updated:** `screens/03-home.md`, `components/quick-access.md`.

---

## Not in scope here

Pure bugfixes with no design-visible behavior change are intentionally not documented as separate
items, matching prior syncs' convention — but note both items above are included per explicit
product decision this round, even though item 1 is a deep-link-navigation correctness fix (the
same category the 2026-07-11 sync's "wrong-tab debt deep link" fix was excluded under) — flagged
here rather than omitted because the back button's visible label text changes user-facing copy,
not just internal routing.
