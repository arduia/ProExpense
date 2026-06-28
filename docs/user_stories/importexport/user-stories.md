# Secure Import & Export — User Stories

> Service: `feature:importexport` · Screen: 13 More → Data export
> PRD use case: Secure Import & Export (🔴 Must).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-IE-1 — Export my data · 🔴 Must
> **As** Carlos ✈️, **I want** to export all my records to a file, **so that** I own my data and can back it up.

- **AC1** — **Given** I trigger an export, **when** the file is built, **then** separate CSVs (expenses / events / debts / shared_costs) are zipped into one file.
- **AC2** — **Given** the export completes, **when** it is shared, **then** the file is created locally and shared via the OS sheet — nothing is uploaded.

### US-IE-2 — Import from a backup · 🔴 Must
> **As** Carlos ✈️, **I want** to import records from a CSV/JSON file, **so that** I can move to a new device with no cloud.

- **AC1** — **Given** a CSV/JSON backup, **when** I import it, **then** records are read from the local file system.
- **AC2** — **Given** the import succeeds, **when** I open Journal/History, **then** the imported records appear.

**Notes / edge cases**
- **Planned (PRD):** encrypted export for sensitive data. Tracked for traceability.
