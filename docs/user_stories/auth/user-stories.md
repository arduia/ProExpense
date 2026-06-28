# Auth & Security — User Stories

> Service: `feature:auth` · Screens: 14 PIN Setup · 15 PIN Entry
> PRD use case: Auth Setup (🟡 Should; Biometric 🔵 Phase 2).
> Legend & format: [`../README.md`](../README.md) · [`../TEMPLATE.md`](../TEMPLATE.md).

### US-AUTH-1 — Protect the app with a PIN · 🟡 Should
> **As** Siti 🏠, **I want** to set a 6-digit PIN, **so that** my financial data is private on a shared device.

- **AC1** — **Given** PIN is off, **when** I toggle to enable, **then** I enter a 6-digit PIN and confirm it (must match).
- **AC2** — **Given** the confirm does not match, **when** I submit, **then** dots clear and shake (±4dp), "PINs do not match. Try again." shows, and the original PIN is preserved.
- **AC3** — **Given** the PIN is set, **when** setup completes, **then** "PIN is now active. You'll be asked to enter it on your next launch." is shown.

### US-AUTH-2 — See my digits while creating a PIN · 🟡 Should
> **As** Mr. Chen 👴, **I want** to reveal the digits while creating my PIN, **so that** I can confirm I typed it correctly.

- **AC1** — **Given** I am creating a PIN (set & confirm), **when** I tap the eye toggle, **then** the entered digits reveal/hide.
- **AC2** — **Given** the creation screen, **when** it first renders, **then** digits are hidden by default and revealing shows the typed digits in place of the dots.
- **AC3** — **Given** the unlock/lock screen, **when** I view it, **then** no reveal toggle exists (scoped to creation only).

### US-AUTH-3 — Set a required recovery question · 🟡 Should
> **As** any user, **I want** to set a security question when enabling PIN, **so that** I can recover access if I forget it.

- **AC1** — **Given** I am enabling a PIN, **when** I complete setup, **then** a security question is required (pick from a predefined list + answer) — the PIN cannot be enabled without it.

### US-AUTH-4 — Unlock the app · 🟡 Should
> **As** any user with PIN on, **I want** to enter my PIN on launch/resume, **so that** only I can open the app.

- **AC1** — **Given** the lock screen, **when** I enter digits, **then** six dot indicators fill, a full-screen numeric keypad is shown, and there is no back navigation.
- **AC2** — **Given** I enter the correct PIN, **when** it is accepted, **then** I go to Home immediately.
- **AC3** — **Given** I enter an incorrect PIN, **when** it is rejected, **then** dots show a danger outline + shake + "Incorrect PIN, try again".
- **AC4** — **Given** the app is sent to background, **when** I resume, **then** the PIN is required again (re-lock on stop).

### US-AUTH-5 — Be protected against brute force · 🟡 Should
> **As** a security-conscious user, **I want** lockout after repeated failures, **so that** my PIN can't be guessed.

- **AC1** — **Given** 5 incorrect attempts, **when** the 5th fails, **then** the app locks for 30s with a countdown, the keypad is disabled until it completes, and attempts then reset.

### US-AUTH-6 — Unlock with biometrics · 🔵 Phase 2
> **As** Carlos ✈️, **I want** Face ID / fingerprint unlock, **so that** I can open the app faster.

- **AC1** — **Given** biometric is offered, **when** I try to enable it, **then** it requires the PIN to be set first.
- **AC2** — **Given** biometric is enabled, **when** the lock screen appears, **then** it auto-prompts; success → Home, failure falls back to PIN.
- **AC3** — **Given** PIN is off, **when** I tap Biometric, **then** "Please enable PIN first to use biometric authentication." is shown.

### US-AUTH-7 — Change or disable my PIN · 🟡 Should
> **As** any user, **I want** to change or turn off my PIN, **so that** I stay in control of security.

- **AC1** — **Given** I change my PIN, **when** I proceed, **then** I verify current → new → confirm (same mismatch handling).
- **AC2** — **Given** I disable my PIN, **when** I confirm with the current PIN, **then** PIN is turned off and biometric is also turned off.

### US-AUTH-8 — Recover a forgotten PIN · 🟡 Should
> **As** Mr. Chen 👴, **I want** to recover access if I forget my PIN, **so that** I'm not locked out of my data.

- **AC1** — **Given** I tap Forgot PIN, **when** I answer the security question correctly, **then** I can set a new PIN.
- **AC2** — **Given** a wrong answer, **when** I submit, **then** "Try again" shows; after 5 wrong → 30s lockout, then attempts reset.
- **AC3** — **Given** recovery fails, **when** I have no other option, **then** "reset app (clear all data)" is offered as a last resort.
