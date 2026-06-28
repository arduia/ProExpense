# Onboarding & First Launch — User Stories

> Service: `feature:onboarding` · Screens: 01 Splash · 02 Onboarding · 02P Profile Setup
> PRD use case: First-launch / setup. Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-ONB-1 — Discover what the app does · 🔴 Must
> **As a** first-time user, **I want** a short swipeable intro to the app's features, **so that** I understand the value before committing.

- **AC1** — **Given** it is the first launch, **when** Splash dismisses, **then** a horizontally swipeable carousel shows Welcome → Quick Log → Shared Costs → Event Budget → Journal.
- **AC2** — **Given** I am on the carousel, **when** I swipe between slides, **then** the page-dot indicator tracks position and the active dot widens.
- **AC3** — **Given** I am onboarding, **when** I view any slide, **then** no use-case selection is offered — features are presented, not chosen.
- **AC4** — **Given** I am past the first slide, **when** I view it, **then** `Back` is shown from slide 2 onward and `Next` is hidden on the last slide.

### US-ONB-2 — Skip the intro · 🟡 Should
> **As an** impatient user, **I want** to skip onboarding, **so that** I can start tracking immediately.

- **AC1** — **Given** I am on any slide except the last, **when** I look top-right, **then** a `Skip` action is present.
- **AC2** — **Given** I tap `Skip`, **when** it is actioned, **then** I jump straight to Profile Setup, then Home.
- **AC3** — **Given** I am on any slide, **when** I view it, **then** the bottom-anchored `Get started` CTA is present so I can start from anywhere.

### US-ONB-3 — Personalize my profile · 🟡 Should
> **As** Maya 🎓, **I want** to enter my name during setup, **so that** the app greets me and labels my exports.

- **AC1** — **Given** I set a name, **when** I reach Home and exports, **then** the greeting reads "Hi, Maya" and CSV exports are labelled with it.
- **AC2** — **Given** I am on Profile Setup, **when** I leave the name blank, **then** the primary action is still enabled (name is optional and pre-focused).
- **AC3** — **Given** I type in the name field, **when** each character is entered, **then** the identity preview card updates live.

### US-ONB-4 — Choose my home currency at setup · 🔴 Must
> **As** Carlos ✈️, **I want** to pick my home currency during setup, **so that** every entry uses the right currency from day one.

- **AC1** — **Given** I am on Profile Setup, **when** I view the currency section, **then** a 2×2 quick grid offers the four most common currencies with USD default-selected.
- **AC2** — **Given** I tap `More currencies`, **when** the searchable sheet opens, **then** selecting a row applies and closes it in one tap.
- **AC3** — **Given** I select a currency, **when** it is applied, **then** the identity card's "Tracking in … · CODE" line updates.
- **AC4** — **Given** setup is complete, **when** I tap `Start tracking`, **then** I land on Home.
- **AC5** — **Given** I chose a non-USD currency, **when** I relaunch the app, **then** that currency is still applied (persistence regression guard).

### US-ONB-5 — Be routed correctly on every launch · 🔴 Must
> **As** any returning user, **I want** the splash to send me to the right place, **so that** I don't navigate manually.

- **AC1** — **Given** the app launches, **when** Splash is shown, **then** it displays ~1.5–2s with no interaction (logo + wordmark only).
- **AC2** — **Given** Splash dismisses, **when** routing runs, **then** first launch → Onboarding, returning + PIN on → PIN Entry, returning + PIN off → Home.
- **AC3** — **Given** an unfinished Add-Expense draft exists, **when** the app relaunches, **then** the restore prompt shows before PIN Entry (no auth required to restore).
