# Pro Expense — Hi-Fi Design Specs

Screenshots and per-screen design specs captured from the **Claude Design** handoff bundle (`Hi-Fi Designs.html` · "All Flows"). Every screen was rendered at the native **414 × 868** artboard size (iPhone, @3× / 1242 × 2604 px PNG) and documented against the source prototype.

- **`DESIGN-SYSTEM.md`** — shared foundations: color tokens, typography, icons, categories, buttons, surfaces, motion. Read this first.
- **`screens/<flow>/<screen>/`** — each screen folder holds:
  - `screenshot.png` — the rendered screen
  - `spec.md` — purpose, layout, components & copy, typography/color, states & interactions, implementation notes

58 screens across 8 product flows plus a profile-setup wizard and an edge-cases set.

Source: https://api.anthropic.com/v1/design/h/MCquREs_fZgzjFO9mdiQ7g?open_file=Hi-Fi+Designs.html

## Index

### 01 · First Launch (Flow 04) — splash → onboarding
| Screen | Folder |
|---|---|
| Splash | `screens/01-first-launch/01-splash/` |
| Onboarding · Welcome | `screens/01-first-launch/02-onb-1/` |
| Onboarding · Quick Log | `screens/01-first-launch/03-onb-2/` |
| Onboarding · Shared Costs | `screens/01-first-launch/04-onb-3/` |
| Onboarding · Event Budget | `screens/01-first-launch/05-onb-4/` |
| Onboarding · Journal | `screens/01-first-launch/06-onb-5/` |

### 02 · Profile Setup (Flow 04) — first-run personalization
| Screen | Folder |
|---|---|
| Profile name | `screens/02-profile-setup/01-prof-name/` |
| Home currency | `screens/02-profile-setup/02-prof-currency/` |
| Currency picker (sheet) | `screens/02-profile-setup/03-prof-currency-sheet/` |

### 03 · PIN Auth (Flow 05)
| Screen | Folder |
|---|---|
| PIN Setup | `screens/03-pin-auth/01-pin-setup/` |
| Security question | `screens/03-pin-auth/02-pin-security/` |
| PIN Entry · 4 of 6 | `screens/03-pin-auth/03-pin-entry/` |
| PIN Entry · Lockout | `screens/03-pin-auth/04-pin-lock/` |

### 04 · Quick Log (Flow 01)
| Screen | Folder |
|---|---|
| Home · empty | `screens/04-quick-log/01-home-empty/` |
| Home · Casual | `screens/04-quick-log/02-home-casual/` |
| Home · Budget Planner | `screens/04-quick-log/03-home-budget/` |
| Home · Event Organizer | `screens/04-quick-log/04-home-event/` |
| Add · $0 validation | `screens/04-quick-log/05-add-zero/` |
| Add · amount typed | `screens/04-quick-log/06-add-amount/` |
| Add · details (@ tag) | `screens/04-quick-log/07-add-details/` |

### 05 · Browse Journal (Flow 02)
| Screen | Folder |
|---|---|
| Journal · date-grouped | `screens/05-journal/01-journal-list/` |
| Journal Detail | `screens/05-journal/02-journal-detail/` |
| Detail · edit / delete sheet | `screens/05-journal/03-journal-actions/` |

### 06 · More (Flow 03)
| Screen | Folder |
|---|---|
| More (hub) | `screens/06-more/01-more-hub/` |
| Reports · monthly | `screens/06-more/02-reports/` |
| Category List | `screens/06-more/03-categories/` |
| Currency setting | `screens/06-more/04-more-currency/` |
| Data Export | `screens/06-more/05-more-export/` |
| Clear Data | `screens/06-more/06-more-clear/` |

### 07 · Event Budget (Flow 06)
| Screen | Folder |
|---|---|
| Events · empty | `screens/07-event-budget/01-event-empty/` |
| Create event · filled | `screens/07-event-budget/02-event-create/` |
| Event Budget · list | `screens/07-event-budget/03-event-list/` |
| Event Detail · Bali Trip | `screens/07-event-budget/04-event-detail/` |

### 08 · Debt & Lending (Flow 07)
| Screen | Folder |
|---|---|
| Debt · I Lent | `screens/08-debt-lending/01-debt-lent/` |
| Add Record | `screens/08-debt-lending/02-debt-add/` |
| Detail · Lent · John | `screens/08-debt-lending/03-debt-lent-detail/` |
| Debt · I Owe | `screens/08-debt-lending/04-debt-owe/` |
| Detail · Owe · David | `screens/08-debt-lending/05-debt-owe-detail/` |

### 09 · Shared Costs (Flow 08)
| Screen | Folder |
|---|---|
| Split a bill · equal | `screens/09-shared-costs/01-shared-input/` |
| Split summary | `screens/09-shared-costs/02-shared-summary/` |
| Shared Costs · history | `screens/09-shared-costs/03-shared-history/` |

### 10 · Edge Cases (all flows)
| Screen | Folder |
|---|---|
| Draft restore | `screens/10-edge-cases/01-edge-draft/` |
| Note at 200 cap | `screens/10-edge-cases/02-edge-note/` |
| @ tag mutual-exclusion | `screens/10-edge-cases/03-edge-tag/` |
| Journal · no results | `screens/10-edge-cases/04-edge-search/` |
| Long-press quick note | `screens/10-edge-cases/05-edge-quicknote/` |
| Reports · all uncategorized | `screens/10-edge-cases/06-edge-reports-unc/` |
| Category · duplicate name | `screens/10-edge-cases/07-edge-cat-dup/` |
| PIN · mismatch | `screens/10-edge-cases/08-edge-pin-mismatch/` |
| PIN · incorrect | `screens/10-edge-cases/09-edge-pin-wrong/` |
| PIN · recovery | `screens/10-edge-cases/10-edge-pin-recovery/` |
| Event · $0 + duplicate | `screens/10-edge-cases/11-edge-event-errors/` |
| Event · over budget (105%) | `screens/10-edge-cases/12-edge-event-warn/` |
| Event · closed (read-only) | `screens/10-edge-cases/13-edge-event-closed/` |
| Debt · opposite-side warning | `screens/10-edge-cases/14-edge-debt-conflict/` |
| Debt · delete settled | `screens/10-edge-cases/15-edge-debt-settled/` |
| Shared · $0 total | `screens/10-edge-cases/16-edge-shared-zero/` |
| Shared · max 20 + custom | `screens/10-edge-cases/17-edge-shared-limits/` |

---

*Captured from the Pro Expense Hi-Fi prototype. Screenshots are renders of the HTML/CSS/JS design at the final resting state (entrance/feedback animations frozen).*
