# Finance Tracker — Project Philosophy

> **Source of truth for product intent and decision-making.**  
> Feature scope, requirements, and roadmap: [`finance_tracker_product.md`](finance_tracker_product.md).  
> Agent workflow and engineering gates: [`../AGENTS.md`](../AGENTS.md).

---

## North Star

**Vision** (from PRD):

> To support tracking and recording of personal finances in the easiest and most effortless way possible.

**Mission in one line:** Build a personal finance notebook that anyone can open, log in seconds, and trust — without banks, accounts, or the internet.

**Android today:** The existing **Pro Expense** app is the first implementation vehicle on `refactor/v2-migration`. The long-term product is a **cross-platform Finance Tracker** (KMP shared logic, Compose on Android, SwiftUI on iOS).

---

## The Problem We Exist to Solve

Most personal finance apps are built for power users: complex dashboards, mandatory bank linking, and constant connectivity. Everyday people who only want to **remember what they spent**, **split a bill**, or **track a trip budget** are forced into tools that feel heavy, invasive, or unreliable offline.

We reject that trade-off.

| Pain we address | Our stance |
|---|---|
| Overwhelming complexity | Radical simplicity — only what helps logging and recall |
| Forced integrations | No bank, card, or third-party account linking |
| Online dependency | Offline-first — core flows work with zero network |
| Trust & privacy | Data stays on device; user owns export/import |

---

## Core Beliefs

These beliefs guide every product and engineering decision. When in doubt, return here.

### 1. Simplicity is a feature

- No clutter, no overwhelming dashboards.
- Every screen should earn its place in the logging or review path.
- Post-MVP ideas (journals, event budgets, debt tracking) must not compromise MVP lean-ness.

### 2. Speed beats completeness at log time

- Logging should take **seconds**, not minutes.
- Quick manual logging is the heartbeat of the app — optimize for under 5 seconds to record amount, category, and move on.
- Voice input and minimal fields are enablers, not afterthoughts.

### 3. Independence from the financial system

- **No dependencies** on banks, aggregators, or account linking.
- The app is a **personal record**, not a live mirror of institutional data.
- This preserves trust for users who are cash-based, privacy-conscious, or globally mobile.

### 4. Personal and private by default

- Feels like **your own notebook**, not a social or analytics product.
- Local storage is the default and source of truth. **Google Drive cloud sync is opt-in** — off
  until the user deliberately connects an account, so nothing leaves the device unless they choose
  it. This is distinct from a server-side identity: the Google Sign-In consent it uses only grants
  access to a private, app-only Drive folder (`drive.appdata` scope, invisible in the user's normal
  Drive UI) — it is not a login/account system for the app itself, and the app never gains a
  server-side identity as a result.
- PIN auth is **local only** (Keystore / Keychain); it protects the device, not a remote identity.
- The remote Drive copy relies on Drive's own storage/transport encryption plus the private
  `appDataFolder` scope, not a second app-level encryption layer like local SQLCipher storage has —
  this trade-off is disclosed to the user before they connect (see `docs/user_stories/sync/`).

### 5. Accessible to everyone

- Not only finance-savvy users — students, housekeepers, retirees, street vendors.
- UI must be obvious, calm, and forgiving; localization and RTL are part of feeling native, not bolted-on translation.

### 6. Global-ready from the start

- Multi-currency is **core**, not an add-on.
- Users log in the currency of the moment; home currency and manual rates keep MVP simple without API lock-in.

### 7. Integrity over growth hacks

- **Integrity** — we do not trade user trust for engagement metrics.
- MVP is **fully free** — no ads, no dark patterns, no pressure to subscribe before value is proven.
- Monetization (freemium) is post-MVP and must not violate offline-first or privacy pillars for free-tier users.

---

## Solution Pillars

How the beliefs show up in the product:

| Pillar | Meaning for users | Meaning for builders |
|---|---|---|
| **Simplicity** | Focused flows, minimal chrome | Fewer modules per screen; no speculative features in MVP |
| **Integrity** | No third-party financial plumbing | No network calls for core logging; no analytics on sensitive fields |
| **Lightweight** | Fast launch, small app, responsive UI | Performance and app size are success metrics (<30MB target) |
| **Offline first** | Works on a plane, in a market, with no signal | Local storage is the source of truth; repositories abstract Room/CoreData; opt-in Drive sync is additive — every core flow works fully with sync off |

