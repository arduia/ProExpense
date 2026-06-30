# User Story

> **ID:** US-MORE-3 · **Service:** `app` (More) · **Screen:** 13 More
> **Priority:** 🔵 Phase 2 · **Status:** 🚧 Planned · **Persona:** 👴 Mr. Chen (Retiree)

## Title

> Adjust appearance and language

---

## User Story

**As** Mr. Chen 👴
**I want to** choose a theme and a language
**So that** the app suits how I see and read, in my own language

---

## Description

### Background

Accessibility and global readiness are core principles, but full localization is scoped to Phase 2
in the roadmap. Theme (Light/Dark/System) and a language picker are grouped here as the two
appearance/locale settings expected once that phase begins.

### Scope

**In Scope**

* Theme selector: Light / Dark / System.
* Language selector (Phase 2 — localization rollout).

**Out of Scope**

* The localization content/translation work itself — tracked separately in the roadmap.

---

## Acceptance Criteria

### Scenario 1 — Theme selection

**Given**

* I open Theme.

**When**

* I choose an option.

**Then**

* Light, Dark, or System are available and apply immediately.

### Scenario 2 — Language selection

**Given**

* I open Language.

**When**

* I choose an option.

**Then**

* A language is selectable (full localization coverage is Phase 2 in the roadmap).

---

## Functional Requirements

* [ ] Theme setting offers exactly three options: Light, Dark, System.
* [ ] Selected theme applies across the whole app without requiring a restart.
* [ ] Language setting is present in More even before full localization ships, as a forward-compat
  placeholder.

---

## Non-Functional Requirements

* [ ] **Accessibility** — Dark theme meets the same contrast standards as Light theme.

---

## Business Rules

* System theme follows the OS-level light/dark setting.

---

## UI / UX Notes

* **Design / Mockup:** [`13-more.md`](../../../design-system-spec/screens/13-more.md) → Theme, Language.

---

## Dependencies

* **Story/Task:** [US-MORE-1](US-MORE-1.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for Light/Dark theme passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Phase 2 per the roadmap — full language coverage is not expected at MVP.
