# Pro Expense — Screen Flows

Navigation map for the MVP. Screen ids match `screens-manifest.yaml`; every node has reference images in `reference-images/`.

## First-launch path

```
Splash
  └─→ Onboarding (5 slides: welcome → quick-log → shared-costs → event-budget → get-started)
        └─→ Profile name
              └─→ Profile currency (+ picker sheet)
                    └─→ PIN setup (+ required security question)
                          └─→ Home  ── enters the app shell ──┐
```

Returning launch: `Splash → PIN entry (or biometric) → Home`. Lockout after 5 wrong PINs (30s countdown); Forgot PIN → security-question recovery.

## App shell (bottom-nav tabs)

The floating `HomeBottomNav` is persistent across the four tab roots, with a raised **Add** FAB in the center.

```
        ┌──────────── HomeBottomNav ────────────┐
   [ Home ]   [ Budget ]   (＋ Add)   [ Journal ]   [ More ]
        │          │           │           │           │
        ▼          ▼           ▼           ▼           ▼
     Home     Event list   AddAmount   Journal     More hub
              (Budget)         │        list           │
                               ▼                        │
                          AddDetails                    │
                          (note, date, @tag)            │
                               ▼                        │
                          Save → Toast → Home/Journal   │
```

> **Budget tab** opens the Event Budget flow (flow-06). **Add** (＋) opens Quick Log (flow-01) as a forward push over the current tab.

## Flow 01 — Quick Log  (＋ Add)
```
AddAmount  ──Next──▶  AddDetails  ──Save──▶  Toast ──▶ back to origin tab
   │  (amount-first, keypad auto-opens)          ▲
   └──Save (quick-log, skips details)────────────┘
```
States: `add-zero` (validation) · `add-amount` (typed) · `add-details` · edge: note cap, @tag exclusion, draft restore.

## Flow 02 — Browse Journal  (Journal tab)
```
Journal list ──tap row──▶ Journal detail ──⋯──▶ Edit / Delete (action sheet)
   │  (search + filter chips)
   └── no-results state
```

## Flow 03 — More  (More tab)
```
More hub ──┬─▶ Reports (monthly · uncategorized)
           ├─▶ Categories (list · add · duplicate error)
           ├─▶ Currency setting (picker sheet)
           ├─▶ Data export
           └─▶ Clear data (destructive confirm)
```

## Flow 06 — Event Budget  (Budget tab)
```
Event list ──┬── empty
             ├── active (3 events, 1 over) ──tap──▶ Event detail (active · closed)
             └──＋ New──▶ Event create (filled · errors) ──Save──▶ Event list
```

## Flow 07 — Debt & Lending  (from Home quick-access "Debt")
```
Debt tracker [ Lent | Owe ] ──tap──▶ Debt detail ──Mark settled / Delete──▶ back
        │
        └──＋ Add──▶ Debt add (sheet) ──Save──▶ Debt tracker
```

## Flow 08 — Shared Costs  (from Home quick-access "Split")
```
Split input (equal · custom · up to 20) ──Calculate──▶ Split summary ──Save──▶ Shared history
        │ (zero-total guard)
```

## Entry points from Home quick-access tiles
`Reports` → flow-03 Reports · `Debt` → flow-07 · `Split` → flow-08 · `Events` → flow-06.

---
*All screens render at 414 × 868 dp. Forward transitions slide-in-right (280ms), back slide-in-left, sheets rise (340ms) — see `design-tokens.json` → motion.*
