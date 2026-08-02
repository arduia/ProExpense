# Pro Expense — Android Screen Specs

Per-screen specifications for the Finance Tracker MVP, built for a **Jetpack Compose** implementation. Each screen pairs its captured **414 × 868 dp** artboards with expected behaviors, component composition (+ Material 3 mapping), applied design tokens, and edge / error states.

- **Foundations:** [`../tokens.md`](../tokens.md) — color, type, shape, spacing, motion, M3 mapping.
- **Components:** [`../components/`](../components/) — per-component specs.
- **Screenshots:** [`../screenshots/screens/`](../screenshots/screens/) — one PNG per state, named by state id.
- **Conventions:** **light + dark** themes · 1 px = 1 dp · display/amounts **Prompt SemiBold** · on-filled text white.

> **Visual system: Blue Banking.** The adopted style is the *Blue Banking* Hi-Fi variant (Claude
> Design canvas "Pro Expense - Finance Tracker", `Hi-Fi Variant - Blue Banking.html` /
> `variant-blue-*.jsx`): gradient hero headers with overlapping 26 dp-radius content sheets,
> gold accent family, navy-tinted dark palette, restyled bottom nav with a 64 dp center Add
> button. The per-screen `.md` files and PNGs below still show the previous skin — layout,
> flows, states, and behaviors remain authoritative, but where their *visual* styling conflicts
> with [`../tokens.md`](../tokens.md) or the Blue Banking canvas, the canvas wins. Screen specs
> and captures are re-synced screen-by-screen as the restyle lands.

---

## Screens

| # | Screen | Flow | States |
|---|---|---|---|
| 01 | [Splash](01-splash.md) | First launch | 1 |
| 02 | [Onboarding](02-onboarding.md) | First launch | 5 |
| 02·P | [Profile Setup](02P-profile-setup.md) | First launch · between Onboarding & Home | 3 |
| 03 | [Home](03-home.md) | Central hub | 4 |
| 04 | [Add Expense](04-add-expense.md) | Quick Log · core | 6 |
| 05 | [Journal](05-journal.md) | Browse history | 3 |
| 06 | [Journal Detail](06-journal-detail.md) | Browse history | 2 |
| 07 | [Event Budget](07-event-budget.md) | Events | 4 |
| 08 | [Event Detail](08-event-detail.md) | Events | 3 |
| 09 | [Debt Tracker](09-debt-tracker.md) | Debt & lending | 7 |
| 10 | [Shared Costs](10-shared-costs.md) | Bill splitting | 5 |
| 11 | [Category List](11-category-list.md) | More · management | 2 |
| 12 | [Reports](12-reports.md) | More · insight | 2 |
| 13 | [More / Settings](13-more.md) | Hub | 4 |
| 14 | [PIN Setup](14-pin-setup.md) | Security | 4 |
| 15 | [PIN Entry](15-pin-entry.md) | Security | 3 |

_16 screens · 58 states & edge cases total._