---

## Who We Build For

**Primary personas** (MVP — see PRD for full detail):

| Persona | Need we serve first |
|---|---|
| 🎓 Student (Maya) | Quick daily logging, simple records |
| 🏠 Housekeeper (Siti) | Household spending, offline, approachable UI |
| ✈️ Traveler (Carlos) | Multi-currency, trip context, fast on-the-go entry |
| 👫 Cost sharer (Aiko) | Shared costs and settlement without another social app |

**Design implication:** If a feature helps a spreadsheet power user but hurts Maya's 5-second log flow, **Maya wins**.

---

## MVP Philosophy

> Ship the leanest possible version that delivers real value to core users — no bloat, no unnecessary features.

**In scope for MVP** (authoritative list in PRD):

- Quick manual logging
- Multi-currency (basic, manual rates)
- Record history
- Shared costs
- Auth setup (PIN)
- Secure import & export (CSV/JSON)
- Offline-first local storage

**Explicitly out of scope for MVP** — treating these as in-scope is a philosophy violation:

- Bank or third-party integrations
- Social or sharing beyond shared-cost records
- Advanced analytics dashboards
- Push notifications
- Auto exchange rate fetching
- Biometric / social / email login

**Success definition** (from PRD): 10,000 downloads, 60% activation (first record), 40% monthly retention within 3 months of launch.

---

## Decision Framework

Use this when prioritizing work, reviewing PRs, or resolving ambiguity.

### Favor ✅

- Changes that shorten time-to-log or time-to-find a past record
- Offline-capable implementations with clear local data ownership
- KMP-shared business rules with thin platform UI shells
- Privacy-preserving defaults (minimal permissions, no silent exfiltration)
- Accessible, tokenized UI aligned with the design system
- Tests that lock domain rules (amounts, limits, export integrity)

### Reject ❌ (unless PRD and philosophy are explicitly revised)

- Features that require always-on internet for core journaling
- Scope that expands MVP without a tracked post-MVP label
- Bank linking or user accounts "for convenience"; cloud sync that is on by default or required
  to use the app (opt-in Google Drive sync is the one sanctioned exception — see belief #4)
- Dashboards or charts that compete with the logging path for attention
- Hardcoded secrets, PII in logs, or bypassing PIN/storage encryption patterns

### When product and engineering conflict

1. **User trust & privacy** — highest
2. **MVP scope & simplicity** — next
3. **Speed and offline reliability**
4. **Cross-platform consistency** (KMP)
5. **Code elegance** — important, but never above the above

---

## Engineering Philosophy

Product beliefs map to how we build (detailed in `AGENTS.md`):

| Product belief | Engineering expression |
|---|---|
| Simplicity | Small, focused feature modules; compose at UI layer only |
| Integrity | Repository boundary; `Result<T>` errors; no hidden side effects |
| Offline first | `core:storage` local persistence; no sync layer in MVP |
| Global-ready | `Amount` as integer ×100; currency as first-class domain |
| Accessible UI | Compose + design tokens; previews and screenshot verification |
| Maintainability | KMP `commonMain` for rules; MVVM on platform UI |

**Architecture north star:**

```
Compose / SwiftUI  →  ViewModel  →  feature repository  →  local storage
```

Business logic is **precious and portable**; frameworks are **replaceable**.

---

## Competitive Identity

We do not try to out-YNAB YNAB. Our edge is the intersection PRD calls out:

- Offline-first **and** multi-currency **and** shared costs — done simply
- Privacy by design — no accounts, no tracking; cloud sync is opt-in, never required
- KMP cross-platform — one product logic, native feel on each OS
- Global-ready — built for users outside a single banking ecosystem

---

## Document Map

| Question | Read |
|---|---|
| Why does this project exist? | **This file** |
| What exactly do we build? | [`finance_tracker_product.md`](finance_tracker_product.md) |
| How do modules fit together? | [`module_structure.md`](module_structure.md) |
| How do agents implement safely? | [`../AGENTS.md`](../AGENTS.md) |
| How should screens look? | `pro-expense-finance-tracker/project/DESIGN-SYSTEM.md` |

---

*Derived from [`finance_tracker_product.md`](finance_tracker_product.md). Update this philosophy when vision or non-negotiables change; update the PRD when scope or requirements change.*
