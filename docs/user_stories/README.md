# Finance Tracker — User Stories

> Agile user stories for every service (feature module) and screen of the Finance Tracker MVP
> (Android ships as **Pro Expense**). Derived from the PRD
> ([`../finance_tracker_product.md`](../finance_tracker_product.md)) and the user journey captured in
> the screen specs ([`../../design-system-spec/screens/`](../../design-system-spec/screens/)).
>
> One folder per feature. Each story lives in its own file (`US-<SERVICE>-<n>.md`), following
> [`TEMPLATE.md`](TEMPLATE.md); each feature folder's `user-stories.md` is an index table linking to
> its story files. This fulfils Stage 3 → *"Write user stories for all MVP use cases"* in the PRD
> roadmap.

---

## How to read these documents

**Story format** — see [`TEMPLATE.md`](TEMPLATE.md) for the canonical structure every story file
follows.

> **As** \<persona>, **I want to** \<goal>, **so that** \<benefit>.

Each story file has a stable **ID** (`US-<SERVICE>-<n>`), a **priority** and **status** in its
header, and **acceptance criteria** written as numbered Given/When/Then **Scenario** blocks,
grounded in the screen behaviors and edge cases so they are testable.

**Priority** (from PRD Feature List):

| Tag | Meaning |
|---|---|
| 🔴 Must | MVP-blocking — core value |
| 🟡 Should | High value, ships if capacity allows |
| 🔵 Phase 2 | Post-MVP per roadmap (Journal extras, Event Budget, Debt, Localization, Biometric) |

**Personas** (PRD): 🎓 Maya (Student) · 🏠 Siti (Housekeeper) · ✈️ Carlos (Traveler) ·
👫 Aiko (Cost Sharer) · 💼 Raj (Freelancer) · 🧳 Sophie (Expat) · 🎉 James (Event Organizer) ·
👴 Mr. Chen (Retiree) · 🛒 Amara (Vendor). "Any user" is used when the story is persona-agnostic.

---

## Index — Service → Screens → Use Case

| Service (`feature:*`) | Stories | Screens | PRD use case |
|---|---|---|---|
| [onboarding](onboarding/user-stories.md) | ONB-1…5 | 01 Splash · 02 Onboarding · 02P Profile Setup | First-launch / setup |
| [logging](logging/user-stories.md) | LOG-1…7 | 04 Add Expense | Quick Manual Logging |
| [history](history/user-stories.md) | HIS-1…8 | 05 Journal · 06 Journal Detail | Record History / Financial Journal |
| [currency](currency/user-stories.md) | CUR-1…4 | 02P Currency picker · 13 More → Currency · 04 Add Expense | Multi-Currency |
| [eventbudget](eventbudget/user-stories.md) | EVT-1…5 | 07 Event Budget · 08 Event Detail | Event Budget |
| [debt](debt/user-stories.md) | DEBT-1…4 | 09 Debt Tracker | Debt & Lending Tracker |
| [sharedcost](sharedcost/user-stories.md) | SHC-1…5 | 10 Shared Costs | Shared Costs |
| [categories](categories/user-stories.md) | CAT-1…3 | 11 Category List | Category Management |
| [reports](reports/user-stories.md) | REP-1…4 | 12 Reports | Record History → Summary |
| [auth](auth/user-stories.md) | AUTH-1…8 | 14 PIN Setup · 15 PIN Entry | Auth Setup (PIN) |
| [importexport](importexport/user-stories.md) | IE-1…2 | 13 More → Data export | Secure Import & Export |
| [sync](sync/user-stories.md) | SYNC-1…7 | 13 More → Google Drive Sync | Google Drive Cloud Sync (opt-in) |
| [app-shell](app-shell/user-stories.md) | HOME-1…4 · MORE-1…4 | 03 Home · 13 More | Central hub / Foundation |

---

## Traceability summary

| Service | Stories | MVP (🔴/🟡) | Phase 2 (🔵) |
|---|---|---|---|
| onboarding | ONB-1…5 | 5 | — |
| logging | LOG-1…7 | 6 | 1 |
| history | HIS-1…8 | 7 | 1 |
| currency | CUR-1…4 | 4 | — |
| eventbudget | EVT-1…5 | — | 5 |
| debt | DEBT-1…4 | — | 4 |
| sharedcost | SHC-1…5 | 5 | — |
| categories | CAT-1…3 | 3 | — |
| reports | REP-1…4 | 4 | — |
| auth | AUTH-1…8 | 7 | 1 |
| importexport | IE-1…2 | 2 | (encryption planned) |
| sync | SYNC-1…7 | 6 | (SYNC-7 🟡 Should) |
| app-shell / settings | HOME-1…4, MORE-1…4 | 7 | 1 |

> Stories trace to PRD use cases and the screen-spec user journey. Phase tags follow the PRD roadmap:
> Journal extras, Event Budget, Debt & Lending, Localization, and Biometric are Phase 2, but their
> screens exist in this build, so their stories are documented here for completeness.
