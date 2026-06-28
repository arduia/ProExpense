# User Story

> **ID:** US-ONB-2 · **Service:** `feature:onboarding` · **Screen:** 02 Onboarding
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any impatient user

## Title

> Skip the intro

---

## User Story

**As an** impatient user
**I want to** skip onboarding
**So that** I can start tracking immediately

---

## Description

### Background

Not every user needs the feature tour — some already know what they want and just want to get to
Home. A visible `Skip` and an always-present `Get started` CTA let any user bail out of the
carousel at any point without being forced through every slide.

### Scope

**In Scope**

* `Skip` action on every slide except the last.
* Bottom-anchored `Get started` CTA present on every slide.
* Jump straight to Profile Setup, then Home.

**Out of Scope**

* The carousel content itself — covered by [US-ONB-1](US-ONB-1.md).

---

## Acceptance Criteria

### Scenario 1 — Skip is available mid-carousel

**Given**

* I am on any slide except the last.

**When**

* I look at the top-right of the screen.

**Then**

* A `Skip` action is present.

### Scenario 2 — Skip jumps to setup

**Given**

* I tap `Skip`.

**When**

* It is actioned.

**Then**

* I jump straight to Profile Setup, then Home — no remaining slides are shown.

### Scenario 3 — Get started always available

**Given**

* I am on any slide.

**When**

* I view it.

**Then**

* The bottom-anchored `Get started` CTA is present so I can exit the carousel from anywhere.

---

## Functional Requirements

* [ ] `Skip` renders on slides 1 through second-to-last; hidden on the final slide.
* [ ] Tapping `Skip` or `Get started` routes to Profile Setup, bypassing remaining slides.

---

## Non-Functional Requirements

* [ ] **Accessibility** — `Skip` and `Get started` meet the 48dp minimum touch target.

---

## Business Rules

* Skipping never skips Profile Setup — currency/name selection always happens once before Home.

---

## UI / UX Notes

* **Design / Mockup:** [`02-onboarding.md`](../../../design-system-spec/screens/02-onboarding.md).
* **User Flow:** Onboarding (any slide) → `Skip`/`Get started` → Profile Setup → Home.

---

## Dependencies

* **Story/Task:** [US-ONB-1](US-ONB-1.md) (carousel), [US-ONB-3](US-ONB-3.md) (Profile Setup).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

`Skip` and `Get started` both route to the same destination — there is no separate "fully skip
setup" path, since currency selection is mandatory once.
