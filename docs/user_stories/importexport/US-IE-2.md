# User Story

> **ID:** US-IE-2 · **Service:** `feature:importexport` · **Screen:** 13 More → Data export
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Frequent Traveler)

## Title

> Import from a backup

---

## User Story

**As** Carlos ✈️
**I want to** import records from a CSV/JSON backup file
**So that** I can move to a new device with no cloud account required

---

## Description

### Background

Since there's no account or cloud sync, switching devices (or recovering from a reinstall) depends
entirely on the user's own exported backup. Import needs to read directly from the local file
system and land the records exactly where they'd appear had they been logged natively.

### Scope

**In Scope**

* Reading a CSV/JSON backup from the local file system.
* Surfacing imported records in Journal/History.

**Out of Scope**

* Producing the export file itself — covered by [US-IE-1](US-IE-1.md).

---

## Acceptance Criteria

### Scenario 1 — Reading the backup

**Given**

* A CSV/JSON backup file.

**When**

* I import it.

**Then**

* Records are read directly from the local file system — no network access is involved.

### Scenario 2 — Imported records appear

**Given**

* The import succeeds.

**When**

* I open Journal/History.

**Then**

* The imported records appear there, indistinguishable from natively logged records.

---

## Functional Requirements

* [ ] Import reads a CSV/JSON file selected from local device storage.
* [ ] Imported records are persisted through the same repository path as natively logged records.
* [ ] Successfully imported records are visible in Journal/History immediately after import completes.

---

## Non-Functional Requirements

* [ ] **Reliability** — a malformed or partial backup file does not corrupt or partially overwrite
  existing local data.

---

## Business Rules

* Import is local-file-only — no cloud or network source is ever consulted.

---

## UI / UX Notes

* **Design / Mockup:** [`13-more.md`](../../../design-system-spec/screens/13-more.md) → Data import.

---

## Dependencies

* **Story/Task:** [US-IE-1](US-IE-1.md), [US-HIS-1](../history/US-HIS-1.md) (imported records appear in Journal).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** despite the "✅ Implemented" status, there was no screen or flow behind
  "Data import" at all — the More hub had no entry point and `PreviewImportUseCase`/
  `ImportDataUseCase` (already implemented at the use-case layer) were never invoked from any UI.
  Added `ImportDataFlow` (androidMain), which opens the system file picker via
  `ActivityResultContracts.OpenDocument()`, reads the picked file through `ContentResolver`,
  detects CSV vs. JSON by filename extension, previews the record count before committing, then
  imports on confirmation and reports imported/skipped counts. Wired a new "Data import" row into
  the More hub (`MorePreviewData.kt`, `MoreFlow.kt`) and a new `ImportFlow` entry point on
  `ImportExportFeatureEntry`. Covered by `MoreImportScreen`'s three `@Preview`/Roborazzi states
  (empty, file picked, error) in `MoreScreenshotTest`; the underlying preview/import use cases
  were already covered by `ImportExportUseCasesTest`.
