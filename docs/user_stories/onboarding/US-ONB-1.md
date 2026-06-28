# User Story

> **ID:** US-ONB-1 · **Service:** `feature:onboarding` · **Screen:** 01 Splash · 02 Onboarding
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any first-time user

## Title

> Discover what the app does (swipeable intro)

---

## User Story

**As a** first-time user
**I want to** see a short swipeable intro to the app's features
**So that** I understand the value before committing to setup

---

## Description

### Background

A cold-open straight into setup forms gives a new user no sense of what the app actually does.
The intro carousel sets expectations — quick logging, shared costs, event budgets, the journal —
before asking for any input, without forcing a use-case choice that would add friction.

### Scope

**In Scope**

* Splash → Onboarding carousel transition.
* Slides: Welcome → Quick Log → Shared Costs → Event Budget → Journal.
* Page-dot progress indicator, `Back`/`Next` navigation.

**Out of Scope**

* Skipping the intro — covered by [US-ONB-2](US-ONB-2.md).
* Profile/currency setup that follows — covered by [US-ONB-3](US-ONB-3.md), [US-ONB-4](US-ONB-4.md).
* Launch routing logic — covered by [US-ONB-5](US-ONB-5.md).

---

## Acceptance Criteria

### Scenario 1 — Carousel shows on first launch

**Given**

* It is the first launch.

**When**

* Splash dismisses.

**Then**

* A horizontally swipeable carousel opens showing Welcome → Quick Log → Shared Costs → Event Budget → Journal, in that order.

### Scenario 2 — Swiping tracks progress

**Given**

* I am on the carousel.

**When**

* I swipe between slides.

**Then**

* The page-dot indicator tracks my position and the active dot widens.

### Scenario 3 — No use-case selection forced

**Given**

* I am onboarding.

**When**

* I view any slide.

**Then**

* Features are presented for awareness only — no use-case selection is offered or required.

### Scenario 4 — Back/Next affordances

**Given**

* I am past the first slide.

**When**

* I view the navigation row.

**Then**

* `Back` is shown from slide 2 onward and `Next` is hidden on the last slide.

---

## Functional Requirements

* [ ] First launch routes from Splash into the Onboarding carousel (never returning users).
* [ ] Carousel order is fixed: Welcome, Quick Log, Shared Costs, Event Budget, Journal.
* [ ] Page-dot indicator reflects current slide with a widened active dot.
* [ ] `Back` hidden on slide 1; `Next` hidden on the last slide.

---

## Non-Functional Requirements

* [ ] **Accessibility** — swipe gesture has an equivalent tap target (`Next`/`Back`) for users who can't swipe.
* [ ] **Performance** — slide transition is smooth (no dropped frames) on a swipe or tap.

---

## Business Rules

* Onboarding never asks the user to pick a single use case — it is descriptive, not configurational.
* The carousel always runs in the same fixed slide order.

---

## UI / UX Notes

* **Design / Mockup:** [`02-onboarding.md`](../../../design-system-spec/screens/02-onboarding.md).
* **User Flow:** Splash → Onboarding carousel → Profile Setup ([US-ONB-3](US-ONB-3.md)).
* **Empty States:** none — content is static per slide.

---

## Dependencies

* **Story/Task:** [US-ONB-2](US-ONB-2.md) (skip), [US-ONB-5](US-ONB-5.md) (launch routing).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for each slide passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Onboarding is shown once per fresh install; returning users skip straight to PIN Entry or Home (see [US-ONB-5](US-ONB-5.md)).
