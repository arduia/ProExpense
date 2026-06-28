# Reports — User Stories

> Service: `feature:reports` · Screen: 12 Reports
> PRD use case: Record History → Summary View (🟡 Should).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-REP-1 — See where my money went this month · 🟡 Should
> **As** Maya 🎓, **I want** a monthly breakdown by category, **so that** I understand my spending.

- **AC1** — **Given** I open Reports, **when** a month is selected, **then** total spent is the large headline number.
- **AC2** — **Given** spending exists, **when** the report renders, **then** a donut chart breaks it down by category with a ranked Top-categories list (name + amount) below.
- **AC3** — **Given** a selected month, **when** the daily average shows, **then** it is total ÷ days elapsed for the current month and total ÷ days in month for a past month.

### US-REP-2 — Move between months · 🟡 Should
> **As** any user, **I want** to switch reporting periods easily, **so that** I can compare months.

- **AC1** — **Given** I am on Reports, **when** I view the top, **then** a period selector is present (monthly only in MVP).
- **AC2** — **Given** I want another period, **when** I swipe left/right or use the selector controls, **then** both methods change the period.

### US-REP-3 — Get useful empty/edge behavior · 🟡 Should
> **As** a new user, **I want** the report to behave gracefully with little data, **so that** it's never confusing.

- **AC1** — **Given** there is no data ever, **when** I open Reports, **then** an empty state "No data yet…" is shown.
- **AC2** — **Given** there is no data this month, **when** I open Reports, **then** it auto-switches to the last month with data (no empty state).
- **AC3** — **Given** every expense is uncategorized, **when** the chart renders, **then** one full grey donut segment shows with the tip "categorize your expenses for better insights." (Uncategorized appears only when such expenses exist.)
