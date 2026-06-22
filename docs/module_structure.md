# KMP Module Structure

Authoritative map for the Finance Tracker KMP multi-module project. Aligned with
`docs/finance_tracker_product.md` MVP scope.

## Layer Overview

```
app/                         Android Compose shell (UI only)
shared/                      Platform utilities (expect/actual)
core/
├── domain/                  Shared domain models
├── data/                    Repository contracts, Result wrapper
└── storage/                 Local persistence contracts (offline-first)
feature/
├── logging/                 Quick Manual Logging (MVP)
├── currency/                Multi-Currency (MVP)
├── history/                 Record History (MVP)
├── sharedcost/              Shared Costs (MVP)
├── auth/                    PIN Auth (MVP)
├── importexport/            Secure Import & Export (MVP)
├── debt/                    Debt & Lending Tracker (Phase 2 UI)
├── eventbudget/             Event Budget (Phase 2 UI)
├── reports/                 Spending reports (Phase 2 UI)
├── categories/              Category management (Phase 2 UI)
└── onboarding/              First-launch onboarding flow
iosApp/                      SwiftUI shell (future)
```

## Dependency Rules

| Module | May depend on |
|--------|---------------|
| `app` | all `core:*`, all `feature:*`, `shared` |
| `shared` | nothing (project modules) |
| `core:domain` | `shared` |
| `core:data` | `core:domain`, `shared` |
| `core:storage` | `core:domain`, `shared` |
| `feature:*` | `core:domain`, `core:data`, `shared` (`importexport` also uses `core:storage`) |
| `feature:*` | **must not** depend on other `feature:*` modules |

## MVP Feature Mapping (PRD)

| PRD Use Case | Module | Key contracts |
|--------------|--------|---------------|
| Quick Manual Logging | `:feature:logging` | `LoggingRepository`, `LogRecordInput` |
| Multi-Currency | `:feature:currency` | `CurrencyRepository`, `CurrencySettings` |
| Record History | `:feature:history` | `HistoryRepository`, `RecordHistoryFilter` |
| Shared Costs | `:feature:sharedcost` | `SharedCostRepository`, `SettlementSummary` |
| Auth Setup (PIN) | `:feature:auth` | `PinAuthRepository` |
| Secure Import & Export | `:feature:importexport` | `ImportExportRepository`, `ExportFormat` |
| Local Storage / Offline | `:core:storage` | `LocalDataStore` |

## Shared Domain Models (`core:domain`)

- `Amount` — money stored as integer cents (max 999,999,999.99)
- `FinanceRecord` — core expense/income entry
- `Category`, `CurrencyCode`, `Participant`, `SharedCost`
- `RecordType` — expense vs income

## Post-MVP Features (not scaffolded yet)

Add as new `feature:*` modules when implementing Phase 2:

- `feature:journal` — Financial Journal *(UI lives in `:feature:history` today)*
- `feature:localization` — Localization experience

Phase 2 UI modules already scaffolded: `feature:debt`, `feature:eventbudget`, `feature:reports`, `feature:categories`, `feature:onboarding`.

## Platform Implementations (upcoming)

| Layer | Android | iOS |
|-------|---------|-----|
| Database | Room (`core:storage` androidMain) | CoreData (`iosMain`) |
| PIN | Keystore (`feature:auth` androidMain) | Keychain (`iosMain`) |
| UI | Jetpack Compose (`app`) | SwiftUI (`iosApp`) |
