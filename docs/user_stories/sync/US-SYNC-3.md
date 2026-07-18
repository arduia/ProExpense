# User Story

> **ID:** US-SYNC-3 · **Service:** `feature:sync` · **Screen:** 13 More → Google Drive Sync
> **Priority:** 🔴 Must · **Status:** 📋 Planned (Phase 2) · **Persona:** 👫 Aiko (Cost Sharer)

## Title

> My edits push to Drive

---

## User Story

**As** Aiko 👫
**I want to** have my logged/edited/deleted records uploaded to Drive when I sync
**So that** my other devices can see the same history

---

## Description

### Background

Bandwidth is reduced by batching at the calendar-month level: every edit within a month marks that
month "dirty," and a sync uploads one file per dirty month (built fresh from all of that month's
records, including tombstoned deletes) rather than one API call per record. Months with no changes
are never re-uploaded.

### Scope

**In Scope**

* Building a SQLite file (via the `sync_record` payload schema) per dirty month and uploading it
  (`files.create` the first time for that month, `files.update` afterward).
* Marking a record's tombstone (soft-delete) as part of its month's payload instead of hard-deleting
  it locally, so a later pull elsewhere doesn't resurrect it.
* Editing a record's `recorded_at` (backdating) marks **both** its old and new month dirty.

**Out of Scope**

* Pulling remote changes — [US-SYNC-4](US-SYNC-4.md).

---

## Acceptance Criteria

### Scenario 1 — Editing within one month

**Given**

* I add or edit three records that all fall in the same month.

**When**

* I trigger a sync.

**Then**

* Exactly one Drive file upload happens for that month, containing all three records.

### Scenario 2 — Deleting a record

**Given**

* I delete a previously-synced record.

**When**

* I trigger a sync.

**Then**

* The record is not hard-deleted locally before syncing — a tombstone is recorded — and the next
  sync's month payload includes it marked `deleted = 1`, so other devices apply the deletion instead
  of keeping a stale copy.

### Scenario 3 — Backdating a record across months

**Given**

* I edit a record's date so it moves from month A to month B.

**When**

* I trigger a sync.

**Then**

* Both month A's and month B's files are re-uploaded (A no longer contains the record, B now does).

### Scenario 4 — Partial failure

**Given**

* I have two dirty months and Drive's API fails while uploading the first.

**When**

* The sync completes.

**Then**

* The failed month stays dirty and is retried on the next sync; the second month's successful
  upload is not rolled back or blocked by the first month's failure.

---

## Functional Requirements

* [ ] Push builds one SQLite payload per dirty month from that month's full local record set
  (including tombstones), not just the changed subset.
* [ ] A record's `delete()` writes a tombstone (with its `year_month`) instead of a hard delete.
* [ ] Editing `recorded_at` marks both the source and destination month dirty.
* [ ] One month's push failure does not block or roll back another month's push in the same sync.
* [ ] A successfully pushed month clears the `dirty` flag on every record/tombstone it covered.

---

## Business Rules

* The remote artifact is one SQLite database file per calendar month (UTC-bucketed), not one file
  per record and not a single all-time file.

---

## Dependencies

* **Story/Task:** [US-SYNC-1](US-SYNC-1.md), [US-SYNC-2](US-SYNC-2.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* Not implemented in Phase 1 (this branch). The local schema/columns this story depends on (`dirty`,
  `finance_record_tombstone`, `finance_record_month_sync`) ship in Phase 1; the push logic itself
  (`TriggerManualSyncUseCase`'s push half) is Phase 2.
