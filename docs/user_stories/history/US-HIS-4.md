# User Story

> **ID:** US-HIS-4 · **Service:** `feature:history` · **Screen:** 05 Journal
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 🏠 Siti (Housekeeper)

## Title

> Jot a quick note without leaving the list

---

## User Story

**As** Siti 🏠
**I want to** long-press an entry and add a note inline
**So that** I can annotate quickly without navigating away

---

## Description

### Background

Opening full Journal Detail just to add a one-line note is overkill for quick annotation. A
long-press triggers a small bottom sheet pinned to that entry, so the note is captured without
breaking the user's flow through the list.

### Scope

**In Scope**

* Long-press on a Journal row opens a Quick-note bottom sheet.
* Save writes the note and dismisses without navigation.

**Out of Scope**

* Full entry editing — covered by [US-HIS-6](US-HIS-6.md).

---

## Acceptance Criteria

### Scenario 1 — Long-press opens Quick-note

**Given**

* I am on the Journal list.

**When**

* I long-press an entry.

**Then**

* A Quick-note bottom sheet opens, pinned to that entry.

### Scenario 2 — Saving stays on the list

**Given**

* The Quick-note sheet is open.

**When**

* I tap `Save`.

**Then**

* The note is written and the sheet dismisses without navigating away from the list.

---

## Functional Requirements

* [ ] Long-press gesture on a Journal row opens the Quick-note sheet for that specific record.
* [ ] Saving updates the record's note in place; the underlying list view does not navigate.
* [ ] Note length follows the same 200-char cap as Details (see [US-LOG-6](../logging/US-LOG-6.md)).

---

## Non-Functional Requirements

* [ ] **Accessibility** — long-press has a discoverable affordance (e.g. hint or alternative entry point) for users who can't long-press.

---

## Business Rules

* Quick-note edits the same `note` field as Details/full edit — there is only one note per record.

---

## UI / UX Notes

* **Design / Mockup:** [`05-journal.md`](../../../design-system-spec/screens/05-journal.md) → "Quick note".
* **User Flow:** Journal list → long-press row → Quick-note sheet → `Save` → list (unchanged position).

---

## Dependencies

* **Story/Task:** [US-LOG-6](../logging/US-LOG-6.md) (200-char note cap), [US-HIS-6](US-HIS-6.md) (full edit).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the sheet-open state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

This is a Phase 2 story per the PRD roadmap, but the underlying interaction already exists in this
build, so it's documented here for completeness.
