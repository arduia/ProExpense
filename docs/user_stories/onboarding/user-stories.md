# Onboarding & First Launch — User Stories

> Service: `feature:onboarding` · Screens: 01 Splash · 02 Onboarding · 02P Profile Setup
> PRD use case: First-launch / setup. Legend & format: [`../README.md`](../README.md).

### US-ONB-1 — Discover what the app does · 🔴 Must
> **As a** first-time user, **I want** a short swipeable intro to the app's features, **so that** I
> understand the value before committing.

- AC1: On first launch (after Splash), a horizontally swipeable carousel shows Welcome → Quick Log → Shared Costs → Event Budget → Journal.
- AC2: A page-dot indicator tracks position; the active dot widens.
- AC3: There is **no** use-case selection — features are presented, not chosen.
- AC4: `Back` appears from slide 2 onward; `Next` is hidden on the last slide.

### US-ONB-2 — Skip the intro · 🟡 Should
> **As an** impatient user, **I want** to skip onboarding, **so that** I can start tracking immediately.

- AC1: `Skip` (top-right) is present on every slide except the last.
- AC2: `Skip` jumps straight to Profile Setup, then Home.
- AC3: The bottom-anchored `Get started` CTA is present on **every** slide, so the user can start from anywhere.

### US-ONB-3 — Personalize my profile · 🟡 Should
> **As** Maya 🎓, **I want** to enter my name during setup, **so that** the app greets me and labels my exports.

- AC1: Name personalizes the Home greeting ("Hi, Maya") and CSV exports.
- AC2: Name is **optional**; the field is pre-focused and the primary action is always enabled.
- AC3: The identity preview card updates live as the name is typed.

### US-ONB-4 — Choose my home currency at setup · 🔴 Must
> **As** Carlos ✈️, **I want** to pick my home currency during setup, **so that** every entry uses the right currency from day one.

- AC1: A 2×2 quick grid offers the four most common currencies (USD default, selected).
- AC2: `More currencies` opens a searchable bottom sheet; selecting a row applies and closes in one tap.
- AC3: The identity card's "Tracking in … · CODE" line updates on selection.
- AC4: `Start tracking` completes setup and lands on Home.
- AC5: The chosen currency **persists** across relaunch (regression guard — see fix for currency persistence).

### US-ONB-5 — Be routed correctly on every launch · 🔴 Must
> **As** any returning user, **I want** the splash to send me to the right place, **so that** I don't navigate manually.

- AC1: Splash displays ~1.5–2s with no interaction (logo + wordmark only).
- AC2: Routing on dismiss: first launch → Onboarding; returning + PIN on → PIN Entry; returning + PIN off → Home.
- AC3: If an unfinished Add-Expense draft exists, the restore prompt shows **before** PIN Entry (no auth required to restore).
