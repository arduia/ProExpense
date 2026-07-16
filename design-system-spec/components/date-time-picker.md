# Date & Time Picker

Quick-pick date chips + a tap-to-step time spinner, for setting an expense/debt/event date without
typing or a full calendar grid.

![Date & time picker](../screenshots/date-time-picker.png)

- **Date row**: 7 `FilterChip`s, `today − 3` … `today + 3`, horizontally scrollable with a fading
  edge. Near days read "Today" / "Yesterday"; the rest read `EEE · MMM d`. A trailing chip
  ("Pick another date") opens a full calendar as a fallback for dates outside the 7-day window —
  necessary because this sheet also edits older records (a debt due date, a past expense) that
  routinely fall outside it. When the selected date isn't one of the 7 chips, none of them show
  selected and a small header line above the row states the resolved date instead.
- **Time spinner**: two steppers — hour (1–12) and minute (00–59) — each a chip-shaped, bordered
  circle with a chevron-up / big number / chevron-down stack. Single tap = ±1, wrapping at the
  ends. Below them, an AM/PM segmented toggle.
- **Future-date notice**: when the resolved date is after today, a `primaryTint`-background info
  row appears above the action buttons ("entries are ordered by created date, not expense date").
- Footer: two buttons, gap 8dp, each `weight(1f)` — `Secondary` Cancel + `Primary` Apply. Unlike
  most sheets, selecting a chip or stepping a spinner does **not** auto-close — the sheet closes
  only on Apply or Cancel.

## Motion
- Inherits the bottom sheet's `sheet-up` enter (see [bottom-sheet.md](bottom-sheet.md)).
- Chip selection uses the same press-scale as any other `FilterChip`; no bespoke motion beyond that.

## Behavior
- Tapping a date chip or a spinner chevron only updates local sheet state — it does not commit or
  close the sheet.
- The fallback calendar dialog commits its own selection back into the sheet's date state
  immediately on tap, then closes itself; the outer sheet still requires its own Apply to commit.
- Apply combines the selected day + hour/minute/AM-PM into one epoch-millis value and closes the
  sheet; Cancel discards all local changes.

## Compose notes
Custom composable (`DateTimePickerSheet`, `shared/.../ui/design/DateTimePickerSheet.kt`) hosted in
`ProBottomSheetHost`. Date chips reuse `FilterChip` (`Fields.kt`); AM/PM reuses `SegmentedToggle`.
The chip row's edge fade is a private `horizontalFadingEdge` modifier (`drawWithContent` +
`BlendMode.DstIn`, wraps `horizontalScroll` — apply before it in the modifier chain, not nested
inside it), the horizontal counterpart to `CategoryNewSheet.kt`'s `verticalFadingEdge`. The
fallback calendar reuses the M3 `DatePicker` + `proDatePickerColors()`/`proDatePickerTypography()`
(`DatePickerTheming.kt`) inside a scrim-backed `Surface` overlay, matching `ProAlertDialog`'s visual
language rather than a system `Dialog`.
