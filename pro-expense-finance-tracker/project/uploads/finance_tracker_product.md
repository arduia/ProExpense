# Finance Tracker — Product Document

## Product Vision

> To support tracking and recording of personal finances in the easiest and most effortless way possible.

### Core Principles

- **Simplicity first** — no clutter, no overwhelming dashboards
- **Speed** — logging should take seconds, not minutes
- **No dependencies** — works without bank integrations or account linking
- **Personal & private** — feels like your own notebook
- **Accessible to everyone** — not just finance-savvy users
- **Global-ready** — multi-currency support for users worldwide

---

## Problem Statement

> *"Most personal finance apps are overly complex, require bank or third-party integrations, and depend heavily on internet connectivity. This creates friction and trust issues for everyday users who simply want a lightweight, private, and reliable way to record and track their finances — anytime, anywhere, without complexity."*

### Core Pain Points
- Too complex — overwhelming features and dashboards
- Forced integrations — banks, cards, third-party services
- Online dependency — doesn't work well offline
- Trust & privacy concerns — sensitive financial data shared with services

### Our Solution Pillars
- ✅ **Simplicity** — minimal, focused experience
- ✅ **Integrity** — no third-party integrations, your data stays yours
- ✅ **Lightweight** — fast, lean, no bloat
- ✅ **Offline First** — fully functional without internet

---

## Use Cases (Casual Records)

### 1. Quick Manual Logging
One-tap expense entry with minimal fields. Log in under 5 seconds — amount, category, done. Supports voice input and fast record-keeping on the go.

### 2. Financial Journal
A diary-like experience where users jot down daily spending. Add notes or context to purchases and review finances like a personal journal.

### 3. Event Budget
Set a budget for a specific event — vacation, wedding, party. Log every expense during that period and see the remaining balance in real time.

### 4. Debt and Lending Tracker
Simple records of money lent or owed. "I lent John $50" or "I owe Sarah $30." No bank connection needed — just personal records between people.

### 5. Shared Costs
Split and track shared expenses with friends, roommates, or travel companions. Simple, lightweight, and without needing a third-party app.

### 6. Secure Import & Export
Export data anytime as CSV, JSON, or PDF. Import from a backup or another device with no cloud dependency. Supports encrypted exports for sensitive financial data, ensuring full data ownership and portability.

### 7. Localization Experience
Full app localization to support users worldwide. Includes translated UI, local date and number formats, region-specific currency defaults, and right-to-left (RTL) language support. The app should feel native to the user's region, not just translated.

### 8. Record History
View a chronological list of all logged entries. Filter and search by date, category, or currency. Edit or delete past records with daily, weekly, and monthly summary views. Works fully offline — all history stored locally on device.

### 9. Auth Setup
PIN/Passcode-based authentication to secure access to the app. Optional but recommended on first launch. No account or server required — auth is fully local and offline. Protects sensitive financial data in case of device loss or shared device usage.

---

## Target Audience

| # | User | Core Need |
|---|---|---|
| 1 | 🎓 **The Student** | Track daily spending and stay within budget |
| 2 | 🏠 **The Housekeeper** | Manage household budget and track lent money |
| 3 | ✈️ **The Traveler** | Set and stick to a trip budget, multi-currency |
| 4 | 👫 **The Cost Sharer** | Split and track shared expenses with friends |

### Use Case Mapping

| User | Quick Log | Journal | Event Budget | Debt & Lending | Shared Costs | Multi-Currency |
|---|---|---|---|---|---|---|
| Student | ✅ | ✅ | | ✅ | | |
| Housekeeper | ✅ | ✅ | | ✅ | | |
| Traveler | ✅ | | ✅ | | | ✅ |
| Cost Sharer | ✅ | | ✅ | | ✅ | |

---

## User Personas

### 1. 🎓 The Student
- **Maya, 21** — University student in Jakarta
- Receives monthly allowance from parents
- Spends on food, transport, and hangouts
- Often lends small amounts to friends and forgets
- Needs: simple daily logging, debt reminders

### 2. 🏠 The Housekeeper
- **Siti, 38** — Stay-at-home mom in Kuala Lumpur
- Manages grocery, utility, and school budgets
- Lends money to relatives frequently
- Not very tech-savvy, needs simple UI
- Needs: budget tracking, lending records, offline access

### 3. ✈️ The Traveler
- **Carlos, 29** — Freelance photographer from Spain
- Travels 3–4 countries per year
- Deals with multiple currencies constantly
- Hates complex apps, wants quick logging on the go
- Needs: trip budget, multi-currency, offline-first

