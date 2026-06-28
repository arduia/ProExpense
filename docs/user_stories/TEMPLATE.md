# User Story Template

> Copy a story block below when adding stories to any feature's `user-stories.md`.
> Keep it **lightweight**: inline priority on the heading (no metadata table), the
> "As a / I want / so that" statement, and **Given/When/Then** acceptance criteria.

**Priority tags:** 🔴 Must · 🟡 Should · 🔵 Phase 2
**Personas:** 🎓 Maya (Student) · 🏠 Siti (Housekeeper) · ✈️ Carlos (Traveler) · 👫 Aiko (Cost Sharer) ·
💼 Raj (Freelancer) · 🧳 Sophie (Expat) · 🎉 James (Event Organizer) · 👴 Mr. Chen (Retiree) ·
🛒 Amara (Vendor) · "any user" when persona-agnostic.

**ID scheme:** `US-<SERVICE>-<n>` (e.g. `US-LOG-2`).

---

## Template

```markdown
### US-<SERVICE>-<n> — <Short title> · <🔴 Must | 🟡 Should | 🔵 Phase 2>
> **As a** <persona>, **I want** <goal/action>, **so that** <benefit/value>.

- **AC1** — **Given** <precondition/context>, **when** <action>, **then** <observable, testable outcome>.
- **AC2** — **Given** <…>, **when** <…>, **then** <…>.

**Notes / edge cases** *(optional)*
- <constraints, error states, dependencies, or out-of-scope items>
```

---

## Filled example

### US-LOG-2 — Block an empty amount · 🔴 Must
> **As** any user, **I want** the app to stop me saving a $0 entry, **so that** I don't create meaningless records.

- **AC1** — **Given** the amount is $0 or empty, **when** I view the Amount screen, **then** `Save` and `Next` are disabled (`canProceed = value > 0`).
- **AC2** — **Given** the amount is $0, **when** I tap a disabled `Save`/`Next`, **then** the field shakes (±4dp) and shows "Amount must be greater than $0".
- **AC3** — **Given** I am typing an amount, **when** I enter digits, **then** the whole part is capped at 7 digits, the fraction at 2, a single decimal is allowed, leading zeros are stripped (except "0."), and commas group live.

**Notes / edge cases**
- Back from Amount with no value navigates away silently — no save, no prompt.
