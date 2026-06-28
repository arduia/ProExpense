# App Shell & Settings — User Stories

> Service: `app` (Home + More) · Screens: 03 Home · 13 More
> PRD use case: Central hub / Foundation (🔴 Must). Legend & format: [`../README.md`](../README.md).

### US-HOME-1 — See a home that fits how I use the app · 🔴 Must
> **As** any persona, **I want** a contextual home header, **so that** the most relevant number is front and center.

- AC1: Header switches by persona context: Casual → total spent this month; Budget Planner → spent vs. budget + progress; Event Organizer → active event name + remaining.
- AC2: An Active Event card appears only when an event is running.

### US-HOME-2 — Glance at recent activity · 🔴 Must
> **As** any user, **I want** my recent transactions on Home, **so that** I can review at a glance.

- AC1: Recent shows the last 5–10 entries, grouped by day with a per-day header (Today / Yesterday / date) and badge, note, meta, amount.
- AC2: `See all` opens Journal.
- AC3: Tapping a recent row opens that record's Journal Detail (same behavior as tapping in Journal).

### US-HOME-3 — Get started when empty · 🔴 Must
> **As** a fresh user, **I want** a clear first action, **so that** I know what to do.

- AC1: Empty Home shows an illustration, "No expenses yet…", and a single CTA "Log your first expense".

### US-HOME-4 — Reach features quickly · 🟡 Should
> **As** any user, **I want** quick-access tiles, **so that** I can deep-link to key features.

- AC1: Tiles deep-link to Reports / Debt / Split / Events.
- AC2: Bottom nav (Home active) + raised center `Add` are always present on top-level screens.

### US-MORE-1 — Navigate settings and features · 🔴 Must
> **As** any user, **I want** a single More hub, **so that** I can find features and settings.

- AC1: Feature links: Debt Tracker, Shared Costs, Reports, Category List.
- AC2: Settings: PIN auth, Biometric (greyed until PIN on), Currency, Monthly budget, Default category, Language, Theme (Light/Dark/System), Data export, Clear data, App version.

### US-MORE-2 — Set a monthly budget · 🟡 Should
> **As** Siti 🏠, **I want** to set a monthly budget, **so that** the Home header tracks spend against it.

- AC1: Monthly budget drives the Budget-Planner Home header and resets on the 1st.

### US-MORE-3 — Adjust appearance and language · 🔵 Phase 2
> **As** Mr. Chen 👴, **I want** theme and language options, **so that** the app suits me and my region.

- AC1: Theme offers Light / Dark / System.
- AC2: Language is selectable (localization is Phase 2 in the roadmap).

### US-MORE-4 — Clear my data deliberately · 🔴 Must
> **As** any user, **I want** selective, confirmed data wipes, **so that** I never lose data by accident.

- AC1: Clear data is selective — the user picks what to wipe.
- AC2: Each option requires a confirmation dialog; the action is irreversible.
