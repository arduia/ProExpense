# Date & Time Picker

A full-screen month calendar, shared across three entry points — transaction date+time, event
date range, and debt due date — matching the Claude Design project's "Date & Time Picker — Dev
Handoff" spec (`date-picker.jsx`'s `CalendarMonth`/`PickerScreenShell` + `flow-01-screens.jsx`'s
`DateTimeSheet`). Replaces an earlier bottom-sheet, quick-pick-chip version. See
[dev sign-off](../dev-signoff/date-time-picker-signoff.md) for the fidelity verification.

![Date & time picker](../screenshots/date-time-picker.png)

## Shell (shared by all three screens)

Full-screen page, not a bottom sheet or dialog — `PickerScreenShell` (`shared/.../ui/design/
PickerScreenShell.kt`). Header: Close (×) pinned left, title centered, no right action. Body
scrolls if content overflows; footer is a fixed action row at the bottom. Enters/exits via the
same push-transition (`ProMotion.forwardScreenEnter`/`forwardScreenExit`) this app's other
full-screen overlays use (`PinLockFlow`/`PinEntryScreen`) — not the bottom sheet's slide-up +
scrim, since there's no "outside" to dim on a full page.

## Three screens, one calendar

- **`DateTimePickerScreen`** (transaction date + time) — a "DATE" eyebrow label, an M3
  `DatePicker` in single-select mode, a "TIME" eyebrow label, then one bordered card containing
  the hour/minute spinner (chevron-up / big number / chevron-down per field, centered colon
  separator) **and** a compact AM/PM stack (`TimeMeridiemToggle`) side by side, plus a conditional
  future-date notice. Footer: one full-width primary Apply button — no Cancel, matching the design
  source (dismissal is the header's X).
- **`DatePickerScreen`** (debt due date, optional) — an M3 `DatePicker` filling the screen. Footer:
  secondary Clear (only rendered when `allowClear = true`) + primary button whose label mirrors
  state (`Select a date` while nothing's picked, `Use {date}` once one is). This is the one place
  the rebuild fixes a real, pre-existing gap: due date was optional in the data model but had no UI
  path back to "unset" once a date was picked — Clear closes that gap.
- **`DateRangePickerScreen`** (event start/end) — a Start/End stub row above an M3
  `DateRangePicker` in range mode, with the same bottom fading-edge trick `JournalDateRangeSheet`
  uses for the next-month preview row. Each stub is independently highlighted (tinted background +
  colored border) whenever *that* stub's own value is unset — both show highlighted "Select" by
  default, and each drops its highlight the moment its own date is picked, independent of the
  other stub. Tapping the **Start** stub itself (not just the calendar) resets the whole range via
  `DateRangePickerState.setSelection(null, null)`, clearing both start and end so the user can
  restart selection from scratch. Footer: one full-width primary button, disabled until both ends
  are set, labeled `Pick a start/end date` while incomplete or `Use {start} — {end}` once complete.
  Replaces two independent single-date pickers (no start≤end enforcement) with one true range
  picker.

## Deliberate deviations from the design source

- **No Cancel button** on any of the three screens (matches the source, which only has Apply/Use +
  the header's X) — a change from the old bottom sheet, which had both Cancel and Apply.
- **Debt due date's fallback/Clear affordance** and **event date range's true range mode** are both
  functional improvements the static design mockup never had to represent, not fidelity gaps.
- **Hour/minute chevron tap targets are ~24dp** (small icon + small padding via
  `proCircularRippleClickable`, not the usual `proIconClickable`'s 48dp floor) — matching the
  design's compact chevron size on explicit user request, below this repo's usual 48dp touch-target
  guidance. A deliberate, flagged trade-off, not an oversight.
- **Range picker Start/End stub highlighting and reset** were revised after product review: each
  stub highlights independently based on its *own* unset state (not the source JSX's
  `active={!rangeEnd}`-on-Start logic, which double-highlights both stubs while a range is
  half-picked — an apparent quirk in the mockup), and the Start stub is now tappable to reset the
  whole range, which the mockup's static `DateStub` (a plain non-interactive `<div>`) has no
  equivalent for.

## Behavior

- Tapping a calendar day only updates local picker state — it does not commit or close the screen.
- Apply/Use combines the picked value(s) and closes; there is no explicit Cancel — the header's X
  discards local state.
- `DatePickerScreen`'s Clear calls `onClear()` then closes, independent of whatever day is
  currently selected in the calendar.

## Compose notes

All three screens are hosted in `PickerScreenShell` and theme their M3 `DatePicker`/
`DateRangePicker` via `DatePickerTheming.kt`'s `proDatePickerColors()`/`proDatePickerTypography()`
— the same theming already used by `JournalDateRangeSheet` and (previously) the old chip sheet's
fallback calendar dialog. Date-math helpers (`startOfDay`, `addDays`, `calendarAt`) live in
`PickerDateMath.kt`, shared across the three screen files to avoid duplicating the same `Calendar`
logic three times. The `TimeSpinner`/`SectionEyebrow`/`FutureDateNotice` composables carry over
unchanged from the prior chip-based `DateTimePickerSheet.kt`.