### 4. 👫 The Cost Sharer
- **Aiko & Friends, 25** — Young professionals in Tokyo
- Share rent, dinners, and weekend trips
- Currently use messy group chats to split costs
- Needs: shared cost tracking, simple settlement summary

### 5. 💼 The Freelancer
- **Raj, 32** — Graphic designer in Mumbai
- Irregular income from multiple clients
- Needs to track what he's owed and what he owes
- Worries about overspending in slow months
- Needs: income logging, debt tracking, budget alerts

### 6. 🧳 The Expat
- **Sophie, 34** — French teacher living in Dubai
- Earns in AED, sends money home in EUR
- Tracks living expenses separately from savings
- Needs: multi-currency, simple daily logging

### 7. 🎉 The Event Organizer
- **James, 27** — Plans group trips and parties for his friend circle
- Collects money from multiple people
- Needs to track who paid and what's left
- Needs: event budget, shared costs, lending tracker

### 8. 👴 The Retiree
- **Mr. Chen, 62** — Retired in Taipei
- Lives on fixed pension, very budget conscious
- Not tech-savvy, needs large text and simple UI
- Needs: daily logging, budget limits, localization

### 9. 🛒 The Small Vendor
- **Amara, 35** — Street food seller in Nairobi
- Tracks daily cash in and out manually
- No bank account, fully cash-based
- Needs: offline-first, quick logging, simple records

### Persona Priority

| Priority | Persona | Reason |
|---|---|---|
| ✅ Core | Maya, Siti, Carlos, Aiko | Clear needs, maps directly to MVP use cases |
| 🔜 Next | Raj, Sophie, James | Natural extension of core features |
| 🔮 Later | Mr. Chen, Amara | Require accessibility and deeper localization |

---

## MVP Definition

### Philosophy
Ship the leanest possible version that delivers real value to core users — no bloat, no unnecessary features.

### MVP Scope

| Use Case | MVP | Post-MVP |
|---|---|---|
| Quick Manual Logging | ✅ | |
| Multi-Currency (basic) | ✅ | |
| Secure Import & Export | ✅ | |
| Record History | ✅ | |
| Shared Costs | ✅ | |
| Auth Setup (PIN) | ✅ | |
| Financial Journal | | 🔜 |
| Event Budget | | 🔜 |
| Debt & Lending Tracker | | 🔜 |
| Localization | | 🔜 |

### MVP Core Requirements
- **Offline-first** — all features work fully without internet connection
- **Core features only** — no nice-to-haves, no distractions
- **Quick Manual Logging** — fastest possible expense entry experience
- **Multi-Currency (basic)** — log in any currency, set a home currency, manual exchange rates
- **Secure Import & Export** — export as CSV/JSON, import from backup, local file transfer
- **Record History** — view, filter, edit and delete past entries by date, category, or currency

- **Auth Setup (PIN)** — optional but recommended PIN/passcode, fully local and offline, no account needed

### Out of Scope for MVP
- Bank or third-party integrations
- Cloud sync or online backup
- Social or sharing features
- Advanced analytics or dashboards
- Push notifications
- Auto exchange rate fetching
- Biometric auth, social login, or email/password (post-MVP)

### Target Personas for MVP
| Persona | Served by MVP |
|---|---|
| 🎓 Maya (Student) | ✅ Quick logging + export |
| 🏠 Siti (Housekeeper) | ✅ Quick logging + multi-currency |
| ✈️ Carlos (Traveler) | ✅ Multi-currency + offline + export |
| 👫 Aiko (Cost Sharer) | ✅ Shared costs in MVP |

---

## Supported Platforms

| Platform | Support |
|---|---|
| iOS (iPhone & iPad) | ✅ |
| Android | ✅ |

### Platform Considerations
- **Native feel** — UI/UX should follow iOS (Human Interface Guidelines) and Android (Material Design) conventions
- **Offline first** — all core features work without internet connection
- **Cross-device sync** — data syncs across user's iOS and Android devices
- **App Store & Google Play** — distributed through official stores

---

## Multi-Currency (MC) Considerations

Since the product targets a global audience, multi-currency support is a core requirement — not an add-on.

### Key Requirements

- **Default currency setting** — user picks their home currency on setup
- **Per-record currency** — each entry can be logged in any currency
- **Manual exchange rates** — user inputs the rate at time of transaction (keeps it simple, no API needed)
- **Optional auto rates** — fetch live rates for users who want convenience
- **Currency display** — always show original currency + converted amount
- **Supported scope** — cover major world currencies (USD, EUR, GBP, JPY, INR, AED, etc.)

