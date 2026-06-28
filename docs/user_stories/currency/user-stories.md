# Multi-Currency — User Stories

> Service: `feature:currency` · Screens: 02P Currency picker · 13 More → Currency
> PRD use case: Multi-Currency (🔴 core, not an add-on). Legend & format: [`../README.md`](../README.md).

### US-CUR-1 — Set a default home currency · 🔴 Must
> **As** Sophie 🧳, **I want** to set my home currency, **so that** all entries use a single consistent currency (single-currency MVP).

- AC1: The home currency applies to every entry.
- AC2: It is selectable at setup (see [onboarding US-ONB-4](../onboarding/user-stories.md)) and from More → Currency later.

### US-CUR-2 — Change my currency later · 🔴 Must
> **As** Sophie 🧳, **I want** to change the default currency in settings, **so that** I can adjust after a move.

- AC1: More → Currency offers a selector for common currencies; the chosen one becomes the default applied to all entries.
- AC2: The change persists across relaunch.

### US-CUR-3 — Search for a less-common currency · 🟡 Should
> **As** Carlos ✈️, **I want** to search the currency list, **so that** I can quickly find a specific currency.

- AC1: The "More currencies" sheet is searchable; selecting a row applies and closes in one tap.

> **Planned (post-MVP, per PRD MC section):** per-record currency, manual exchange rate per entry, and
> "original + converted" display. Tracked here for traceability; **not** in MVP scope.
