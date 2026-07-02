# User Story

> **ID:** US-CUR-3 · **Service:** `feature:currency` · **Screen:** 02P Currency picker / 13 More → Currency
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Traveler)

## Title

> Search for a less-common currency

---

## User Story

**As** Carlos ✈️
**I want to** search the currency list
**So that** I can quickly find a specific currency outside the common four

---

## Description

### Background

The quick-pick grid only covers the four most common currencies; everyone else needs to search a
longer list. The `More currencies` sheet exists exactly for this, shared between Profile Setup and
More → Currency.

### Scope

**In Scope**

* Search field inside the `More currencies` sheet.
* One-tap select-and-close on a filtered row.

**Out of Scope**

* The quick-pick grid itself — covered by [US-ONB-4](../onboarding/US-ONB-4.md).

---

## Acceptance Criteria

### Scenario 1 — Searching filters the list

**Given**

* The "More currencies" sheet is open.

**When**

* I type in search.

**Then**

* The list filters to matching currencies.

### Scenario 2 — Selecting applies and closes

**Given**

* A filtered row.

**When**

* I select it.

**Then**

* It applies and the sheet closes in one tap.

---

## Functional Requirements

* [ ] Search filters the full currency list by name/code as the user types.
* [ ] Selecting any row applies that currency and dismisses the sheet immediately.

---

## Non-Functional Requirements

* [ ] **Performance** — filtering responds with no visible lag for the full currency list size.

---

## Business Rules

* The same `More currencies` sheet and search behavior is shared by both entry points (setup and settings).

---

## UI / UX Notes

* **Design / Mockup:** [`02P-profile-setup.md`](../../../design-system-spec/screens/02P-profile-setup.md) → "More currencies".
* **User Flow:** `More currencies` → search → tap row → applied + sheet closes.

---

## Dependencies

* **Story/Task:** [US-ONB-4](../onboarding/US-ONB-4.md), [US-CUR-2](US-CUR-2.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the search-results state passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** the More → Currency settings screen (`MoreCurrencyScreen`) had no search
  field at all — only the Profile Setup entry point (`ProfileSetupScreen`'s sheet, backed by
  `CurrencyPickerContent`) had search wired. Added a hoisted `searchQuery` + `SearchField` directly
  in `MoreCurrencyScreen`, filtering by code/name case-insensitively, matching the search behavior
  (not the exact shared composable) of the Profile Setup entry point. Re-recorded the
  `MoreScreenshotTest.more_currency` baseline for the new search field.
