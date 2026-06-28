# User Story

> **ID:** US-HIS-1 · **Service:** `feature:history` · **Screen:** 05 Journal
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 🏠 Siti (Housekeeper)

## Title

> Review spending by day

---

## User Story

**As** Siti 🏠
**I want to** see my entries grouped by day with daily totals
**So that** I can review my spending like a notebook

---

## Description

### Background

The Journal is the full financial history — its core organizing principle is the day, mirroring
how a paper notebook works: every day is always visible (never collapsed), with the newest entry
of the day on top and a running daily total in the header.

### Scope

**In Scope**

* Day-grouped Journal list, always expanded.
* Newest-created-first ordering within a day.
* Per-day header: date + mono daily total.

**Out of Scope**

* Searching/filtering — covered by [US-HIS-2](US-HIS-2.md), [US-HIS-3](US-HIS-3.md).

---

## Acceptance Criteria

### Scenario 1 — Entries grouped by day

**Given**

* I have logged entries.

**When**

* I open Journal.

**Then**

* Entries are grouped by expense date and every day group is always expanded — no collapsing.

### Scenario 2 — Ordering within a day

**Given**

* A day has several entries.

**When**

* I view it.

**Then**

* The newest-created entry appears first within that day.

### Scenario 3 — Day header shows the total

**Given**

* A day group.

**When**

* I view its header.

**Then**

* It shows the date plus the mono-styled daily total.

---

## Functional Requirements

* [ ] Journal groups records by `recordedAtEpochMillis` day, sorted newest-day-first.
* [ ] Within a day, records sort newest-created-first.
* [ ] Day headers show date + summed total in a monospaced numeral style.
* [ ] Day groups never collapse.

---

## Non-Functional Requirements

* [ ] **Performance** — grouping and totals recompute smoothly as records are added/edited/deleted.
* [ ] **Reliability** — totals are always in sync with the underlying records (no stale cache).

---

## Business Rules

* Day key is the calendar day of `recordedAtEpochMillis`, not creation time.
* Daily total is the sum of all entries grouped under that day.

---

## UI / UX Notes

* **Design / Mockup:** [`05-journal.md`](../../../design-system-spec/screens/05-journal.md).
* **User Flow:** Home → `See all` / bottom nav → Journal.

---

## Technical Notes

* Reads via `FinanceRecordRepository`; day-key grouping logic is shared with Home's Recent section (see [US-HOME-2](../app-shell/US-HOME-2.md)) to keep both surfaces consistent.

---

## Dependencies

* **Story/Task:** [US-HOME-2](../app-shell/US-HOME-2.md) (same grouping reused on Home).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for multi-day grouping passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Day-key computation (calendar year + day-of-year) is shared with Home's Recent section — keep both
in one place rather than duplicating the grouping logic.
