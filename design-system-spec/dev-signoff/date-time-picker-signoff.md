# Dev Sign-off — Date & Time Picker

**Component spec:** [`components/date-time-picker.md`](../components/date-time-picker.md)
**Design source:** Claude Design project *"Pro Expense - Finance Tracker"*
(`79eccec0-6ad2-477a-95ec-18df4a5dc017`) → **`Date & Time Picker - Dev Handoff.html`** (the
authoritative dev handoff doc for this component — a full page of anatomy specs, sizing tables,
color tokens, and interaction rules) → `date-picker.jsx` (`CalendarMonth`, `PickerScreenShell`,
`DatePickerSheet`, `DateRangeSheet`) + `flow-01-screens.jsx` (`DateTimeSheet`). Reference
screenshots: `handoff/picker-txn.png`, `handoff/picker-event-range.png`, `handoff/picker-debt-due.png`.
**Implementation:** `shared/src/androidMain/kotlin/com/arduia/expense/ui/design/PickerScreenShell.kt`,
`DateTimePickerScreen.kt`, `DatePickerScreen.kt`, `DateRangePickerScreen.kt`, `PickerDateMath.kt`.
**Branch:** `claude/datetime-picker-enhancements-aib11q`
**Date:** 2026-07-16

## Revision note

This sign-off **replaces** an earlier version written against a different, superseded design —
a bottom-sheet with quick-pick date chips (`flow-01-screens.jsx`'s `DateTimeSheet` **as it existed
in a different, older Claude Design project**, `e86b9ef3-c210-4df2-94f9-208d9d9308b9`). That
project did not have the dev handoff doc, the shared `date-picker.jsx` calendar component, or the
event-range/debt-due-date screens — none of which were discoverable until the correct project
(`79eccec0-...`) was found and its `Date & Time Picker - Dev Handoff.html` file was read directly.
The chip-based implementation and its screenshots were fully replaced by this rebuild; nothing
from the earlier sign-off carries forward.

## Fidelity checklist

| Design element (Dev Handoff spec) | Implementation | Status |
|---|---|---|
| Full-screen page (not overlay/sheet), `--paper` background | `PickerScreenShell` | ✅ Match |
| Header: close (×) left, title centered, no right action | `PickerScreenHeader` | ✅ Match |
| Body: "Date" label → month calendar → "Time" label → spinner card (transaction screen) | `DateTimePickerScreen` | ✅ Match |
| Selected day: solid `--blue-500` circle, white numeral | M3 `DatePicker` + `proDatePickerColors()` (`selectedDayContainerColor`/`selectedDayContentColor`) | ✅ Match |
| Today marker: dot under today's numeral when not selected | M3 `DatePicker`'s own today indicator (`todayContentColor`/`todayDateBorderColor`), not a hand-drawn 4px dot | ⚠️ Close, not pixel-identical (see below) |
| Time control: hour/minute vertical spinners + AM/PM, all in one bordered box | `TimeSpinner` × 2 + `TimeMeridiemToggle`, one `Row` | ✅ Match — corrected after initial review; AM/PM was first built as a separate full-width toggle below the box, flagged as wrong, and fixed to match the compact in-box stack the spec actually shows |
| Future-date banner below time card | `FutureDateNotice` | ✅ Match |
| Footer: full-width primary Apply, always enabled | `ProButton` primary, `fillMaxWidth` | ✅ Match |
| Event range: Start/End stub row, highlighted field awaiting input | `DateRangeStub` × 2 | ✅ Match — revised per product review, see below |
| Event range: band fill between start/end, rounded only at range ends | M3 `DateRangePicker` + `proDatePickerColors()` (`dayInSelectionRangeContainerColor`) | ✅ Match — M3's native range rendering already does exactly this |
| Event range: footer button label mirrors picked range / prompts for start-end | `DateRangePickerScreen` footer `when` | ✅ Match |
| Debt due date: empty state, no day pre-selected | `DatePickerScreen(initialEpochMillis = null)` | ✅ Match |
| Debt due date: footer = secondary Clear (when clearable) + primary "Use {date}" | `DatePickerScreen` footer | ✅ Match — **and fixes a real gap**: due date was optional in the data model but had no UI path back to "unset" before this rebuild |
| Color tokens (`--blue-500`, `--blue-100`, `--card`, `--line`, `--ink`, `--muted`) | `proDatePickerColors()` maps all of these to `ProColors` roles already | ✅ Match — same theming file used by `JournalDateRangeSheet` prior to this work |

