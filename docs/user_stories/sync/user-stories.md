# Google Drive Cloud Sync — User Stories

> Service: `feature:sync` · Screen: 13 More → Google Drive Sync (new)
> PRD use case: Google Drive Cloud Sync (opt-in, MVP). Legend & format:
> [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).
>
> Each story lives in its own file (one story per document). This page indexes them.

| ID | Title | Priority |
|---|---|---|
| [US-SYNC-1](US-SYNC-1.md) | Connect my Google Drive account | 🔴 Must |
| [US-SYNC-2](US-SYNC-2.md) | First sync after connecting | 🔴 Must |
| [US-SYNC-3](US-SYNC-3.md) | My edits push to Drive | 🔴 Must |
| [US-SYNC-4](US-SYNC-4.md) | Changes from another device pull down | 🔴 Must |
| [US-SYNC-5](US-SYNC-5.md) | Two devices edit the same month | 🔴 Must |
| [US-SYNC-6](US-SYNC-6.md) | Disconnect my Google Drive account | 🔴 Must |
| [US-SYNC-7](US-SYNC-7.md) | Sync fails gracefully | 🟡 Should |

**Scope note:** v1 syncs `finance_record` entries only (plain expense/income, no debt/event/shared-
cost link — those features aren't synced yet, so a linked record would dangle on another device
without its parent). Categories aren't synced; an unknown `category_id` pulled from Drive falls
back to Uncategorized rather than crashing.

**Status (2026-07):** Phase 1 (this branch) implements connect/disconnect and the local schema/
module scaffold only — no record is actually pushed or pulled yet. Push (US-SYNC-3) and pull
(US-SYNC-4, US-SYNC-5) are tracked as explicit follow-up phases; see the branch's implementation
plan for the phase breakdown.
