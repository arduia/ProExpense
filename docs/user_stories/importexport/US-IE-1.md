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
* A JSON format option — a single `expenses.json` file, per PRD's "export as CSV, JSON, or PDF."
* Sharing the resulting file via the OS share sheet.

**Out of Scope**

* Importing a previously exported file — covered by [US-IE-2](US-IE-2.md).

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

* **Gap fix (2026-07, JSON format option):** export only ever produced the grouped-CSV zip —
  `ExportDataUseCase(ExportFormat.JSON)` existed and was unit-tested but had no UI caller, so JSON
  export (PRD Feature #15, "CSV or JSON") was unreachable. `MoreExportScreen` gained a
  ZIP(CSV)/JSON `SegmentedToggle`; picking JSON exports a single `expenses.json` (still
  optionally password-encrypted via the same `ExportFileWriter` zip path). Fixing this also
  surfaced that JSON *import* silently parsed zero records — see [US-IE-2](US-IE-2.md) Notes.

* **Gap fix (2026-07, encrypted export):** the PRD's Secure Import & Export use case (previously
  tracked here as "planned, not yet implemented") is now covered — the export screen gained an
  optional password field (`PasswordField`, new shared design primitive); a non-blank password
  makes `ExportFileWriter` produce an AES-encrypted zip via zip4j. Covered by
  `ExportImportZipRoundTripTest.encryptedZip_roundTripsWithCorrectPassword` and
  `encryptedZip_missingOrWrongPassword_reportsNeedsPassword`.

* **Gap fix (2026-07):** despite the "✅ Implemented" status, `MoreExportScreen` was wired to a
  hardcoded file list and `onExport` was a no-op — no CSV was ever generated, zipped, or shared.
  `ImportExportRepository.exportGrouped()` (new) now builds one CSV per record type (expenses,
  events, debts, shared_costs) from the real repositories; `ExportFileWriter` (androidMain) writes
  them to `cacheDir/exports/` and zips them via the already-declared but previously unused `zip4j`
  dependency. `ExportSettingsFlow` invokes the use case, then launches an `ACTION_SEND` share-sheet
  intent through a new `FileProvider` (`app/src/main/res/xml/file_paths.xml`) — matching the
  existing `more_export_subtitle` copy's promise of "one zip with a CSV per feature." Covered by
  `ExportGroupedDataUseCaseTest.invoke_delegatesToRepository` and
  `SqlDelightImportExportRepositoryTest.exportGrouped_returnsOneCsvPerRecordTypeWithRealData`.
