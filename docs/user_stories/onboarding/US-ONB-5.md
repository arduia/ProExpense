# User Story

> **ID:** US-ONB-5 · **Service:** `feature:onboarding` · **Screen:** 01 Splash
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any returning user

## Title

> Be routed correctly on every launch

---

## User Story

**As** any returning user
**I want to** be sent to the right place by the splash screen
**So that** I don't have to navigate manually after opening the app

---

## Description

### Background

Splash is a brief, non-interactive branding moment, but it also carries the app's routing logic:
first launch goes to Onboarding, returning users go to PIN Entry (if PIN is on) or Home, and an
unfinished Add-Expense draft takes priority over all of it so nothing is silently lost.

### Scope

**In Scope**

* Splash duration and content (logo + wordmark only).
* Routing decision: first launch vs. returning, PIN on vs. off.
* Draft-restore prompt priority over PIN Entry.

**Out of Scope**

* The PIN entry flow itself — covered by [US-AUTH-4](../auth/US-AUTH-4.md).
* Draft auto-save mechanics — covered by [US-LOG-7](../logging/US-LOG-7.md).

---

## Acceptance Criteria

### Scenario 1 — Splash is brief and non-interactive

**Given**

* The app launches.

**When**

* Splash is shown.

**Then**

* It displays for roughly 1.5–2s with no interaction available, showing only logo + wordmark.

### Scenario 2 — Routing after splash

**Given**

* Splash dismisses.

**When**

* Routing runs.

**Then**

* First launch → Onboarding; returning user with PIN on → PIN Entry; returning user with PIN off → Home.

### Scenario 3 — Draft restore takes priority

**Given**

* An unfinished Add-Expense draft exists.

**When**

* The app relaunches.

**Then**

* The restore prompt shows before PIN Entry — no authentication is required just to see or discard the restore prompt.

---

## Functional Requirements

* [ ] Splash shows for ~1.5–2s with no tap targets.
* [ ] Routing branches correctly on (first launch) × (PIN on/off).
* [ ] A pending draft's restore prompt always appears before any PIN gate.

---

## Non-Functional Requirements

* [ ] **Performance** — routing decision resolves within the splash duration; no visible delay/flicker after dismiss.
* [ ] **Security** — the draft-restore prompt exposes no financial data beyond what the user typed themselves (no auth bypass risk, since it's the user's own unsaved input).

---

## Business Rules

* Draft restore is offered without authentication because it is pre-save, user-authored input — not stored financial history.
* PIN gate only applies to *stored* data access, never to viewing/discarding an unsaved draft.

---

## UI / UX Notes

* **Design / Mockup:** [`01-splash.md`](../../../design-system-spec/screens/01-splash.md).
* **User Flow:** Splash → (draft prompt) → PIN Entry → Home, or Splash → Onboarding (first launch).

---

## Dependencies

* **Story/Task:** [US-ONB-1](US-ONB-1.md) (Onboarding), [US-AUTH-4](../auth/US-AUTH-4.md) (PIN Entry), [US-LOG-7](../logging/US-LOG-7.md) (draft auto-save).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Routing order is: draft prompt (if any) → PIN gate (if on) → Home/Onboarding. Skipping or
mis-ordering this sequence either loses user data or weakens the PIN gate — both are regressions.