### Impact on Use Cases

| Use Case | MC Consideration |
|---|---|
| Quick Manual Logging | Log in any currency, auto-converts to home currency |
| Financial Journal | Entries show original + home currency |
| Event Budget | Set budget in one currency, log expenses in mixed currencies |
| Debt & Lending Tracker | Record debts in the currency they occurred |
| Shared Costs | Each participant can log in their local currency |

---

## Feature List (MVP)

### 1. Quick Manual Logging

| # | Feature | Description | Priority |
|---|---|---|---|
| 1 | Quick Entry | Log an expense in under 5 seconds — amount, category, date, note | 🔴 Must Have |
| 2 | Category Management | Default categories with ability to add custom ones | 🔴 Must Have |
| 3 | Edit & Delete Record | Modify or remove any past entry | 🔴 Must Have |

### 2. Multi-Currency

| # | Feature | Description | Priority |
|---|---|---|---|
| 4 | Multi-Currency Logging | Log each entry in any currency | 🔴 Must Have |
| 5 | Home Currency Setting | Set a default home currency on setup | 🔴 Must Have |
| 6 | Manual Exchange Rate | Enter exchange rate manually per entry | 🔴 Must Have |

### 3. Record History

| # | Feature | Description | Priority |
|---|---|---|---|
| 7 | Record List View | Chronological list of all logged entries | 🔴 Must Have |
| 8 | Filter & Search | Filter records by date, category, currency | 🔴 Must Have |
| 9 | Summary View | Daily, weekly, monthly spending summary | 🟡 Should Have |

### 4. Shared Costs

| # | Feature | Description | Priority |
|---|---|---|---|
| 10 | Shared Cost Entry | Log an expense split between people | 🔴 Must Have |
| 11 | Participant Management | Add people to a shared cost entry | 🔴 Must Have |
| 12 | Settlement Summary | View who owes what in a shared cost | 🟡 Should Have |

### 5. Auth Setup

| # | Feature | Description | Priority |
|---|---|---|---|
| 13 | PIN Setup | Set a PIN to lock the app | 🟡 Should Have |
| 14 | PIN Authentication | Prompt PIN on app launch or resume | 🟡 Should Have |

### 6. Secure Import & Export

| # | Feature | Description | Priority |
|---|---|---|---|
| 15 | Export Data | Export all records as CSV or JSON | 🔴 Must Have |
| 16 | Import Data | Import records from a CSV or JSON file | 🔴 Must Have |

### 7. Foundation

| # | Feature | Description | Priority |
|---|---|---|---|
| 17 | Local Storage | All data stored locally on device | 🔴 Must Have |
| 18 | Offline Mode | Full functionality without internet | 🔴 Must Have |

### Future-Compatible Design Notes
- **Categories** → extensible for budgets and journals
- **Multi-currency** → ready for auto rate fetching post-MVP
- **Participant management** → extensible for group and social features
- **Local storage** → ready for optional cloud sync layer
- **Export/Import** → foundation for migration, backup and cross-device support

---

## Tech Stack

### Core
| Layer | Technology |
|---|---|
| Framework | Kotlin Multiplatform (KMP) — shared business logic across iOS and Android |
| iOS UI | SwiftUI |
| Android UI | Jetpack Compose |

### Data & Storage
| Layer | Technology |
|---|---|
| Android DB | Room |
| iOS DB | CoreData |
| Shared Logic | KMP common layer for data models and business rules |

### Multi-Currency
| Layer | Technology |
|---|---|
| Exchange Rates | Manual input (MVP) — open exchange rate API ready for post-MVP |
| Currency Formatting | KMP-compatible currency formatting library |

### Security
| Layer | Technology |
|---|---|
| PIN Storage | Android Keystore + iOS Keychain |
| Data Encryption | SQLCipher (Android) + CoreData encryption (iOS) |

### Import & Export
| Layer | Technology |
|---|---|
| Formats | CSV, JSON |
| Transfer | Local file system, AirDrop (iOS), Files app (iOS), Android Sharesheet |

### Backend / Cloud
| Layer | Technology |
|---|---|
| MVP | None — fully local |
| Post-MVP | TBD — optional cloud sync layer |

---

## Monetization

### MVP Phase — Fully Free
- All 6 MVP use cases fully free
- No ads during MVP phase
- Focus on user acquisition and feedback
- Build trust with the core personas

### Post-MVP — Freemium Model

