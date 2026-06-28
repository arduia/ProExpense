# Record History & Journal — User Stories

> Service: `feature:history` · Screens: 05 Journal · 06 Journal Detail
> PRD use case: Record History (🔴) / Financial Journal (🔵). Legend & format: [`../README.md`](../README.md).

### US-HIS-1 — Review spending by day · 🔴 Must
> **As** Siti 🏠, **I want** my entries grouped by day with daily totals, **so that** I can review like a notebook.

- AC1: Entries are grouped by expense date; days are always expanded (no collapsing).
- AC2: Within a day, newest-created appears first.
- AC3: Each day header shows the date plus the mono daily total.

### US-HIS-2 — Find a past entry · 🔴 Must
> **As** any user, **I want** to search by keyword, note, or amount, **so that** I can find a specific entry fast.

- AC1: A search field plus category filter chips sit at the top.
- AC2: When search is active, the list flattens (no day grouping); rows show amount, category, date, note.
- AC3: No matches → centered "No matches" illustration that echoes the query and suggests a different keyword/amount/note.

### US-HIS-3 — Filter by category · 🔴 Must
> **As** Maya 🎓, **I want** to filter the list to one category, **so that** I can see e.g. only food spending.

- AC1: Filter chips mirror the category catalogue; "All" is the default.
- AC2: Selecting a chip narrows the list to that category.

### US-HIS-4 — Jot a quick note without leaving the list · 🔵 Phase 2
> **As** Siti 🏠, **I want** to long-press an entry and add a note inline, **so that** I can annotate quickly.

- AC1: Long-press opens a Quick-note bottom sheet pinned to that entry.
- AC2: `Save` writes the note and dismisses without navigating away.

### US-HIS-5 — Open an entry's full detail · 🔴 Must
> **As** any user, **I want** to tap an entry to see everything about it, **so that** I can review or act on it.

- AC1: Tapping an entry opens Journal Detail showing amount (large), category icon + label, date & time, note, and any `@` tag link.
- AC2: From Home, tapping a recent transaction opens that same record's detail (consistent with Journal).
- AC3: `Back` returns to the list.

### US-HIS-6 — Edit a past entry · 🔴 Must
> **As** any user, **I want** to edit a logged entry, **so that** I can correct mistakes.

- AC1: The detail's action sheet offers Edit · Delete · Cancel.
- AC2: `Edit` opens Add Expense (Details) pre-filled with the record's values (amount, category, date/time, note, tag).
- AC3: Saving updates the **same** record (no duplicate) and returns to Journal; a changed date regroups it.

### US-HIS-7 — Delete a past entry safely · 🔴 Must
> **As** any user, **I want** a confirmation before deleting, **so that** I don't lose data by accident.

- AC1: `Delete` shows a confirmation dialog first, then removes the entry and returns to Journal.
- AC2: If the entry was tagged, the linked Event/Debt recalculates immediately after delete or edit.