## Deliberate deviations (reasoned, not oversights)

1. **Today marker rendered via M3's native today-indicator, not a hand-drawn 4×4px dot** — using
   the stock M3 `DatePicker`/`DateRangePicker` (already themed and precedented twice elsewhere in
   this codebase — `DatePickerTheming.kt`, `JournalDateRangeSheet.kt`) avoids writing and testing a
   fully custom month-grid (leap years, locale weekday ordering, custom accessibility semantics)
   for a component that already exists, is accessible out of the box, and only needs recoloring.
   The trade-off is not pixel-identical cell art for a few details (today's dot position, exact
   corner radii) — judged an acceptable trade against the risk/size of a hand-rolled grid.
2. **No Cancel button** on any of the three screens — matches the design source exactly (none of
   the three reference screenshots has one; dismissal is the header's × only). This *is* a change
   from the prior bottom-sheet pattern elsewhere in the app (which pairs Cancel + Apply), but it's
   what the actual dev handoff spec calls for on all three of these screens specifically.
3. **Range-picker Start/End highlight and reset revised on product review** — the source JSX's
   `active={!rangeEnd}` on the Start stub double-highlights both stubs while a range is
   half-picked, an apparent quirk of the prototype's own state logic. After review, each stub now
   highlights independently based on its own unset state (`active = value == null`) — both
   highlighted by default, each drops its highlight the instant its own date is picked. The Start
   stub is also now tappable, resetting the whole range (`setSelection(null, null)`) — an
   interaction the mockup's static, non-interactive `DateStub` div has no equivalent for.
4. **Event date range is now a true range picker**, replacing two independent single-date pickers
   that had no start≤end enforcement — a functional fix the static mockup never had to represent,
   not a fidelity question.

## Verification (Step 6 gate, this repo's `AGENTS.md`)

- ✅ `gradle :shared:compileDebugKotlinAndroid`, `:app:compileDevDebugKotlin`,
  `:app:compileDevDebugUnitTestKotlin` — all modules touched (`shared`, `feature:debt`,
  `feature:eventbudget`, `feature:logging`, `app`) compile clean.
- ✅ `gradle :shared:ktlintFormat` — no outstanding issues after formatting.
- ✅ `gradle :shared:detektAndroidDebug :shared:detektAndroidRelease` and the same for
  `feature:debt`/`feature:eventbudget`/`feature:logging` — baseline regenerated only for the
  expected, already-documented pattern (new `@Composable`s need `FunctionNaming`/`LongMethod`/
  `LongParameterList`/`UnusedPrivateMember`-on-`@Preview` baseline entries; two genuine
  `MaxLineLength` findings were fixed as real code changes, not baselined).
- ✅ `gradle :app:testDevDebugUnitTest` — full unit suite green.
- ✅ `gradle :app:verifyRoborazziDevDebug` — full screenshot suite green, including the
  re-recorded `add_date_time_sheet` baseline and four new `PickerScreenshotTest` baselines
  (`date_picker_empty_clearable`, `date_picker_selected`, `date_range_picker_empty`,
  `date_range_picker_set`).
- ✅ `gradle verifyAll -x :app:detekt` (`:app:detekt` excluded for a pre-existing, unrelated
  `MoreFlow.kt` failure confirmed present on the base branch before any of this session's work).
- Post-implementation `compose-product-auditor` pass: one Low finding (no `BackHandler` for the
  Android system back button on any of the three full-screen picker screens) — confirmed
  consistent with this app's existing convention (`ProBottomSheetHost` and the prior bottom-sheet
  picker also lack one), not a regression introduced by this rebuild. Left as a non-blocking,
  possible app-wide follow-up rather than fixed in this pass.

## Sign-off

Implementation matches the authoritative dev handoff on every element except the four deviations
above, each with a stated engineering or product reason. The AM/PM placement was initially built
wrong (a separate full-width toggle instead of the spec's compact in-box stack) — caught on user
review and corrected before this sign-off, not left as an accepted deviation. No open blockers.
Items worth a design opinion if strict pixel-parity matters more than the stated trade-offs: the
today-marker rendering (deviation 1) and the range-picker double-highlight quirk (deviation 3).
