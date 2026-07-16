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
  `DatePicker` in single-select mode, a "TIME" eyebrow label, then the hour/minute spinner box
  (unchanged from the prior chip-based version: chevron-up / big number / chevron-down per field,
  centered colon separator, all inside one bordered card), an AM/PM `SegmentedToggle`, and a
  conditional future-date notice. Footer: one full-width primary Apply button — no Cancel, matching
  the design source (dismissal is the header's X).
- **`DatePickerScreen`** (debt due date, optional) — an M3 `DatePicker` filling the screen. Footer:
  secondary Clear (only rendered when `allowClear = true`) + primary button whose label mirrors
  state (`Select a date` while nothing's picked, `Use {date}` once one is). This is the one place
  the rebuild fixes a real, pre-existing gap: due date was optional in the data model but had no UI
  path back to "unset" once a date was picked — Clear closes that gap.
- **`DateRangePickerScreen`** (event start/end) — a Start/End stub row (whichever is awaiting input
  is highlighted) above an M3 `DateRangePicker` in range mode, with the same bottom fading-edge
  trick `JournalDateRangeSheet` uses for the next-month preview row. Footer: one full-width primary
  button, disabled until both ends are set, labeled `Pick a start/end date` while incomplete or
  `Use {start} — {end}` once complete. Replaces two independent single-date pickers (no start≤end
  enforcement) with one true range picker.

## Deliberate deviations from the design source

- **AM/PM** is a full-width `SegmentedToggle` below the time box, not the mock's small stacked
  buttons beside it — larger touch target, reuses an existing design-system component.
- **No Cancel button** on any of the three screens (matches the source, which only has Apply/Use +
  the header's X) — a change from the old bottom sheet, which had both Cancel and Apply.
- **Debt due date's fallback/Clear affordance** and **event date range's true range mode** are both
  functional improvements the static design mockup never had to represent, not fidelity gaps.
- The range picker's Start/End stub highlighting is simplified from the source JSX, which
  double-highlights both stubs while a range is half-picked (`active={!rangeEnd}` on Start) — an
  apparent quirk in the mockup's own logic. This implementation highlights exactly one stub at a
  time: Start when nothing's picked, End once Start is set and End isn't.

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
