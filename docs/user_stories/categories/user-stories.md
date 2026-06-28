# Category Management — User Stories

> Service: `feature:categories` · Screen: 11 Category List
> PRD use case: Category Management (🔴 Must).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-CAT-1 — Use sensible default categories · 🔴 Must
> **As** any user, **I want** ready-made categories, **so that** I can log without setup.

- **AC1** — **Given** a fresh install, **when** I view categories, **then** the defaults (Food, Transport, Shopping, Bills, Health, Entertainment) are locked and always first.
- **AC2** — **Given** I delete all custom categories, **when** I view the list, **then** defaults remain visible — no empty state.

### US-CAT-2 — Create my own category · 🔴 Must
> **As** Raj 💼, **I want** to add a custom category with an icon and color, **so that** it fits my spending.

- **AC1** — **Given** I add/edit a category, **when** I configure it, **then** I pick an icon + color and a name ≤ 20 chars with counter; duplicates are blocked ("A category with this name already exists.").
- **AC2** — **Given** the form is invalid, **when** I view it, **then** `Add` is disabled until valid.
- **AC3** — **Given** custom categories exist, **when** I reorder them, **then** they follow defaults and drag-to-reorder; their order mirrors the chip order in Add Expense.

### US-CAT-3 — Edit or delete a custom category · 🔴 Must
> **As** any user, **I want** to manage custom categories, **so that** I can keep them relevant.

- **AC1** — **Given** a custom category, **when** I open its bottom sheet, **then** Edit / Delete are available (custom only).
- **AC2** — **Given** I delete a category, **when** it is removed, **then** its expenses move to Uncategorized.
- **AC3** — **Given** Uncategorized, **when** I log or review, **then** it is not selectable when logging but is shown in Journal & Reports for reference.
