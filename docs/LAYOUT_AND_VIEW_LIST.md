# PRO Expense - Layout Files and Custom Views

## 📂 Layout Files (XML)
### App Module (`app/src/main/res/layout/`)
- `activity_main.xml`
- `choose_theme_dialog.xml`
- `expense_detail_dialog.xml`
- `filter_expense_dialog.xml`
- `fragment_about.xml`
- `fragment_about_update_dialog.xml`
- `fragment_backup.xml`
- `fragment_backup_detail.xml`
- `fragment_choose_currency.xml`
- `fragment_choose_currency_dialog.xml`
- `fragment_choose_language.xml`
- `fragment_choose_language_dialog.xml`
- `fragment_delete_confirm_dialog.xml`
- `fragment_expense_entry.xml`
- `fragment_expense_logs.xml`
- `fragment_export_dialog.xml`
- `fragment_feedback.xml`
- `fragment_feedback_status_dialog.xml`
- `fragment_home.xml`
- `fragment_lang_dialog.xml`
- `fragment_onboard_config.xml`
- `fragment_settings.xml`
- `fragment_splash.xml`
- `fragment_statistic.xml`
- `fragment_web.xml`
- `item_backup.xml`
- `item_category.xml`
- `item_category_statistic.xml`
- `item_currency.xml`
- `item_expense_date_header.xml`
- `item_expense_log.xml`
- `item_expense_recent.xml`
- `item_language.xml`
- `layout_expense_graph.xml`
- `layout_expense_in_out.xml`
- `layout_header.xml`
- `layout_no_expense_logs.xml`
- `layout_recent_lists.xml`
- `layout_search_box.xml`
- `layout_toolbar.xml`

## 🎨 Custom Views (Kotlin)
### Swipe Layout
- **Class**: `com.arduia.expense.ui.expenselogs.swipe.SwipeFrameLayout`
- **Location**: `app/src/main/java/com/arduia/expense/ui/expenselogs/swipe/SwipeFrameLayout.kt`
- **Usage**: Used in `item_expense_log.xml` for swipe-to-delete functionality.

### Search Box
- **Class**: `com.arduia.expense.ui.common.customview.MaterialSearchBox`
- **Location**: `app/src/main/java/com/arduia/expense/ui/common/customview/MaterialSearchBox.kt`
- **Usage**: Used in `fragment_choose_language_dialog.xml` (and potentially others).

### Expense Graph
- **Class**: `com.arduia.graph.SpendGraph`
- **Module**: `:week-expense-graph`
- **Location**: `week-expense-graph/src/main/java/com/arduia/graph/SpendGraph.kt`
- **Usage**: Used in `layout_expense_graph.xml` to display weekly expense data.
