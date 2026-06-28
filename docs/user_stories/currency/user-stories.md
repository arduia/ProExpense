# Multi-Currency — User Stories

> Service: `feature:currency` · Screens: 02P Currency picker · 13 More → Currency
> PRD use case: Multi-Currency (🔴 core, not an add-on).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-CUR-1 — Set a default home currency · 🔴 Must
> **As** Sophie 🧳, **I want** to set my home currency, **so that** all entries use a single consistent currency (single-currency MVP).

- **AC1** — **Given** a home currency is set, **when** I log any entry, **then** that currency applies to it.
- **AC2** — **Given** I am setting up or in settings, **when** I open the currency option, **then** it is selectable at setup (see [onboarding US-ONB-4](../onboarding/user-stories.md)) and from More → Currency.

### US-CUR-2 — Change my currency later · 🔴 Must
> **As** Sophie 🧳, **I want** to change the default currency in settings, **so that** I can adjust after a move.

- **AC1** — **Given** I am in More → Currency, **when** I pick a currency, **then** it becomes the default applied to all entries.
- **AC2** — **Given** I changed the currency, **when** I relaunch, **then** the change persists.

### US-CUR-3 — Search for a less-common currency · 🟡 Should
> **As** Carlos ✈️, **I want** to search the currency list, **so that** I can quickly find a specific currency.

- **AC1** — **Given** the "More currencies" sheet is open, **when** I type in search, **then** the list filters and selecting a row applies and closes it in one tap.

**Notes / edge cases**
- **Planned (post-MVP, per PRD MC section):** per-record currency, manual exchange rate per entry, and "original + converted" display. Tracked for traceability; **not** in MVP scope.
