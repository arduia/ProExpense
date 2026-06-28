# Secure Import & Export — User Stories

> Service: `feature:importexport` · Screen: 13 More → Data export
> PRD use case: Secure Import & Export (🔴 Must). Legend & format: [`../README.md`](../README.md).

### US-IE-1 — Export my data · 🔴 Must
> **As** Carlos ✈️, **I want** to export all my records to a file, **so that** I own my data and can back it up.

- AC1: Export produces separate CSVs (expenses / events / debts / shared_costs) zipped into one file.
- AC2: Nothing is uploaded — the file is created locally and shared via the OS sheet.

### US-IE-2 — Import from a backup · 🔴 Must
> **As** Carlos ✈️, **I want** to import records from a CSV/JSON file, **so that** I can move to a new device with no cloud.

- AC1: Import reads a CSV/JSON backup from the local file system.
- AC2: Imported records appear in Journal/History after import.

> **Planned (PRD):** encrypted export for sensitive data. Tracked for traceability.
