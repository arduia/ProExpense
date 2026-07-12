# Requirements Sync — 2026-07-11

Follow-up to `REQUIREMENTS-SYNC-2026-07-10.md`. That doc's item 2 ("Shared Costs now count toward
Journal & Reports") flagged that Journal/Reports mockups would need a visual marker for
split-originated rows, but didn't yet know the final shape of that system — it shipped over the
following day's worth of commits (`aaf7653f..3c7f14f7`, ~28 commits). This doc captures the shape
that landed. Each item below has already been applied to the relevant spec file; this doc is the
"why," the spec files are the "what."

Scope: genuine product/UX drift only (new or changed intended behavior), same convention as the
prior sync. Pure bugfixes with no behavior change visible to design (e.g. a deep-link race
rendering a blank frame, a wrong-tab deep link) are omitted.

## 1. Split and Debt rows are now first-class in Journal (still hidden on Home Recents)

Home Recents and Journal previously rendered every record as a plain Expense/Income row regardless
of what actually created it. A `RecordKind` classification (Expense / Income / Split / Debt-Lent /
Debt-Owed) now drives a dedicated badge, title, and tap destination for Split and Debt rows in
both feeds — but product wants only plain Expense/Income visible on **Home Recents** until this
mixed-kind UX gets a design pass; **Journal** (the full-history browse) shows every kind now, this
being genuinely its own screen's purpose. A debt only appears as a real, counted row via its own
opt-in "record as transaction" toggle — off by default, since a debt is a lending record, not
necessarily real spending.

**Design ask:** the six-kind row system (Expense/Income/Split/Debt×2) has no Hi-Fi mockup yet —
worth a dedicated mixed-kind Journal mock, and a decision on whether/when Split rows join Home
Recents too (engineering already has both feeds working behind a single flag flip).

**Updated:** `screens/03-home.md`, `screens/05-journal.md`, `components/transaction-row.md`.

## 2. Row naming, badge, and amount-color conventions for Split/Debt

Once Split/Debt rows needed to render outside their own feature, three separate conventions had to
be settled (previously undocumented, iterated live across several commits):

- **Title:** falls back to a type label, never the linked record's bookkeeping category (a debt's
  linked expense is literally categorized "Shopping" or "Gift," which reads as nonsense in a feed).
  Split reads **"Split · \<name\>"** (was briefly "Shared - "); Debt reads **"Lent to \<person\>"**
  / **"Owe \<person\>"**.
- **Badge:** a fixed glyph + tint per kind instead of a category lookup — Split uses the existing
  orange @-tag hue, Debt reuses the Debt Tracker's own Lent=green / Owe=red convention regardless
  of which screen it's rendered on.
- **Amount color:** follows each screen's own cash-flow convention independently of the badge —
  neutral ink for Split and Debt-Lent everywhere; Debt-Owed is neutral on Journal but renders
  success green on Home Recents specifically (the one deliberate screen-specific override).

**Design ask:** confirm the orange badge tint for Split doesn't read as "this is tagged to an
event" (the @-tag orange is already a taken signal per the 07-10 sync's income-color note) — three
same-hue usages (event tag, split badge, on-track budget) may need differentiating the same way
the 07-10 doc flagged for green.

**Updated:** `components/transaction-row.md`, `screens/05-journal.md`, `screens/03-home.md`.

## 3. Event-tagged expenses hide Income categories from the picker

An event budget only makes sense as spend. The category chip row on Add Expense now excludes
Income categories the instant an expense carries an @ tag to an event — live-checked, so tagging
via the in-flow "@" picker narrows the choices too, not just the "Add tagged" deep-link entry point
that starts pre-tagged.

**Design ask:** none blocking — this is a filter rule on the existing category chip layout from the
07-10 income-support sync, not a new surface.

**Updated:** `screens/07-event-budget.md`.

---

## Not in scope here (see `docs/finance_tracker_product.md` for full MVP status)

Product-level roadmap items unchanged by this sync. Also excluded: pure bugfixes with no design-
visible behavior change (deep-link race placeholder frame, wrong-tab debt deep link, Shared Cost
Summary read-only-flag conflation) — tracked in commit history, not design docs.
