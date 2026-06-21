# Pro Expense — Design System Spec

Implementation reference for the **Pro Expense** finance tracker, extracted from the shipped Hi-Fi build. Written for a **Jetpack Compose** implementation: tokens, dimensions (dp), states, and Material 3 mapping notes. No Kotlin bodies — these are specs, not source.

**Companion page:** [`Component Gallery.html`](Component%20Gallery.html) — the same components rendered live, each in an isolated spec section.

## Foundations
- [**Design Tokens**](tokens.md) — colour, typography, shape, spacing, elevation, motion + M3 `ColorScheme` / `Typography` mapping.

## Components
| Component | Spec | M3 mapping |
|---|---|---|
| Button | [button.md](components/button.md) | `Button` / `OutlinedButton` / `TextButton` |
| Category badge & chip | [category.md](components/category.md) | custom / `FilterChip` |
| Filter chip | [filter-chip.md](components/filter-chip.md) | `FilterChip` |
| Transaction row & day group | [transaction-row.md](components/transaction-row.md) | custom |
| Card & surfaces | [card-surfaces.md](components/card-surfaces.md) | `Card` / `Surface` |
| Amount entry (keypad + validation) | [amount-entry.md](components/amount-entry.md) | custom |
| Search field | [search-field.md](components/search-field.md) | `BasicTextField` |
| Quick-access tiles | [quick-access.md](components/quick-access.md) | custom `Surface` |
| Bottom navigation | [bottom-nav.md](components/bottom-nav.md) | `NavigationBar` + custom FAB |
| Bottom sheet | [bottom-sheet.md](components/bottom-sheet.md) | `ModalBottomSheet` |
| Toast | [toast.md](components/toast.md) | `Snackbar` |
| Iconography | [icons.md](components/icons.md) | `ImageVector` |

## Conventions
- **1 CSS px = 1 dp.** Tracking values are `em`.
- Single **light** theme. Surfaces are pure white; elevation is drop-shadow only (no M3 tonal tint).
- `onPrimary` / on-filled text is warm white **`#FFFDF6`**, not `#FFFFFF`.
- `--sans` = **Manrope**, `--mono` = **Geist Mono**, `--display` = **Inter** (titles + amounts).

_Screenshots in [`screenshots/`](screenshots/) are captured from the live components._