| Feature | Free | Premium |
|---|---|---|
| Quick Manual Logging | ✅ | ✅ |
| Record History (30 days) | ✅ | ✅ |
| Record History (unlimited) | | ✅ |
| Single Currency | ✅ | ✅ |
| Multi-Currency | | ✅ |
| Basic Categories | ✅ | ✅ |
| Custom Categories | | ✅ |
| Shared Costs | | ✅ |
| Export (CSV, 100 records) | ✅ | ✅ |
| Export (full, encrypted) | | ✅ |
| Import | | ✅ |
| PIN / Auth | | ✅ |
| Ads | Non-intrusive | ❌ None |

### Pricing (Post-MVP)
| Plan | Price |
|---|---|
| 🎯 Annual | $9.99/year |
| 🔄 Monthly | $1.99/month |
| 💳 Lifetime | $4.99 one-time |

---

## Success Metrics

### Acquisition
| Metric | Target |
|---|---|
| Total Downloads (iOS + Android) | 10,000 in first 3 months |
| Install to First Record Rate | 60%+ activation rate |

### Engagement
| Metric | Target |
|---|---|
| Daily Active Users (DAU) | Growing week-on-week |
| Monthly Active Users (MAU) | Growing month-on-month |
| DAU/MAU Ratio (Stickiness) | 30%+ |
| Avg Records Logged per User/Week | 5+ |

### Retention
| Metric | Target |
|---|---|
| Monthly Retention Rate | 40%+ |

### Quality
| Metric | Target |
|---|---|
| App Store Rating | 4.5+ |
| Crash Rate | <1% |
| App Size | <30MB |

### Post-MVP Revenue
| Metric | Description |
|---|---|
| Free to Premium Conversion | 5%+ of active users |
| Monthly Recurring Revenue (MRR) | Track post-freemium launch |
| Annual Recurring Revenue (ARR) | Track post-freemium launch |
| Lifetime Value (LTV) | Per user revenue over time |

### MVP Success Definition
> The MVP is successful if within 3 months of launch it achieves **10,000 downloads**, **60% activation rate**, and **40% monthly retention**.

---

## Competitive Landscape

### Key Competitors

| App | Strengths | Weaknesses | Our Differentiation |
|---|---|---|---|
| **YNAB** | Strong budgeting, zero-based method | Complex, subscription-heavy, requires bank linking | Simpler, no linking, offline-first |
| **Monarch Money** | Great reporting, category flexibility | Online-dependent, subscription only | Offline-first, lightweight, no subscription for MVP |
| **Quicken Simplifi** | Investment tracking, cash flow | Desktop-heavy, expensive, complex | Mobile-first, casual users focused |
| **Spendee** | Clean UI, multi-currency | Limited offline, freemium limits | Stronger offline, deeper multi-currency |
| **Pocket Clear** | Fully offline, no bank linking, free, no ads | Limited shared costs, basic features | Shared costs, multi-currency, import/export |
| **AndroMoney** | Multi-currency, CSV export, loyal user base | Not as polished, Android-only | Cross-platform (iOS + Android), modern UI |
| **Zero App** | Privacy-first, fully offline, no cloud, no tracking | Limited shared costs features | Shared costs, localization, KMP tech |

### Our Competitive Edge
- ✅ **Offline-first + multi-currency + shared costs** — no competitor does all three well
- ✅ **KMP cross-platform** — consistent iOS and Android experience
- ✅ **Privacy by design** — no accounts, no cloud, no tracking
- ✅ **Global-ready** — localization from day one
- ✅ **Freemium MVP** — fully free to start, no pressure

---

## Roadmap

### Timeline Overview

| Phase | Timeline | Focus |
|---|---|---|
| 🚀 Phase 1 — MVP | Month 1–2 | Build & Launch |
| 📈 Phase 2 — Growth | Month 3–6 | Expand & Monetize |
| 🌍 Phase 3 — Scale | Month 7–12 | Scale & Polish |

### Phase 1 — MVP (Month 1–2)
- Quick Manual Logging
- Record History
- Multi-Currency (basic, manual rates)
- Shared Costs
- Auth Setup (PIN)
- Secure Import & Export
- Offline-first foundation
- App Store & Google Play launch

### Phase 2 — Growth (Month 3–6)
- Financial Journal
- Event Budget
- Debt & Lending Tracker
- Localization (first 5 languages)
- Biometric Auth (Face ID / Fingerprint)
- Auto Exchange Rates (API integration)
- Freemium model launch + ads
- User feedback & bug fixes

### Phase 3 — Scale (Month 7–12)
- Full Localization (10+ languages)
- Cloud Sync (optional)
- Advanced Analytics & Reports
- Premium subscription launch
- Wider currency support
- Performance & accessibility improvements
- Community & referral program

