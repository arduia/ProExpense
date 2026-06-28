# Category Management — User Stories

> Service: `feature:categories` · Screen: 11 Category List
> PRD use case: Category Management (🔴 Must). Legend & format: [`../README.md`](../README.md).

### US-CAT-1 — Use sensible default categories · 🔴 Must
> **As** any user, **I want** ready-made categories, **so that** I can log without setup.

- AC1: Defaults (Food, Transport, Shopping, Bills, Health, Entertainment) are locked and always first.
- AC2: Deleting all custom categories still leaves defaults visible — no empty state.

### US-CAT-2 — Create my own category · 🔴 Must
> **As** Raj 💼, **I want** to add a custom category with an icon and color, **so that** it fits my spending.

- AC1: Add/edit via icon picker + color picker; name ≤ 20 chars with counter; duplicates blocked ("A category with this name already exists.").
- AC2: `Add` is disabled until valid.
- AC3: Custom categories follow defaults and are drag-to-reorder; their order mirrors the chip order in Add Expense.

### US-CAT-3 — Edit or delete a custom category · 🔴 Must
> **As** any user, **I want** to manage custom categories, **so that** I can keep them relevant.

- AC1: Edit / Delete is available via a bottom sheet (custom only).
- AC2: Deleting a category moves its expenses to **Uncategorized**.
- AC3: Uncategorized is a system category: not selectable when logging, but shown in Journal & Reports for reference.
