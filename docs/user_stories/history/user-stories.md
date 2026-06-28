# Record History & Journal — User Stories

> Service: `feature:history` · Screens: 05 Journal · 06 Journal Detail
> PRD use case: Record History (🔴) / Financial Journal (🔵).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-HIS-1 — Review spending by day · 🔴 Must
> **As** Siti 🏠, **I want** my entries grouped by day with daily totals, **so that** I can review like a notebook.

- **AC1** — **Given** I have logged entries, **when** I open Journal, **then** entries are grouped by expense date and days are always expanded (no collapsing).
- **AC2** — **Given** a day has several entries, **when** I view it, **then** the newest-created appears first within that day.
- **AC3** — **Given** a day group, **when** I view its header, **then** it shows the date plus the mono daily total.

### US-HIS-2 — Find a past entry · 🔴 Must
> **As** any user, **I want** to search by keyword, note, or amount, **so that** I can find a specific entry fast.

- **AC1** — **Given** I open Journal, **when** I view the top, **then** a search field plus category filter chips are present.
- **AC2** — **Given** search is active, **when** results show, **then** the list flattens (no day grouping) and rows show amount, category, date, note.
- **AC3** — **Given** a search has no matches, **when** results render, **then** a centered "No matches" illustration echoes the query and suggests a different keyword/amount/note.

### US-HIS-3 — Filter by category · 🔴 Must
> **As** Maya 🎓, **I want** to filter the list to one category, **so that** I can see e.g. only food spending.

- **AC1** — **Given** I open Journal, **when** I view the filter chips, **then** they mirror the category catalogue with "All" default.
- **AC2** — **Given** I select a category chip, **when** the list updates, **then** it narrows to that category.

### US-HIS-4 — Jot a quick note without leaving the list · 🔵 Phase 2
> **As** Siti 🏠, **I want** to long-press an entry and add a note inline, **so that** I can annotate quickly.

- **AC1** — **Given** I am on the Journal list, **when** I long-press an entry, **then** a Quick-note bottom sheet opens pinned to that entry.
- **AC2** — **Given** the Quick-note sheet is open, **when** I tap `Save`, **then** the note is written and the sheet dismisses without navigating away.

### US-HIS-5 — Open an entry's full detail · 🔴 Must
> **As** any user, **I want** to tap an entry to see everything about it, **so that** I can review or act on it.

- **AC1** — **Given** I am on the Journal list, **when** I tap an entry, **then** Journal Detail shows amount (large), category icon + label, date & time, note, and any `@` tag link.
- **AC2** — **Given** I am on Home, **when** I tap a recent transaction, **then** that same record's detail opens (consistent with Journal).
- **AC3** — **Given** I am on Journal Detail, **when** I tap `Back`, **then** I return to the list.

### US-HIS-6 — Edit a past entry · 🔴 Must
> **As** any user, **I want** to edit a logged entry, **so that** I can correct mistakes.

- **AC1** — **Given** I am on Journal Detail, **when** I open the action sheet, **then** it offers Edit · Delete · Cancel.
- **AC2** — **Given** I tap `Edit`, **when** the editor opens, **then** Add Expense (Details) is pre-filled with the record's values (amount, category, date/time, note, tag).
- **AC3** — **Given** I change fields and save, **when** the update commits, **then** the same record is updated (no duplicate), I return to Journal, and a changed date regroups it.

### US-HIS-7 — Delete a past entry safely · 🔴 Must
> **As** any user, **I want** a confirmation before deleting, **so that** I don't lose data by accident.

- **AC1** — **Given** I choose `Delete`, **when** I confirm in the dialog, **then** the entry is removed and I return to Journal.
- **AC2** — **Given** the entry was tagged, **when** it is edited or deleted, **then** the linked Event/Debt recalculates immediately.
