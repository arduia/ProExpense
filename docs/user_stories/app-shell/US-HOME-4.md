# User Story

> **ID:** US-HOME-4 · **Service:** `app` (Home) · **Screen:** 03 Home
> **Priority:** 🟡 Should · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Reach features quickly

---

## User Story

**As** any user
**I want to** jump straight to key features from Home
**So that** I don't have to dig through More to reach the things I use often

---

## Description

### Background

Reports, Debt Tracker, Shared Costs, and Events are all reachable through More, but for users who
use them regularly, that's an extra hop every time. Quick-access tiles on Home, plus the persistent
bottom navigation and raised `Add` button, keep every top-level destination within one tap.

### Scope

**In Scope**

* Quick-access tiles deep-linking to Reports / Debt / Split / Events.
* Persistent bottom nav (Home active) + raised center `Add` across top-level screens.

**Out of Scope**

* The destination screens themselves — covered by their own feature story sets.

---

## Acceptance Criteria

### Scenario 1 — Quick-access tiles

**Given**

* Home.

**When**

* I tap a quick-access tile.

**Then**

* It deep-links directly into Reports, Debt Tracker, Shared Costs (Split), or Events.

### Scenario 2 — Persistent navigation

**Given**

* Any top-level screen.

**When**

* I view it.

**Then**

* The bottom navigation bar (with Home shown active when relevant) and the raised center `Add`
  button are always present.

---

## Functional Requirements

* [ ] Home renders quick-access tiles for Reports, Debt Tracker, Shared Costs, and Events.
* [ ] Each tile navigates directly to its feature's entry screen.
* [ ] Bottom navigation and the raised `Add` action are rendered on every top-level screen, not
  just Home.

---

## Non-Functional Requirements

* [ ] **Usability** — every MVP feature is reachable within at most two taps from any top-level
  screen.

---

## Business Rules

* None beyond the tile-to-feature mapping above.

---

## UI / UX Notes

* **Design / Mockup:** [`03-home.md`](../../../design-system-spec/screens/03-home.md) → quick-access tiles, bottom nav.

---

## Dependencies

* **Story/Task:** [US-HOME-1](US-HOME-1.md), [US-MORE-1](US-MORE-1.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the quick-access tile row passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

None.
