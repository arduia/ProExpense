# User Story

> **ID:** US-IE-1 · **Service:** `feature:importexport` · **Screen:** 13 More → Data export
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Frequent Traveler)

## Title

> Export my data

---

## User Story

**As** Carlos ✈️
**I want to** export all my records to a file
**So that** I own my data and can back it up myself, without relying on any cloud service

---

## Description

### Background

Data ownership is a non-negotiable product constraint — there is no cloud sync, so the only backup
path is a local export the user fully controls. Splitting by record type into separate CSVs (rather
than one mixed file) keeps the export human-readable and easy to inspect or re-import selectively.

### Scope

**In Scope**

* Building a zip of per-type CSVs (expenses / events / debts / shared_costs).
* Sharing the resulting file via the OS share sheet.

**Out of Scope**

* Importing a previously exported file — covered by [US-IE-2](US-IE-2.md).
* Encrypted export — not yet implemented, tracked under Notes.

---

## Acceptance Criteria

### Scenario 1 — Building the export file

**Given**

* I trigger an export.

**When**

* The file is built.

**Then**

* Separate CSVs (expenses, events, debts, shared_costs) are generated and zipped into a single file.

### Scenario 2 — Sharing, not uploading

**Given**

* The export completes.

**When**

* I share it.

**Then**

* The file is created locally on-device and shared through the OS share sheet — nothing is
  uploaded to any server.

---

## Functional Requirements

* [ ] Export produces one CSV per record type (expenses, events, debts, shared_costs).
* [ ] All per-type CSVs are packaged into a single zip file.
* [ ] The zip is written to local device storage only.
* [ ] Sharing the export uses the platform share sheet; no network call is made.

---

## Non-Functional Requirements

* [ ] **Privacy** — export never transmits data off-device; the only distribution path is the
  user's own choice via the OS share sheet.

---

## Business Rules

* Export covers every record type the app stores; no record type is silently excluded.

---

## UI / UX Notes

* **Design / Mockup:** [`13-more.md`](../../../design-system-spec/screens/13-more.md) → Data export.

---

## Dependencies

* **Story/Task:** [US-IE-2](US-IE-2.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

**Planned (PRD):** encrypted export for sensitive data is not yet implemented. Tracked here for
traceability against the PRD's Secure Import & Export use case.
