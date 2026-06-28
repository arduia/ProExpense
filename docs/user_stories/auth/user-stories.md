# Auth & Security — User Stories

> Service: `feature:auth` · Screens: 14 PIN Setup · 15 PIN Entry
> PRD use case: Auth Setup (🟡 Should; Biometric 🔵 Phase 2). Legend & format: [`../README.md`](../README.md).

### US-AUTH-1 — Protect the app with a PIN · 🟡 Should
> **As** Siti 🏠, **I want** to set a 6-digit PIN, **so that** my financial data is private on a shared device.

- AC1: Toggle to enable → enter 6-digit PIN → confirm (must match).
- AC2: Mismatch → dots clear and shake (±4dp), "PINs do not match. Try again.", original PIN preserved.
- AC3: Success message: "PIN is now active. You'll be asked to enter it on your next launch."

### US-AUTH-2 — See my digits while creating a PIN · 🟡 Should
> **As** Mr. Chen 👴, **I want** to reveal the digits while creating my PIN, **so that** I can confirm I typed it correctly.

- AC1: During PIN **creation** (set & confirm), an eye toggle reveals/hides the entered digits.
- AC2: Hidden is the default; revealing shows the typed digits in place of the dots.
- AC3: The reveal toggle is scoped to creation only — **not** the unlock/lock screen.

### US-AUTH-3 — Set a required recovery question · 🟡 Should
> **As** any user, **I want** to set a security question when enabling PIN, **so that** I can recover access if I forget it.

- AC1: A security question is **required**: pick from a predefined list + answer; PIN cannot be enabled without it.

### US-AUTH-4 — Unlock the app · 🟡 Should
> **As** any user with PIN on, **I want** to enter my PIN on launch/resume, **so that** only I can open the app.

- AC1: Six dot indicators fill as digits are entered; a full-screen numeric keypad is shown; no back navigation.
- AC2: Correct PIN → Home immediately.
- AC3: Incorrect → dots show danger outline + shake + "Incorrect PIN, try again".
- AC4: The app re-locks when sent to background (resume requires PIN again).

### US-AUTH-5 — Be protected against brute force · 🟡 Should
> **As** a security-conscious user, **I want** lockout after repeated failures, **so that** my PIN can't be guessed.

- AC1: 5 incorrect attempts → 30s lockout with a countdown; keypad disabled until it completes, then attempts reset.

### US-AUTH-6 — Unlock with biometrics · 🔵 Phase 2
> **As** Carlos ✈️, **I want** Face ID / fingerprint unlock, **so that** I can open the app faster.

- AC1: Biometric is offered but requires the PIN to be set first.
- AC2: If enabled, biometric auto-prompts on the lock screen; success → Home; failure falls back to PIN.
- AC3: Tapping Biometric while PIN is off → "Please enable PIN first to use biometric authentication."

### US-AUTH-7 — Change or disable my PIN · 🟡 Should
> **As** any user, **I want** to change or turn off my PIN, **so that** I stay in control of security.

- AC1: Change PIN: verify current → new → confirm (same mismatch handling).
- AC2: Disable PIN: enter current PIN to confirm; disabling also turns biometric off.

### US-AUTH-8 — Recover a forgotten PIN · 🟡 Should
> **As** Mr. Chen 👴, **I want** to recover access if I forget my PIN, **so that** I'm not locked out of my data.

- AC1: Forgot PIN → security question; correct answer sets a new PIN.
- AC2: Wrong answer → "Try again"; 5 wrong → 30s lockout, then attempts reset.
- AC3: Reset app (clear all data) is offered as a last resort.
