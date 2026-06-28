# App Shell & Settings — User Stories

> Service: `app` (Home + More) · Screens: 03 Home · 13 More
> PRD use case: Central hub / Foundation (🔴 Must).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-HOME-1 — See a home that fits how I use the app · 🔴 Must
> **As** any persona, **I want** a contextual home header, **so that** the most relevant number is front and center.

- **AC1** — **Given** my usage pattern, **when** Home renders, **then** the header switches: Casual → total spent this month; Budget Planner → spent vs. budget + progress; Event Organizer → active event name + remaining.
- **AC2** — **Given** an event is running, **when** I view Home, **then** an Active Event card appears (otherwise it is hidden).

### US-HOME-2 — Glance at recent activity · 🔴 Must
> **As** any user, **I want** my recent transactions on Home, **so that** I can review at a glance.

- **AC1** — **Given** I have entries, **when** I view Home, **then** Recent shows the last 5–10 entries grouped by day with a per-day header (Today / Yesterday / date) and badge, note, meta, amount.
- **AC2** — **Given** Recent is shown, **when** I tap `See all`, **then** Journal opens.
- **AC3** — **Given** a recent row, **when** I tap it, **then** that record's Journal Detail opens (same behavior as tapping in Journal).

### US-HOME-3 — Get started when empty · 🔴 Must
> **As** a fresh user, **I want** a clear first action, **so that** I know what to do.

- **AC1** — **Given** I have no expenses, **when** I view Home, **then** an illustration, "No expenses yet…", and a single CTA "Log your first expense" are shown.

### US-HOME-4 — Reach features quickly · 🟡 Should
> **As** any user, **I want** quick-access tiles, **so that** I can deep-link to key features.

- **AC1** — **Given** Home, **when** I tap a quick-access tile, **then** it deep-links to Reports / Debt / Split / Events.
- **AC2** — **Given** a top-level screen, **when** I view it, **then** the bottom nav (Home active) and raised center `Add` are always present.

### US-MORE-1 — Navigate settings and features · 🔴 Must
> **As** any user, **I want** a single More hub, **so that** I can find features and settings.

- **AC1** — **Given** I open More, **when** I view feature links, **then** Debt Tracker, Shared Costs, Reports, and Category List are present.
- **AC2** — **Given** I open More, **when** I view settings, **then** PIN auth, Biometric (greyed until PIN on), Currency, Monthly budget, Default category, Language, Theme (Light/Dark/System), Data export, Clear data, and App version are present.

### US-MORE-2 — Set a monthly budget · 🟡 Should
> **As** Siti 🏠, **I want** to set a monthly budget, **so that** the Home header tracks spend against it.

- **AC1** — **Given** I set a monthly budget, **when** the month progresses, **then** it drives the Budget-Planner Home header and resets on the 1st.

### US-MORE-3 — Adjust appearance and language · 🔵 Phase 2
> **As** Mr. Chen 👴, **I want** theme and language options, **so that** the app suits me and my region.

- **AC1** — **Given** I open Theme, **when** I choose, **then** Light / Dark / System are available.
- **AC2** — **Given** I open Language, **when** I choose, **then** a language is selectable (localization is Phase 2 in the roadmap).

### US-MORE-4 — Clear my data deliberately · 🔴 Must
> **As** any user, **I want** selective, confirmed data wipes, **so that** I never lose data by accident.

- **AC1** — **Given** I open Clear data, **when** I choose what to wipe, **then** the wipe is selective (I pick what to remove).
- **AC2** — **Given** I confirm a wipe, **when** it runs, **then** each option requires a confirmation dialog and the action is irreversible.
