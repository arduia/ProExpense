# User Story

> **ID:** US-CUR-4 · **Service:** `feature:logging` · **Screen:** 04 Add Expense (Amount · Details)
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** ✈️ Carlos (Traveler) / 🧳 Sophie (Expat)

## Title

> Log an entry in a different currency with a manual rate

---

## User Story

**As** Carlos ✈️
**I want to** log an expense in the local currency I actually paid in, with a manual exchange rate
**So that** my spend still rolls up correctly into my home-currency totals, budgets, and reports

---

## Description

### Background

PRD Multi-Currency Feature #4 ("log each entry in any currency") and #6 ("enter exchange rate
manually per entry") are 🔴 Must-Have — multi-currency is called a core requirement, not an
add-on. US-CUR-1 originally scoped this out of MVP ("no per-record override"); this story
supersedes that line now that it's built. Quick Log's Amount screen gains a currency selector; when
the picked currency differs from the account's home currency, Details shows a manual "1 [X] in [Y]"
rate field and a live converted-amount preview. The record still stores the original currency and
amount faithfully — the conversion only feeds the separate home-currency total used everywhere else
(Home, Journal, Reports, Event Budget spend).

### Scope

**In Scope**

* Currency picker on Quick Log's Amount screen (reuses the same catalog as Settings → Currency).
* Manual exchange-rate field on Details, shown only when the picked currency ≠ home currency.
* Live converted-amount preview ("≈ $48.60") while typing the rate.
* Quick-commit Save redirects to Details instead of failing silently when a rate is still needed.
* Journal/Home rows display the record's own currency + symbol; day/month/event totals sum the
  converted home-currency amount so mixed-currency periods add up correctly.
* Editing a foreign-currency record reloads its original rate (reverse-derived from the stored
  amounts) so re-editing doesn't silently reset it.
* CSV/JSON export and import carry the home-currency amount/code alongside the original, so a
  re-imported backup doesn't lose the conversion.

**Out of Scope**

* Auto-fetched live exchange rates — manual entry only (PRD: "keeps it simple, no API needed").
* Per-participant currency in Shared Costs — untouched by this story.

---

## Acceptance Criteria

### Scenario 1 — Logging in a foreign currency

**Given**

* My home currency is USD.

**When**

* I pick EUR on the Amount screen and enter €45.00, then enter a manual rate of 1.08 on Details.

**Then**

* The record stores €45.00 as its own amount/currency and $48.60 as its home-currency amount.

### Scenario 2 — Totals stay correct

**Given**

* I have both USD and EUR records in the same month.

**When**

* I view Home, Journal, or Reports totals.

**Then**

* The total is the sum of each record's home-currency amount, not a mix of raw foreign and home
  cents.

### Scenario 3 — Quick-commit without a rate yet

**Given**

* I picked a foreign currency on the Amount screen and haven't entered a rate.

**When**

* I tap the quick-commit "Save" button.

**Then**

* I land on Details (where the rate field lives) instead of the record silently failing to save.

---

## Functional Requirements

* [ ] Currency picker available on Quick Log's Amount screen; same catalog as Settings → Currency.
* [ ] Manual exchange-rate field appears on Details only when currency ≠ home currency.
* [ ] Save is blocked (redirected to Details) until a positive rate is entered for foreign currency.
* [ ] `FinanceRecord.homeCurrencyMoney` reflects the converted amount, not a copy of the original.
* [ ] Day/month/event/report totals sum `homeCurrencyMoney`, never the original per-record amount.
* [ ] Editing a foreign-currency record restores its original currency + rate.
* [ ] Export/import preserves both the original and home-currency amount + currency code.

---

## Non-Functional Requirements

* [ ] **Correctness** — mixed-currency totals never silently misreport (no cross-currency cent
  addition).
* [ ] **Offline** — no network call for the rate; fully manual, works offline.

---

## Business Rules

* Exchange rate must be a positive number; zero/blank/negative rejects the save.
* Same-currency entries never show or require a rate field.

---

## UI / UX Notes

* **Design / Mockup:** extends [`04-add-expense.md`](../../../design-system-spec/screens/04-add-expense.md)
  — the original spec has no currency picker; this adds a "USD ▾" pill above the amount display and
  an exchange-rate card on Details, both built from existing tokens/components
  (`ProTextAction`, `DetailFieldCard`, `CurrencyPickerContent`).

---

## Technical Notes

* `Money.convertedTo(currency, rate)` (`core:domain`) does the conversion math.
* Storage: `finance_record.home_currency_code` (new column, migration `7.sqm`) pairs with the
  existing `home_amount_cents` — both null when currency == home currency.
* `feature:logging`'s `SaveExpenseInput.homeCurrencyCode` / `exchangeRateRaw` thread the rate from
  UI state to `Money.convertedTo`; `SaveExpenseOutcome.InvalidExchangeRate` is the failure case
  (unreachable in production since the UI gates Save first).

---

## Dependencies

* **Story/Task:** [US-CUR-1](US-CUR-1.md) (home currency), [US-HOME-1](../app-shell/US-HOME-1.md),
  [US-HIS-1](../history/US-HIS-1.md), [US-REP-1](../reports/US-REP-1.md) (all consume
  `homeCurrencyMoney` for totals), [US-EVT-1](../eventbudget/US-EVT-1.md) (event spend cache).

---

## Definition of Done

* [x] Acceptance criteria met
* [x] Code reviewed
* [x] Unit tests completed
* [x] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* **Gap fix (2026-07):** supersedes US-CUR-1's "no per-record override in MVP" line and the PRD
  audit finding that multi-currency logging (PRD Feature #4/#6) didn't exist despite being 🔴
  Must-Have. Implementing this also surfaced two unrelated latent defects fixed alongside it:
  (1) `finance_record.home_amount_cents` had no paired currency column, so `homeCurrencyMoney`
  silently reused the record's own currency code — invisible while every record was single-currency,
  wrong the moment one wasn't; (2) the CSV parser's quote-escape check couldn't distinguish an
  *empty* quoted field from an escaped literal quote, corrupting every row with a blank note or tag
  (the common case), and the JSON parser's nested-object regex could never match `"money":{"cents":…}`
  at all, so JSON import silently imported zero records. Covered by
  `MoneyTest.convertedTo*`, `SaveExpenseUseCasesTest`'s foreign-currency/invalid-rate cases,
  `SqlDelightFinanceRecordRepositoryTest`'s foreign-currency round-trip and event-cache tests,
  `FinanceRecordMapperTest`, and `SqlDelightImportExportRepositoryTest`'s CSV/JSON round-trip tests.
