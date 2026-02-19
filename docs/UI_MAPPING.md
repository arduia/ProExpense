# UI Mapping: XML Layouts vs Kotlin Classes

This document provides a mapping between the legacy XML layout files and their corresponding Kotlin
classes (Fragments, Activities, Dialogs, or Adapters).

## 📱 Screens (Activities & Fragments)

| XML Layout File               | Kotlin Class (Legacy)         | Kotlin Class (Compose)   | Description                                   |
|:------------------------------|:------------------------------|:-------------------------|:----------------------------------------------|
| `activity_main.xml`           | `MainActivity.kt`             | `MainHost.kt` (Planned)  | Main entry point and navigation host.         |
| `fragment_home.xml`           | `HomeFragment.kt`             | `HomeScreen.kt`          | Home dashboard with summary and recent items. |
| `fragment_statistic.xml`      | `StatisticsFragment.kt`       | `StatisticScreen.kt`     | Expense statistics and category breakdown.    |
| `fragment_expense_logs.xml`   | `ExpenseFragment.kt`          | `ExpenseLogsScreen.kt`   | List of all expense transaction logs.         |
| `fragment_expense_entry.xml`  | `ExpenseEntryFragment.kt`     | `ExpenseEntryScreen.kt`  | Screen to add or edit an expense.             |
| `fragment_backup.xml`         | `BackupFragment.kt`           | `BackupScreen.kt`        | Data backup and restore functionality.        |
| `fragment_settings.xml`       | `SettingsFragment.kt`         | `SettingsScreen.kt`      | App settings menu.                            |
| `fragment_about.xml`          | `AboutFragment.kt`            | `AboutScreen.kt`         | About app and version information.            |
| `fragment_feedback.xml`       | `FeedbackFragment.kt`         | `FeedbackScreen.kt`      | User feedback form.                           |
| `fragment_web.xml`            | `WebFragment.kt`              | `WebScreen.kt`           | In-app browser for privacy policy, etc.       |
| `fragment_splash.xml`         | `SplashFragment.kt`           | `SplashScreen.kt`        | Initial launch screen.                        |
| `fragment_onboard_config.xml` | `OnBoardingConfigFragment.kt` | `OnboardConfigScreen.kt` | Initial setup for currency and language.      |

## 💬 Dialogs

| XML Layout File                       | Kotlin Class (Legacy)          | Kotlin Class (Compose)                 | Description                             |
|:--------------------------------------|:-------------------------------|:---------------------------------------|:----------------------------------------|
| `filter_expense_dialog.xml`           | `FilterExpenseDialog.kt`       | `FilterExpenseDialog.kt`               | Date range and sort order filter.       |
| `fragment_export_dialog.xml`          | `ExportDialogFragment.kt`      | `ExportDialog.kt`                      | Export data to CSV/Excel.               |
| `fragment_feedback_status_dialog.xml` | `FeedbackStatusDialog.kt`      | `FeedbackStatusDialog.kt` (View-based) | Success message after sending feedback. |
| `choose_theme_dialog.xml`             | `ChooseThemeDialog.kt`         | `ChooseThemeDialog.kt` (View-based)    | Light/Dark theme selector.              |
| `fragment_choose_currency_dialog.xml` | `ChooseCurrencyDialog.kt`      | `ChooseCurrencyDialogContent.kt`       | Currency selection dialog.              |
| `fragment_choose_language_dialog.xml` | `ChooseLanguageDialog.kt`      | `LanguageDialogContent.kt`             | Language selection dialog.              |
| `expense_detail_dialog.xml`           | `ExpenseDetailDialogScreen.kt` | `ExpenseDetailDialog.kt`               | Transaction details with actions.       |
| `fragment_about_update_dialog.xml`    | `AboutUpdateDialogScreen.kt`   | `AboutUpdateDialog.kt`                 | App update information.                 |
| `fragment_delete_confirm_dialog.xml`  | `DeleteConfirmDialogScreen.kt` | `DeleteConfirmDialog.kt`               | Generic styled confirmation dialog.     |

## 🧩 List Items (Adapters)

| XML Layout File                | related Adapter / Class           | Description                                    |
|:-------------------------------|:----------------------------------|:-----------------------------------------------|
| `item_expense_log.xml`         | `ExpenseLogAdapter.kt`            | Main transaction item in logs list.            |
| `item_expense_recent.xml`      | `RecentListAdapter.kt`            | Recent transaction item on Home screen.        |
| `item_category.xml`            | `CategoryListAdapter.kt`          | Category item in expense entry.                |
| `item_category_statistic.xml`  | `CategoryStatisticListAdapter.kt` | Category item with progress bar in statistics. |
| `item_currency.xml`            | `CurrencyListAdapter.kt`          | Currency item in selection list.               |
| `item_language.xml`            | `LangListAdapter.kt`              | Language item in selection list.               |
| `item_backup.xml`              | `BackupListAdapter.kt`            | Backup file item in restore list.              |
| `item_expense_date_header.xml` | `ExpenseLogAdapter.kt` (Header)   | Date separator in transaction lists.           |

## 🏗️ Reusable Layouts & Components

| XML Layout File              | Related Class / Component       | Description                             |
|:-----------------------------|:--------------------------------|:----------------------------------------|
| `layout_toolbar.xml`         | `ToolbarLayout.kt`              | Standard app toolbar.                   |
| `layout_header.xml`          | `HeaderLayout.kt`               | Navigation drawer or main header.       |
| `layout_search_box.xml`      | `MaterialSearchBox.kt`          | Custom search view.                     |
| `layout_expense_graph.xml`   | `SpendGraph.kt` (Custom View)   | Weekly spending bar chart.              |
| `layout_expense_in_out.xml`  | `HomeFragment.kt` (Included)    | Income/Outcome summary card.            |
| `layout_no_expense_logs.xml` | `ExpenseFragment.kt` (Included) | Empty state view for logs.              |
| `layout_recent_lists.xml`    | `HomeFragment.kt` (Included)    | Container for recent transactions list. |
