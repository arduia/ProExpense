# User Story

> **ID:** US-HIS-2 · **Service:** `feature:history` · **Screen:** 05 Journal
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Find a past entry

---

## User Story

**As** any user
**I want to** search by keyword, note, or amount
**So that** I can find a specific entry fast

---

## Description

### Background

As the Journal grows, scrolling day-by-day stops scaling. A single search field covering
category, note, and amount text gives a fast path to any entry, flattening day grouping while a
query is active so results aren't broken across day headers.

### Scope

**In Scope**

* Search field at the top of Journal.
* Flattened (non-grouped) result list while searching.
* "No matches" empty state.

**Out of Scope**

* Category chip filtering — covered by [US-HIS-3](US-HIS-3.md).

---

## Acceptance Criteria

### Scenario 1 — Search field present

**Given**

* I open Journal.

**When**

* I view the top of the screen.

**Then**

* A search field plus category filter chips are present.

### Scenario 2 — Results flatten while searching

**Given**

* Search is active.

**When**

* Results render.

**Then**

* The list flattens (no day grouping) and each row shows amount, category, date, and note.

### Scenario 3 — No matches

**Given**

* A search has no matches.

**When**

* Results render.

**Then**

* A centered "No matches" illustration echoes the query and suggests trying a different keyword, amount, or note.

---

## Functional Requirements

* [ ] Search matches against category name, note text, and amount.
* [ ] An active query flattens the list — day headers are not shown.
* [ ] Each flattened result row shows amount, category, date, note.
* [ ] Zero results renders a "No matches" empty state referencing the query.

---

## Non-Functional Requirements

* [ ] **Performance** — search filters responsively as the user types (no visible lag at typical record counts).

---

## Business Rules

* Search is local/offline only — no network lookups.

---

## UI / UX Notes

* **Design / Mockup:** [`05-journal.md`](../../../design-system-spec/screens/05-journal.md) → "Search · results" / "Search · no matches".
* **Empty States:** "No matches" illustration + query echo + suggestion text.

---

## Dependencies

* **Story/Task:** [US-HIS-1](US-HIS-1.md) (default grouped view), [US-HIS-3](US-HIS-3.md) (category filter).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for results/no-matches states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
