# User Story

> **ID:** US-ONB-4 · **Service:** `feature:onboarding` · **Screen:** 02P Profile Setup
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Traveler)

## Title

> Choose my home currency at setup

---

## User Story

**As** Carlos ✈️
**I want to** pick my home currency during setup
**So that** every entry uses the right currency from day one

---

## Description

### Background

Multi-currency is a core PRD pillar, not an add-on, so currency selection happens once at setup
rather than being buried in settings. A quick grid covers the four most common currencies; a
searchable sheet covers everyone else.

### Scope

**In Scope**

* 2×2 quick-pick grid of common currencies (USD default-selected).
* `More currencies` searchable sheet for the full list.
* Persisting the chosen currency across relaunches.

**Out of Scope**

* Changing currency later from settings — covered by [US-CUR-2](../currency/US-CUR-2.md).
* Per-record currency / manual exchange rates — explicitly out of MVP scope.

---

## Acceptance Criteria

### Scenario 1 — Quick grid on Profile Setup

**Given**

* I am on Profile Setup.

**When**

* I view the currency section.

**Then**

* A 2×2 quick grid offers the four most common currencies with USD default-selected.

### Scenario 2 — Searching for another currency

**Given**

* I tap `More currencies`.

**When**

* The searchable sheet opens and I select a row.

**Then**

* That currency applies and the sheet closes in one tap.

### Scenario 3 — Identity card reflects the choice

**Given**

* I select a currency.

**When**

* It is applied.

**Then**

* The identity card's "Tracking in … · CODE" line updates to match.

### Scenario 4 — Finishing setup

**Given**

* Setup is complete.

**When**

* I tap `Start tracking`.

**Then**

* I land on Home with the chosen currency active.

### Scenario 5 — Currency persists across relaunch

**Given**

* I chose a non-USD currency during setup.

**When**

* I relaunch the app.

**Then**

* That currency is still applied — selecting a currency must never silently fall back to USD.

---

## Functional Requirements

* [ ] Quick grid shows exactly 4 common currencies; USD is default-selected.
* [ ] `More currencies` opens a searchable sheet covering the full currency list.
* [ ] Selecting a row in the sheet applies it and dismisses the sheet in one tap.
* [ ] The applied currency is written to durable storage independently of other setup fields (e.g. name) being blank.
* [ ] `Start tracking` always routes to Home.

---

## Non-Functional Requirements

* [ ] **Reliability** — currency selection persists fully offline and survives process death.

---

## Business Rules

* Single-currency MVP: one home currency applies to every record, app-wide.
* Currency persistence must not be gated on any other setup field being non-blank.

---

## UI / UX Notes

* **Design / Mockup:** [`02P-profile-setup.md`](../../../design-system-spec/screens/02P-profile-setup.md).
* **User Flow:** Profile Setup → currency grid / `More currencies` sheet → `Start tracking` → Home.

---

## Technical Notes

* Persisted via `CurrencyRepository.setHomeCurrency(CurrencyCode(...))`. The persistence call must
  fire whenever onboarding completes, regardless of whether the display name was set in the same
  pass — a prior regression gated this behind a non-blank name check and silently dropped the
  chosen currency back to USD.

---

## Dependencies

* **Story/Task:** [US-ONB-3](US-ONB-3.md) (same screen, name field), [US-CUR-1](../currency/US-CUR-1.md), [US-CUR-2](../currency/US-CUR-2.md), [US-CUR-3](../currency/US-CUR-3.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for grid/sheet states passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

This story previously regressed: currency persistence was incorrectly coupled to the (optional)
name field being non-blank, so a blank-name setup silently kept USD. Scenario 5 is a regression
guard for that specific bug.
