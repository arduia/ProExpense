# User Story

> **ID:** US-MORE-1 · **Service:** `app` (More) · **Screen:** 13 More
> **Priority:** 🔴 Must · **Status:** ✅ Implemented · **Persona:** any user

## Title

> Navigate settings and features

---

## User Story

**As** any user
**I want to** find every feature and setting in one hub
**So that** I don't have to remember where each option lives

---

## Description

### Background

With no top-level nav slot for every feature, More acts as the catch-all hub for both
secondary features (Debt, Shared Costs, Reports, Categories) and app settings (security,
currency, budget, appearance, data). Keeping all of it in one predictable screen avoids a maze
of nested settings.

### Scope

**In Scope**

* Feature links section (Debt Tracker, Shared Costs, Reports, Category List).
* Settings section (PIN auth, Biometric, Currency, Monthly budget, Default category, Language,
  Theme, Data export, Clear data, App version).

**Out of Scope**

* The behavior of each linked feature/setting — covered by their own stories.

---

## Acceptance Criteria

### Scenario 1 — Feature links

**Given**

* I open More.

**When**

* I view feature links.

**Then**

* Debt Tracker, Shared Costs, Reports, and Category List are present and navigable.

### Scenario 2 — Settings list

**Given**

* I open More.

**When**

* I view settings.

**Then**

* PIN auth, Biometric (greyed until PIN is on), Currency, Monthly budget, Default category,
  Language, Theme (Light/Dark/System), Data export, Clear data, and App version are all present.

---

## Functional Requirements

* [ ] More renders the four feature links unconditionally.
* [ ] More renders all ten settings entries; Biometric is disabled/greyed while PIN is off.
* [ ] App version entry displays the current installed version string.

---

## Non-Functional Requirements

* [ ] **Discoverability** — every MVP setting and secondary feature is reachable from this single
  screen, with no setting hidden behind another setting.

---

## Business Rules

* Biometric entry is only interactive when PIN protection is enabled.

---

## UI / UX Notes

* **Design / Mockup:** [`13-more.md`](../../../design-system-spec/screens/13-more.md).

---

## Dependencies

* **Story/Task:** [US-AUTH-1](../auth/US-AUTH-1.md), [US-AUTH-6](../auth/US-AUTH-6.md), [US-CUR-2](../currency/US-CUR-2.md), [US-MORE-2](US-MORE-2.md), [US-MORE-3](US-MORE-3.md), [US-MORE-4](US-MORE-4.md), [US-CAT-1](../categories/US-CAT-1.md).

---

## Definition of Done

* [ ] Acceptance criteria met
* [ ] Code reviewed
* [ ] Unit tests completed
* [ ] Screenshot (Roborazzi) test for the More hub passes
* [ ] Documentation updated
* [ ] Product Owner accepted

---

## Notes

Fixed: the Biometric row was always disabled (`enabled = false` hardcoded) and its toggle was a
no-op (`onSettingToggle = { _, _ -> }`), regardless of PIN state — the Business Rule ("interactive
only when PIN is on") was never actually true. It now reads live `pinEnabled` +
`BiometricAuthenticator.isAvailable(...)` to enable/disable the row, and toggling calls
`PinAuthRepository.enrollBiometric()`/`clearBiometric()`. Tapping it while blocked shows
"Please enable PIN first to use biometric authentication." (US-AUTH-6 Scenario 3) via `ProToast`,
since a disabled `Switch` swallows touches — the row itself is now clickable when disabled to
catch that tap. Covered by `MoreScreenshotTest.edge_more_hub_pin_on`.

Also fixed: the "Default category" row had no tap handler at all (silent no-op) and no backing
preference. Added `DefaultCategoryRepository` (backed by `app_meta.default_category_id`), a
`MoreDefaultCategoryScreen` reusing the shared `CategoryPicker`, and wired the selection into
`QuickLogFlow`'s initial category. Covered by `MoreScreenshotTest.more_default_category` and
`AppMetaRepositoriesTest`.
