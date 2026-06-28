# User Story

> **ID:** US-DEBT-2 · **Service:** `feature:debt` · **Screen:** 09 Debt Tracker
> **Priority:** 🔵 Phase 2 · **Status:** ✅ Implemented · **Persona:** 💼 Raj (Freelancer)

## Title

> Record money lent or owed

---

## User Story

**As** Raj 💼
**I want to** record a debt with a person and amount
**So that** I remember who owes what without relying on memory

---

## Description

### Background

Freelancers and anyone informally lending/borrowing money need a lightweight record: who, how
much, when, and optionally why and a due date — with due date being purely informational since the
MVP has no reminder system.

### Scope

**In Scope**

* Add Record fields: person, amount, date, optional due date, optional note, optional `@`-linked expense.
* Detail view of a saved record.

**Out of Scope**

* Settling/deleting — covered by [US-DEBT-3](US-DEBT-3.md).
* Conflict warnings — covered by [US-DEBT-4](US-DEBT-4.md).

---

## Acceptance Criteria

### Scenario 1 — Add Record fields

**Given**

* I open Add Record.

**When**

* I fill it in.

**Then**

* Fields are: person (required, ≤ 30 chars), amount (required, > $0), date (today default), optional due date (reference only — no reminders in MVP), optional note (≤ 200), optional `@`-linked expense.

### Scenario 2 — Viewing a saved record

**Given**

* A saved record.

**When**

* I open its detail.

**Then**

* It shows person, amount, dates, status, note, and any linked expense reference.

---

## Functional Requirements

* [ ] Person name is required, ≤ 30 chars.
* [ ] Amount is required and must be > $0.
* [ ] Date defaults to today; due date is optional and purely informational (no reminder/notification).
* [ ] Note is optional, ≤ 200 chars.
* [ ] An optional `@` link references an existing expense.

---

## Non-Functional Requirements

* [ ] **Reliability** — record creation persists fully offline.

---

## Business Rules

* Due date never triggers reminders or notifications in the MVP — it's reference text only.

---

## UI / UX Notes

* **Design / Mockup:** [`09-debt-tracker.md`](../../../design-system-spec/screens/09-debt-tracker.md) → "Add Record".
* **Validation Rules:** person required ≤ 30 chars; amount > $0; note ≤ 200 chars.

---

## Dependencies

* **Story/Task:** [US-DEBT-1](US-DEBT-1.md), [US-LOG-5](../logging/US-LOG-5.md) (`@` linking from the expense side).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the Add Record form passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the PRD roadmap, but the screen exists in this build, so it's documented here for
completeness.
