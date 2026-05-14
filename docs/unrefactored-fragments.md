# Unrefactored Fragments (Pending Compose Migration)

Fragments that still use the legacy View/ViewBinding approach and have not yet been integrated with Jetpack Compose.

## Settings

| Fragment | Path |
|---|---|
| `ChooseCurrencyDialog` | `ui/settings/ChooseCurrencyDialog.kt` |

## Splash

| Fragment | Path |
|---|---|
| `SplashFragment` | `ui/splash/SplashFragment.kt` |

## Web

| Fragment | Path |
|---|---|
| `WebFragment` | `ui/web/WebFragment.kt` |

## Backup

| Fragment | Path |
|---|---|
| `ExportDialogFragment` | `ui/backup/ExportDialogFragment.kt` |
| `ImportDialogFragment` | `ui/backup/ImportDialogFragment.kt` |

## Onboarding

| Fragment | Path |
|---|---|
| `OnBoardingConfigFragment` | `ui/onboarding/OnBoardingConfigFragment.kt` |
| `ChooseCurrencyFragment` | `ui/onboarding/ChooseCurrencyFragment.kt` |

## Common

| Fragment | Path |
|---|---|
| `ExpenseDetailDialog` | `ui/common/expense/ExpenseDetailDialog.kt` |
| `DeleteConfirmFragment` | `ui/common/delete/DeleteConfirmFragment.kt` |
| `ExpenseFilterDialogFragment` | `ui/common/filter/ExpenseFilterDialogFragment.kt` |

---

> Note: `NavBaseFragment` is a base class shared across screens — evaluate separately whether it needs a Compose equivalent.
