# Dev Sign-off — Date & Time Picker

**Component spec:** [`components/date-time-picker.md`](../components/date-time-picker.md)
**Design source:** Claude Design project *"Pro Expense - Finance Tracker"* → `flow-01-screens.jsx`
→ `DateTimeSheet` (live mockup, no static PNG export existed for this component prior to this
pass — the reference screenshot at `../screenshots/date-time-picker.png` was captured from the
implementation, not the mockup; the mockup itself was verified by rendering its actual JSX source
in a browser, not by re-describing it from memory).
**Implementation:** `shared/src/androidMain/kotlin/com/arduia/expense/ui/design/DateTimePickerSheet.kt`
**Branch:** `claude/datetime-picker-enhancements-aib11q`
**Date:** 2026-07-16

## Fidelity checklist

| Design element (`DateTimeSheet`, `flow-01-screens.jsx`) | Implementation | Status |
|---|---|---|
| "DATE" / "TIME" uppercase mono eyebrow labels above each section | `SectionEyebrow` using `typography.eyebrow` | ✅ Match |
| Date chips wrap to multiple lines (`flexWrap`), not a scrolling row | `FlowRow` | ✅ Match |
| Selected chip filled dark, unselected outlined | `FilterChip` selected/unselected styling | ✅ Match |
| Future-dated chip shows a small dot marker | Not carried over — future-ness is instead surfaced via the sheet-level future-date notice banner | ⚠️ Deviation (see below) |
| Hour + colon + minute + AM/PM inside one bordered box | Hour/colon/minute unified in one bordered `card`-shaped `Row`; AM/PM broken out below it | ⚠️ Partial — see below |
| Hour/minute steppers: chevron-up / big number / chevron-down | `TimeSpinner` composable | ✅ Match |
| AM/PM as small stacked buttons inside the time box | Full-width `SegmentedToggle` below the time box | ⚠️ Deviation (see below) |
| Full-width primary "Apply" button | `ProButton` primary, `weight(1f)` | ✅ Match (paired with a Cancel button the mockup doesn't have — see below) |
| No calendar fallback for out-of-range dates | Trailing "Pick another date" chip + M3 `DatePicker` fallback dialog | ⚠️ Addition (see below) |

## Deliberate deviations (all covered by the planning-stage `compose-product-auditor` pass)

1. **Fallback calendar / "Pick another date" chip** — the mockup's date row is a fixed 6-date demo
   range with no fallback, because its static prototype never needs to represent a date outside
   that window. This sheet is reused in production to edit real records (a debt due date, a past
   expense) that routinely fall outside a ±3-day quick-pick window, so a fallback is required for
   the feature to function at all, not optional polish.
2. **AM/PM as a full-width `SegmentedToggle` instead of small stacked buttons inside the time box**
   — chosen for a larger, design-system-consistent touch target; the mockup's stacked buttons are
   sized for a static demo, not validated against this repo's touch-target standard.
3. **Cancel button** — the mockup only has "Apply"; the sheet keeps an explicit Cancel alongside it
   to match every other bottom sheet in this codebase (`bottom-sheet.md`'s established
   Cancel/primary-action footer pattern) rather than relying on the sheet's own close (X) affordance
   alone.
4. **Future-date dot marker on chips** — the mockup marks future-dated chips with a small trailing
   dot; the implementation instead surfaces "future date" once, at the sheet level, via the info
   banner already required by product copy ("entries are ordered by created date, not expense
   date"). Carrying the per-chip dot as well would duplicate the same signal — flagged here as an
   intentional simplification, not an oversight, but open to revisiting if design wants the
   per-chip marker kept regardless.

## Verification (Step 6 gate, this repo's `AGENTS.md`)

- ✅ `gradle :shared:compileDebugKotlinAndroid` / `:app:compileDevDebugKotlin`
- ✅ `gradle :shared:ktlintFormat` (no changes needed)
- ✅ `gradle :shared:detektAndroidDebug :shared:detektAndroidRelease` (baseline regenerated for
  signature-key drift only — see `.agents/skills/kotlin-lint-style/lint-retrospective.md`)
- ✅ `gradle :app:verifyRoborazziDevDebug` — full suite, including the re-recorded
  `add_date_time_sheet` baseline
- ✅ `gradle verifyAll -x :app:detekt` (`:app:detekt` excluded for a pre-existing, unrelated
  `MoreFlow.kt` failure confirmed present on the base branch before this work)
- Live design comparison: `DateTimeSheet` rendered directly from its JSX source via a local
  Playwright/Chromium harness and compared side-by-side against the Roborazzi capture (not a
  re-description from memory).

## Sign-off

Implementation matches the design source on every element except the four deviations above, each
of which has a stated product/engineering reason and was raised during the planning-stage audit
rather than discovered after the fact. No open blockers. The future-date dot marker (deviation 4)
is the one item worth a design opinion if pixel-parity on that specific detail matters more than
avoiding the duplicate signal.
