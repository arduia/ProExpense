# User Story

> **ID:** US-SYNC-7 · **Service:** `feature:sync` · **Screen:** 13 More → Google Drive Sync
> **Priority:** 🟡 Should · **Status:** 🚧 In Progress (Phase 1 partial) · **Persona:** 🛒 Amara (Vendor)

## Title

> Sync fails gracefully

---

## User Story

**As** Amara 🛒
**I want to** keep using the app normally when sync can't complete
**So that** a flaky connection or an expired login never blocks my logging

---

## Description

### Background

Sync is additive and opt-in (belief #4) — a sync failure must never degrade the core offline
logging experience, which remains the app's primary job regardless of connection state.

### Scope

**In Scope**

* No network / offline at sync time.
* Expired or revoked OAuth token.
* Drive API errors (quota, HTTP 4xx/5xx).
* A corrupt or unreadable downloaded month file.
* One month's failure isolated from other months in the same sync run.

**Out of Scope**

* Automatic retry scheduling / background sync — v1 is manual "Sync now" only (see
  [US-SYNC-3](US-SYNC-3.md) Notes); a failed sync simply leaves the affected month(s) dirty for the
  next manual trigger.

---

## Acceptance Criteria

### Scenario 1 — Offline

**Given**

* The device has no network connection.

**When**

* I trigger a sync (or connect).

**Then**

* The app reports the sync couldn't complete, without crashing; local logging keeps working
  normally.

### Scenario 2 — Expired token

**Given**

* My stored OAuth token has expired or been revoked.

**When**

* A sync runs.

**Then**

* The app surfaces a "reconnect required" state on the sync settings row instead of silently
  failing or looping.

### Scenario 3 — Corrupt downloaded file

**Given**

* A downloaded month file fails to open as a valid SQLite database.

**When**

* Pull processes that month.

**Then**

* That month's pull is reported as failed and skipped; other months in the same sync still complete
  normally, and local storage is not left partially written.

### Scenario 4 — Auth/network error surfaces as a typed failure

**Given**

* Any use case in `feature:sync` hits a network or auth exception.

**When**

* The exception is caught.

**Then**

* It surfaces as `Result.Error`, not an uncaught crash, per the repository boundary contract used
  throughout the codebase.

---

## Functional Requirements

* [ ] Every sync use case wraps failures as `Result.Error`, never lets an exception propagate
  uncaught.
* [ ] An expired/revoked token surfaces a distinct "reconnect required" state, not a generic error.
* [ ] A single month's push or pull failure does not abort or roll back other months in the same
  sync run.
* [ ] Sync failures never block or degrade non-sync app functionality (logging, history, etc.).

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

* Phase 1 (this branch) covers the connect-flow failure modes (Scenario 1 offline / cancelled
  consent, covered under US-SYNC-1). Push/pull failure isolation (Scenarios 3-4) lands with Phase 2/3.
