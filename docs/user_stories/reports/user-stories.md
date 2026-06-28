# Reports — User Stories

> Service: `feature:reports` · Screen: 12 Reports
> PRD use case: Record History → Summary View (🟡 Should). Legend & format: [`../README.md`](../README.md).

### US-REP-1 — See where my money went this month · 🟡 Should
> **As** Maya 🎓, **I want** a monthly breakdown by category, **so that** I understand my spending.

- AC1: Total spent is the large headline number for the selected month.
- AC2: A donut chart breaks spending down by category, with a ranked Top-categories list (name + amount) below.
- AC3: Daily average = current month: total ÷ days elapsed; past month: total ÷ days in month.

### US-REP-2 — Move between months · 🟡 Should
> **As** any user, **I want** to switch reporting periods easily, **so that** I can compare months.

- AC1: A period selector is at the top (monthly only in MVP).
- AC2: The period can be changed by swiping left/right **and** by the selector controls (both must work).

### US-REP-3 — Get useful empty/edge behavior · 🟡 Should
> **As** a new user, **I want** the report to behave gracefully with little data, **so that** it's never confusing.

- AC1: No data ever → empty state "No data yet…".
- AC2: No data this month → auto-switches to the last month with data (no empty state).
- AC3: If every expense is uncategorized → one full grey donut segment + tip "categorize your expenses for better insights." Uncategorized appears only when such expenses exist.
