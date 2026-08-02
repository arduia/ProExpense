# User Story

> **ID:** US-SYNC-2 · **Service:** `feature:sync` · **Screen:** 13 More → Google Drive Sync
> **Priority:** 🔴 Must · **Status:** 📋 Planned (Phase 2/3) · **Persona:** ✈️ Carlos (Traveler)

## Title

> First sync after connecting

---

## User Story

**As** Carlos ✈️
**I want to** have my existing records uploaded and any records already in my Drive folder pulled
down the first time I connect
**So that** I don't lose history from either side when I start syncing

---

## Description

### Background

The first sync after connecting is a merge, not a one-directional copy — a user could be
reconnecting on a second device that already has remote data, or connecting for the first time with
only local data. Every local record starts `dirty = 1` on creation, so "push everything dirty" is
already the correct behavior for a fresh connect with no special-casing.

### Scope

**In Scope**

* On first successful connect, push every local month with data, then pull every remote month not
  yet known locally.

**Out of Scope**

* Ongoing incremental sync after the first run — [US-SYNC-3](US-SYNC-3.md)/[US-SYNC-4](US-SYNC-4.md).
* Conflict resolution when the same record exists both locally and remotely with different content —
  [US-SYNC-5](US-SYNC-5.md).

---

## Acceptance Criteria

### Scenario 1 — Fresh connect, local data only

**Given**

* I have local records and no prior Drive sync history.

**When**

* I connect my Drive account.

**Then**

* Every month containing local records is uploaded as a new month file.

### Scenario 2 — Reconnecting with existing remote data

**Given**

* My Drive `appDataFolder` already has month files from a previous connect (e.g. another device).

**When**

* I connect on this device for the first time.

**Then**

* Every remote month not already known locally is downloaded and merged in, in addition to any
  local-only months being pushed.

---

## Functional Requirements

* [ ] First sync pushes every month with local data.
* [ ] First sync pulls every remote month not yet represented locally.
* [ ] First sync does not require the user to trigger anything beyond the initial connect (or, if
  Phase 1 ships connect without auto-sync, an explicit first "Sync now" — see Notes).

---

## Business Rules

* A record's month bucket is computed **in UTC** from `recorded_at`, so the bucket is deterministic
  regardless of device timezone.

---

## Dependencies

* **Story/Task:** [US-SYNC-1](US-SYNC-1.md), [US-SYNC-3](US-SYNC-3.md), [US-SYNC-4](US-SYNC-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* Not implemented in Phase 1 (this branch) — connect/disconnect only. Tracked as a Phase 2/3
  follow-up alongside push/pull sync.
