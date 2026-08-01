# User Story

> **ID:** US-SYNC-1 · **Service:** `feature:sync` · **Screen:** 13 More → Google Drive Sync
> **Priority:** 🔴 Must · **Status:** 🚧 In Progress (Phase 1: connect/disconnect UI) · **Persona:** ✈️ Carlos (Traveler)

## Title

> Connect my Google Drive account

---

## User Story

**As** Carlos ✈️
**I want to** connect my Google Drive account from Settings
**So that** my records can sync across my devices without me managing files manually

---

## Description

### Background

Cloud sync is opt-in and off by default (project philosophy belief #4) — nothing about the app's
core offline experience changes unless the user deliberately connects. The OAuth consent requested
is scoped to `drive.appdata` only: a private, per-app folder that never appears in the user's normal
Drive UI and that no other app can read. This is a Drive-access grant, not an app-level account or
identity system — the app still has no server-side auth of its own.

### Scope

**In Scope**

* Google Sign-In / Credential Manager flow requesting the `drive.appdata` OAuth scope only.
* Persisting the resulting connection state (connected flag, account email) and encrypted OAuth
  tokens.
* Settings row showing "Not connected" / "Connected as {email}".
* Disclosure copy on the Connect screen: sync is optional, and the remote copy relies on Drive's
  own encryption plus the private app-folder scope (no additional app-level encryption in v1 — see
  [US-SYNC-6](US-SYNC-6.md) for what disconnecting does and does not do).

**Out of Scope**

* Actually pushing or pulling any record — covered by [US-SYNC-2](US-SYNC-2.md)/[US-SYNC-3](US-SYNC-3.md)/[US-SYNC-4](US-SYNC-4.md).
* Disconnect flow — covered by [US-SYNC-6](US-SYNC-6.md).

---

## Acceptance Criteria

### Scenario 1 — Connecting

**Given**

* I am not connected to Google Drive.

**When**

* I tap "More → Google Drive Sync" and complete the Google OAuth consent screen, granting
  `drive.appdata` access.

**Then**

* The app stores the connection state and encrypted OAuth tokens locally.
* The settings row updates to "Connected as {my email}".

### Scenario 2 — Cancelling consent

**Given**

* I open the Connect screen.

**When**

* I back out of or cancel the Google OAuth consent screen.

**Then**

* The app remains in the "Not connected" state; no partial connection state or token is persisted.

### Scenario 3 — Disclosure is shown before connecting

**Given**

* I open the Connect screen.

**When**

* The screen renders, before I tap "Connect".

**Then**

* I see copy disclosing that sync is optional and that the remote copy is protected by Drive's own
  encryption and a private app-only folder, not additional app-level encryption.

---

## Functional Requirements

* [ ] Connect flow requests only the `https://www.googleapis.com/auth/drive.appdata` OAuth scope.
* [ ] OAuth tokens are stored via `androidx.security.crypto` (EncryptedSharedPreferences), not
  plaintext.
* [ ] Cancelling/failing the OAuth flow leaves no connection state or token behind.
* [ ] The Settings row and Connect screen reflect the current connection state on every launch.

---

## Non-Functional Requirements

* [ ] **Privacy** — no app-level encryption on the remote per-month DB files in v1; this is
  disclosed to the user in the Connect screen copy, not silently omitted. Revisit only if a
  user-managed recovery-key flow is explicitly requested (rejected for v1: no server exists to
  broker a shared key across the user's devices).
* [ ] **Security** — OAuth tokens are Keystore-encrypted at rest on-device.

---

## Business Rules

* Sync is off by default for every install; connecting is the only way to turn it on.
* Google Sign-In here grants Drive file access only — it is not used as an app login/identity
  mechanism (PIN remains the only local auth).

---

## UI / UX Notes

* **Design / Mockup:** extends [`13-more.md`](../../../design-system-spec/screens/13-more.md) with
  a new "Google Drive Sync" row and Connect screen states.

---

## Dependencies

* **Story/Task:** [US-SYNC-2](US-SYNC-2.md), [US-SYNC-6](US-SYNC-6.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

* Phase 1 (this branch) implements this story fully; push/pull sync (US-SYNC-2..5) are explicit
  follow-up phases, not implemented yet — `TriggerManualSyncUseCase` is a stub.
