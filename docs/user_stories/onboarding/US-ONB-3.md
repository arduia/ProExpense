# User Story

> **ID:** US-ONB-3 · **Service:** `feature:onboarding` · **Screen:** 02P Profile Setup
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** 🎓 Maya (Student)

## Title

> Personalize my profile

---

## User Story

**As** Maya 🎓
**I want to** enter my name during setup
**So that** the app greets me and labels my exports

---

## Description

### Background

A name personalizes the Home greeting and gives exported CSVs a recognizable label, but it must
never block onboarding — many users will skip it, so the field is optional and the primary action
stays enabled regardless.

### Scope

**In Scope**

* Optional name field on Profile Setup, pre-focused.
* Live identity preview card.
* Greeting on Home ("Hi, Maya") and CSV export labelling.

**Out of Scope**

* Currency selection on the same screen — covered by [US-ONB-4](US-ONB-4.md).

---

## Acceptance Criteria

### Scenario 1 — Name personalizes greeting and exports

**Given**

* I set a name during Profile Setup.

**When**

* I reach Home and later export my data.

**Then**

* The Home greeting reads "Hi, Maya" and CSV exports are labelled with that name.

### Scenario 2 — Name is optional

**Given**

* I am on Profile Setup.

**When**

* I leave the name field blank.

**Then**

* The primary action remains enabled — the field is optional and pre-focused on screen entry.

### Scenario 3 — Live preview while typing

**Given**

* I am typing in the name field.

**When**

* Each character is entered.

**Then**

* The identity preview card updates live.

---

## Functional Requirements

* [ ] Name field is optional; primary action enablement never depends on it.
* [ ] Name field is pre-focused when Profile Setup opens.
* [ ] Identity preview card reflects the name live, character by character.
* [ ] A set name appears in the Home greeting and on exported CSV files.

---

## Non-Functional Requirements

* [ ] **Accessibility** — name field has a visible label and meets minimum touch target/contrast.

---

## Business Rules

* No name set → Home greeting falls back to a generic greeting (no "Hi, ").

---

## UI / UX Notes

* **Design / Mockup:** [`02P-profile-setup.md`](../../../design-system-spec/screens/02P-profile-setup.md).
* **User Flow:** Onboarding → Profile Setup (name + currency) → Home.
* **Empty States:** blank name is a valid, fully supported state.

---

## Technical Notes

* Persisted via the profile preference store (`ProfileRepository.setDisplayName(...)`), read back by Home and export.

---

## Dependencies

* **Story/Task:** [US-ONB-4](US-ONB-4.md) (currency, same screen).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for blank/filled name states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

The name has no length limit enforced in the current build beyond reasonable UI wrapping; revisit
if abuse/edge cases surface.
