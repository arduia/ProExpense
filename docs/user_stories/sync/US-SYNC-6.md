# User Story

> **ID:** US-SYNC-6 · **Service:** `feature:sync` · **Screen:** 13 More → Google Drive Sync
> **Priority:** 🔴 Must · **Status:** 🚧 In Progress (Phase 1) · **Persona:** ✈️ Carlos (Traveler)

## Title

> Disconnect my Google Drive account

---

## User Story

**As** Carlos ✈️
**I want to** disconnect Google Drive sync at any time
**So that** I can turn sync off without worrying about losing data

---

## Description

### Background

Data ownership is a non-negotiable product constraint (mirrors [US-IE-1](../importexport/US-IE-1.md)'s
export story): disconnecting is a **local** action only. It must never delete the user's local
records, and must never delete or touch their remote Drive files — the user's data stays exactly
where it was on both sides; only this device's link between them is severed.

### Scope

**In Scope**

* Clearing the locally-stored OAuth tokens and connection state.
* Stopping any further sync activity from this device.

**Out of Scope**

* Deleting local records — never happens as part of disconnect.
* Deleting remote Drive files — never happens as part of disconnect (revoking Google's own account
  permission, if the user wants that, happens in the user's Google Account settings, not this app).

---

## Acceptance Criteria

### Scenario 1 — Disconnecting

**Given**

* I am connected to Google Drive.

**When**

* I tap "Disconnect" and confirm.

**Then**

* The locally-stored OAuth tokens and connection state are cleared, and the settings row reverts to
  "Not connected."

### Scenario 2 — Data is untouched

**Given**

* I disconnect.

**When**

* I check my local record history and (separately) my Drive `appDataFolder`.

**Then**

* My local records are all still present, unchanged, and my Drive month files are still present,
  unchanged — disconnect deleted neither.

### Scenario 3 — Reconnecting later

**Given**

* I previously disconnected.

**When**

* I connect again (same or different Google account).

**Then**

* This behaves as a fresh connect ([US-SYNC-1](US-SYNC-1.md)/[US-SYNC-2](US-SYNC-2.md)) — no stale
  token or state from the prior connection is reused.

---

## Functional Requirements

* [ ] Disconnect clears local OAuth tokens and connection state only.
* [ ] Disconnect never deletes local records.
* [ ] Disconnect never deletes or modifies remote Drive files.
* [ ] After disconnect, no further automatic or manual sync can run until the user reconnects.

---

## Business Rules

* Disconnect is reversible by reconnecting; it is not a data-loss operation on either side.

---

## Dependencies

* **Story/Task:** [US-SYNC-1](US-SYNC-1.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* Implemented in Phase 1 (this branch) alongside connect (US-SYNC-1).
