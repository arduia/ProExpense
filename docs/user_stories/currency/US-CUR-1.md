# User Story

> **ID:** US-CUR-1 · **Service:** `feature:currency` · **Screen:** 02P Profile Setup (Currency)
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 🧳 Sophie (Expat)

## Title

> Set a default home currency

---

## User Story

**As** Sophie 🧳
**I want to** set my home currency
**So that** all entries use a single consistent currency

---

## Description

### Background

Multi-currency is a PRD core pillar, but the MVP keeps it simple: one home currency applies to
every record, app-wide. This story is the foundation — the currency is chosen once (at setup) and
read by every screen that displays or stores an amount.

### Scope

**In Scope**

* A single home currency setting, applied to every logged entry.
* Selectable at setup (Profile Setup) and later from settings.

**Out of Scope**

* Changing it later in detail — covered by [US-CUR-2](US-CUR-2.md).
* Per-record currency / manual exchange rates — covered by [US-CUR-4](US-CUR-4.md).

---

## Acceptance Criteria

### Scenario 1 — Currency applies to every entry

**Given**

* A home currency is set.

**When**

* I log any entry.

**Then**

* That currency applies to it.

### Scenario 2 — Selectable from setup or settings

**Given**

* I am setting up the app or in settings.

**When**

* I open the currency option.

**Then**

* It is selectable at setup (see [US-ONB-4](../onboarding/US-ONB-4.md)) and from More → Currency.

---

## Functional Requirements

* [ ] Exactly one home currency exists app-wide; no per-record override in MVP.
* [ ] Every newly logged record stores amounts under the current home currency.
* [ ] The currency setting is reachable from both Profile Setup and More → Currency.

---

## Non-Functional Requirements

* [ ] **Reliability** — the home currency persists fully offline.

---

## Business Rules

* Single-currency MVP: one home currency, applied uniformly.

---

## UI / UX Notes

* **Design / Mockup:** [`02P-profile-setup.md`](../../../design-system-spec/screens/02P-profile-setup.md), [`13-more.md`](../../../design-system-spec/screens/13-more.md).
* **User Flow:** Profile Setup → currency grid/sheet, or More → Currency.

---

## Technical Notes

* Persisted via `CurrencyRepository.setHomeCurrency(CurrencyCode(...))`.

---

## Dependencies

* **Story/Task:** [US-ONB-4](../onboarding/US-ONB-4.md) (setup-time selection), [US-CUR-2](US-CUR-2.md), [US-CUR-3](US-CUR-3.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** per-record currency, manual exchange rate per entry, and "original +
  converted" display are now implemented — see [US-CUR-4](US-CUR-4.md). This story's "single
  home currency" concept is unchanged; a different per-record currency now converts into it
  instead of the app assuming every record already matches it.
