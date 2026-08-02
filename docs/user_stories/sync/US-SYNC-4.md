# User Story

> **ID:** US-SYNC-4 · **Service:** `feature:sync` · **Screen:** 13 More → Google Drive Sync
> **Priority:** 🔴 Must · **Status:** 📋 Planned (Phase 3) · **Persona:** 👫 Aiko (Cost Sharer)

## Title

> Changes from another device pull down

---

## User Story

**As** Aiko 👫
**I want to** see records I logged on another device show up here after syncing
**So that** all my devices reflect the same history

---

## Description

### Background

Pull only re-downloads month files whose remote copy is newer than what's already merged locally
(tracked via `finance_record_month_sync.remote_updated_at`), so an unrelated month's edits never
cost this device a download.

### Scope

**In Scope**

* Detecting month files with a newer remote version than the locally-known one.
* Downloading and reading a changed month file, applying each row's insert/update/delete into local
  storage.
* Falling back to Uncategorized for a pulled record whose `category_id` doesn't exist locally.

**Out of Scope**

* Same-record conflicting edits from two devices — [US-SYNC-5](US-SYNC-5.md).

---

## Acceptance Criteria

### Scenario 1 — New record from another device

**Given**

* A record was added on Device B in a month this device has no local changes in.

**When**

* This device syncs.

**Then**

* That month's file is downloaded and the new record appears in this device's history.

### Scenario 2 — Untouched month is skipped

**Given**

* A month's remote file has not changed since this device last synced it.

**When**

* This device syncs.

**Then**

* That month's file is not re-downloaded.

### Scenario 3 — Unknown category on pull

**Given**

* A pulled record references a `category_id` that doesn't exist on this device.

**When**

* The record is merged in.

**Then**

* It is filed under the device's Uncategorized bucket instead of crashing or being silently
  dropped.

### Scenario 4 — Deletion from another device

**Given**

* A record was deleted on Device B (tombstoned, `deleted = 1` in its month's payload).

**When**

* This device pulls that month.

**Then**

* The record is removed from this device's history too (tombstone applied locally).

---

## Functional Requirements

* [ ] Pull only downloads a month file when its remote version is newer than the locally-known one.
* [ ] A downloaded month's rows are applied individually (insert/update/tombstone-delete), not as a
  blind whole-file overwrite.
* [ ] An unresolvable `category_id` on a pulled record falls back to Uncategorized.
* [ ] A corrupt or unreadable downloaded SQLite file is reported as a sync error for that month
  without crashing the app or corrupting local storage (see [US-SYNC-7](US-SYNC-7.md)).

---

## Dependencies

* **Story/Task:** [US-SYNC-1](US-SYNC-1.md), [US-SYNC-3](US-SYNC-3.md), [US-SYNC-5](US-SYNC-5.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* Not implemented in Phase 1 (this branch) — tracked as Phase 3.
