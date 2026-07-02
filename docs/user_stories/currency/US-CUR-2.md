# User Story

> **ID:** US-CUR-2 · **Service:** `feature:currency` · **Screen:** 13 More → Currency
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** 🧳 Sophie (Expat)

## Title

> Change my currency later

---

## User Story

**As** Sophie 🧳
**I want to** change the default currency in settings
**So that** I can adjust after relocating or changing my main spending currency

---

## Description

### Background

Life circumstances change — an expat relocating needs to change their tracked currency without
re-running onboarding. More → Currency exposes the same picker used at setup, and the change must
persist across relaunches reliably (this exact path previously regressed and silently fell back to
USD).

### Scope

**In Scope**

* Currency picker reachable from More → Currency.
* Persisting the new currency across relaunches.

**Out of Scope**

* Initial setup-time selection — covered by [US-CUR-1](US-CUR-1.md), [US-ONB-4](../onboarding/US-ONB-4.md).
* Converting historical records to the new currency — out of scope (single-currency MVP has no conversion).

---

## Acceptance Criteria

### Scenario 1 — Picking a new default

**Given**

* I am in More → Currency.

**When**

* I pick a currency.

**Then**

* It becomes the default applied to all entries going forward.

### Scenario 2 — Change persists

**Given**

* I changed the currency.

**When**

* I relaunch the app.

**Then**

* The change persists — it does not reset to USD.

---

## Functional Requirements

* [ ] More → Currency opens the same picker UI used during setup.
* [ ] Selecting a currency immediately updates the home currency setting.
* [ ] The setting survives app relaunch.

---

## Non-Functional Requirements

* [ ] **Reliability** — currency persistence must not depend on any unrelated field (e.g. display name) being set.

---

## Business Rules

* Historical records keep their stored amount; the MVP does not retroactively convert past entries when the home currency changes (single-currency display only).

---

## UI / UX Notes

* **Design / Mockup:** [`13-more.md`](../../../design-system-spec/screens/13-more.md) → "Currency".
* **User Flow:** More → Currency → picker → applied.

---

## Technical Notes

* Same `CurrencyRepository.setHomeCurrency(...)` call as setup. A prior regression gated this
  persistence call behind an unrelated onboarding-name check — the fix decouples currency
  persistence from any other field's state.

---

## Dependencies

* **Story/Task:** [US-CUR-1](US-CUR-1.md), [US-ONB-4](../onboarding/US-ONB-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the More → Currency screen passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Scenario 2 is a regression guard — currency selection previously reset to USD on relaunch in some
flows; both `US-CUR-2` and [US-ONB-4](../onboarding/US-ONB-4.md) carry this guard.

Fixed a second regression against Scenario 1's "immediately" wording: `ExpenseApp.kt` kept its own
`homeCurrencyCode` copy, refreshed only via `LaunchedEffect(onboardingComplete, userCurrency)` —
neither key changes when Settings saves a new currency, so Quick Log kept using the previous
currency until the app relaunched. `MoreFlow` now takes an `onCurrencyChanged` callback invoked
right after `saveHomeCurrency` succeeds, so `ExpenseApp` updates its copy (and therefore Quick Log)
in the same session, not just after restart.
