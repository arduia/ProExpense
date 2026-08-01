# User Story

> **ID:** US-SYNC-5 · **Service:** `feature:sync` · **Screen:** 13 More → Google Drive Sync
> **Priority:** 🔴 Must · **Status:** 📋 Planned (Phase 3) · **Persona:** 👫 Aiko (Cost Sharer)

## Title

> Two devices edit the same month

---

## User Story

**As** Aiko 👫
**I want to** have my edits and my other device's edits both survive when they touch the same month
**So that** syncing never silently discards one device's work

---

## Description

### Background

Because the sync unit is a whole month's file, naively overwriting the remote file with the local
one (or vice versa) would discard whichever side lost the race — even if the two devices edited
completely different records. Conflict resolution therefore happens **per record**, while merging a
downloaded month's rows, not per file.

### Scope

**In Scope**

* Per-record last-write-wins using `updated_at`, applied while merging a pulled month's rows.
* A deterministic tie-break (remote wins) when two `updated_at` values are exactly equal.

**Out of Scope**

* A manual merge UI for the user to pick a side — explicitly deferred; last-write-wins is the whole
  mechanism for v1.

---

## Acceptance Criteria

### Scenario 1 — Different records, same month

**Given**

* Device A edits record X and Device B edits record Y, both in the same month, and both devices
  sync.

**Then**

* Both edits survive — record X reflects Device A's edit and record Y reflects Device B's edit; the
  same-month file update does not cause either edit to be lost.

### Scenario 2 — Same record, different devices

**Given**

* Device A and Device B both edit record X (same id) before either syncs.

**When**

* Both devices eventually sync.

**Then**

* The edit with the later `updated_at` timestamp wins; the other device's local copy is overwritten
  to match on its next pull.

### Scenario 3 — Exact timestamp tie

**Given**

* Two devices' edits to the same record carry identical `updated_at` values.

**When**

* The conflict is resolved.

**Then**

* The remote value wins, deterministically (not randomly per run).

---

## Functional Requirements

* [ ] Conflict resolution compares `updated_at` per record, not per month file.
* [ ] Exact-tie resolution is deterministic (remote wins) and covered by a test, not left to
  incidental map/list ordering.
* [ ] No user-facing merge-conflict prompt in v1 — resolution is automatic.

---

## Dependencies

* **Story/Task:** [US-SYNC-3](US-SYNC-3.md), [US-SYNC-4](US-SYNC-4.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* Not implemented in Phase 1 (this branch) — tracked as Phase 3, alongside pull sync.
