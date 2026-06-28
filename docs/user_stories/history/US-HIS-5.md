# User Story

> **ID:** US-HIS-5 · **Service:** `feature:history` · **Screen:** 06 Journal Detail
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Open an entry's full detail

---

## User Story

**As** any user
**I want to** tap an entry to see everything about it
**So that** I can review or act on it fully

---

## Description

### Background

Journal Detail is the single source of truth view for one record — full amount, category, date,
note, and any event/debt link. It must be reachable identically from both Journal and Home's
Recent section, so the same record always shows the same detail regardless of entry point.

### Scope

**In Scope**

* Journal Detail screen: amount, category icon + label, date & time, note, `@` tag link.
* Reachable from Journal rows and from Home's Recent rows.
* `Back` returns to the originating list.

**Out of Scope**

* Edit/delete actions from Detail — covered by [US-HIS-6](US-HIS-6.md), [US-HIS-7](US-HIS-7.md).

---

## Acceptance Criteria

### Scenario 1 — Detail from Journal

**Given**

* I am on the Journal list.

**When**

* I tap an entry.

**Then**

* Journal Detail shows amount (large), category icon + label, date & time, note, and any `@` tag link.

### Scenario 2 — Detail from Home

**Given**

* I am on Home.

**When**

* I tap a recent transaction.

**Then**

* That same record's detail opens (identical behavior to tapping it in Journal).

### Scenario 3 — Back navigation

**Given**

* I am on Journal Detail.

**When**

* I tap `Back`.

**Then**

* I return to the list I came from.

---

## Functional Requirements

* [ ] Journal Detail renders amount, category, date/time, note, and `@` tag link (when present).
* [ ] Tapping a Home Recent row opens the same Detail screen for that exact record (no duplicate detail UI on Home).
* [ ] `Back` returns to the originating screen (Journal or Home → Journal tab), preserving scroll position where feasible.

---

## Non-Functional Requirements

* [ ] **Reliability** — Detail always reflects the live record state, not a stale snapshot.

---

## Business Rules

* There is exactly one Detail surface for a record; Home reuses it rather than duplicating it.

---

## UI / UX Notes

* **Design / Mockup:** [`06-journal-detail.md`](../../../design-system-spec/screens/06-journal-detail.md).
* **User Flow:** Journal row / Home Recent row → Journal Detail → `Back`.

---

## Dependencies

* **Story/Task:** [US-HIS-1](US-HIS-1.md) (Journal list), [US-HOME-2](../app-shell/US-HOME-2.md) (Home Recent), [US-HOME-2](../app-shell/US-HOME-2.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the Detail screen passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Home routes into Journal Detail by selecting the tapped record's id and switching to the Journal
tab — there is no separate Home-hosted Detail composable.
