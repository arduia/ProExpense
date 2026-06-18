# Finance Tracker — Screen Flow Document
*Product: Finance Tracker | Version: 1.0 | Status: Final*

---

## Screen Index

| # | Screen Name | Purpose Summary |
|---|-------------|-----------------|
| 01 | Splash | Initial loading screen shown briefly when the app launches |
| 02 | Onboarding | Introduces MVP features to new users through a brief swipeable slide flow |
| 03 | Home | Central hub of the app with recent activity and quick logging access |
| 04 | Add Expense | Core logging screen to record an expense in under 5 seconds |
| 05 | Journal | Diary-style view of spending history, organized by day |
| 06 | Journal Detail | Full details of a single journal entry or all entries for a selected day |
| 07 | Event Budget | Create a budget for a specific event and track spending against it |
| 08 | Event Detail | Drill-down view for a specific event showing all logged expenses |
| 09 | Debt Tracker | Simple personal record of money lent or owed |
| 10 | Shared Costs | Track and split expenses among a group of people |
| 11 | Category List | Manage expense categories used across the app |
| 12 | Reports | Visual summary of spending patterns over time |
| 13 | More / Settings | Combined hub for secondary features and app configuration |
| 14 | PIN Setup | Enable and configure 6-digit PIN authentication |
| 15 | PIN Entry | Session authentication screen shown on every app launch if PIN is enabled |

---

## Empty States

> All empty states follow the **Soft Illustrated** style — rounded, slightly playful, pastel tones consistent with the app's Sage `#C8D8C8` and Warm Peach `#F5C5B0` color palette.

| Screen | Illustration | Message | CTA |
|--------|-------------|---------|-----|
| Home | Empty wallet or open notebook | "No expenses yet. Start by logging your first one!" | Add (+) button |
| Journal | Open diary with blank pages | "Your journal is empty. Log an expense to see it here." | Add (+) button |
| Budget | Empty jar or piggy bank | "No active events. Create one to start tracking." | Create Event button |
| Debt Tracker | Two hands with a coin between them | "No records yet. Track who owes who here." | Add Record button |
| Shared Costs | Group of people around a table | "No splits yet. Use this to split a bill instantly." | Split Bill button |
| Reports | Empty bar chart with a magnifying glass | "No data yet. Start logging to see your spending insights." | Add (+) button |

### Empty State Design Principles

| Principle | Detail |
|-----------|--------|
| Tone | Friendly and encouraging — never "Error" or "Nothing found" |
| Illustration style | Soft illustrated — rounded, playful, pastel tones |
| CTA | Always one clear action to guide the user forward |
| Color palette | Consistent with app palette — Sage, Warm Peach, and neutral pastels |

---

## Use Case Screen Flows

### 🚀 First Launch
```
Splash → Onboarding → Home
```

### 🔄 Returning User (PIN disabled)
```
Splash → Home
```

### 🔐 Returning User (PIN enabled)
```
Splash → PIN Entry → Home
```

---

### 1. Quick Manual Logging
> One-tap expense entry with minimal fields. Log in under 5 seconds.

```
Home → Add Expense → Home
```

| Step | Screen | Action |
|------|--------|--------|
| 1 | Home | Tap the floating Add (+) button |
| 2 | Add Expense | Enter amount, pick category, optional note → Save |
| 3 | Home | Entry appears in recent transactions list |

---

### 2. Financial Journal
> A diary-like experience to jot down and review daily spending.

```
Home → Journal → Journal Detail → (Edit) → Journal
```

| Step | Screen | Action |
|------|--------|--------|
| 1 | Home | Navigate to Journal via bottom nav (Journal tab) |
| 2 | Journal | Browse date-grouped entries, tap a day |
| 3 | Journal Detail | View all entries for that day |
| 4 | Journal Detail | Tap an entry to add or edit a note |

---

### 3. Event Budget
> Set a budget for a specific event and track spending in real time.

```
Home → Event Budget → (Create Event) → Add Expense → Event Detail
```

| Step | Screen | Action |
|------|--------|--------|
| 1 | Home | Navigate to Event Budget via Budget tab in bottom nav |
| 2 | Event Budget | Tap Create, enter event name, date range, and budget |
| 3 | Add Expense | Log an expense, use @ tag to link to the event |
| 4 | Event Detail | View remaining balance and itemized expense list |

---

### 4. Debt & Lending Tracker
> Record money lent or owed without any bank connection.

```
Home → Debt Tracker → (Add Record) → Debt Tracker → (Mark Settled)
```

| Step | Screen | Action |
|------|--------|--------|
| 1 | Home | Navigate to Debt Tracker via More in bottom nav |
| 2 | Debt Tracker | Toggle "I Lent" or "I Owe", enter name and amount → Save |
| 3 | Debt Tracker | View list of active debts and loans |
| 4 | Debt Tracker | Tap a record and mark as settled when resolved |

---

### 5. Shared Costs
> Quickly split a bill on the spot — no group setup needed.

```
Home → Shared Costs → Split Summary (sub-screen) → Save → Shared Costs (history)
```

| Step | Screen | Action |
|------|--------|--------|
| 1 | Home | Navigate to Shared Costs via More in bottom nav |
| 2 | Shared Costs | Enter total amount, number of people, optional names |
| 3 | Split Summary | Sub-screen shows per person amount, adjust if custom split |
| 4 | Save | Saves total bill amount, returns to Shared Costs history |

---

### 6. View Reports *(Supporting Flow)*
```
Home → Reports
```

| Step | Screen | Action |
|------|--------|--------|
| 1 | Home | Navigate to Reports via More in bottom nav |
| 2 | Reports | Select period, view charts and category breakdown |

---

### 7. Manage Categories *(Supporting Flow)*
```
Home → More → Category List
```

| Step | Screen | Action |
|------|--------|--------|
| 1 | Home | Navigate to More in bottom nav |
| 2 | More | Tap Category List |
| 3 | Category List | Add, edit, or delete categories |

---

## Screen Details

### 01. Splash

| Field | Details |
|-------|---------|
| **Purpose** | Initial loading screen shown briefly when the app launches. |
| **Key Elements** | App logo, App name |
| **Behavior** | Displays for ~1.5–2 seconds. Auto-navigates to Onboarding (first launch), PIN Entry (returning user with PIN enabled), or Home (returning user without PIN). No user interaction required. |
| **Notes** | No tagline. No login or signup prompt. Minimal and fast. |

---

### 02. Onboarding

| Field | Details |
|-------|---------|
| **Purpose** | Introduces MVP features to new users through a brief slide flow before landing on Home. |
| **Key Elements** | 5 onboarding slides, Skip button (every slide), Get Started CTA (last slide only) |
| **Behavior** | Shown on first launch only. Skip navigates directly to Home. Get Started on last slide navigates to Home. |
| **Notes** | Swipeable slides. No use case selection — user discovers features naturally. |

#### Onboarding Slides

| # | Title | Description |
|---|-------|-------------|
| 1 | Welcome | "Your personal finance notebook" — simple intro, sets the tone |
| 2 | Quick Log | "Log expenses in seconds" — highlight one-tap entry |
| 3 | Journal | "Review your spending like a diary" — date-grouped spending view |
| 4 | Event Budget | "Plan and track any event budget" — real-time balance for any occasion |
| 5 | Shared Costs | "Split bills instantly" — enter total, number of people, done |

---

### 03. Home

| Field | Details |
|-------|---------|
| **Purpose** | Central hub of the app. Shows a contextual financial snapshot and provides quick access to logging. |
| **Key Elements** | Contextual header, Recent transactions list, Floating Add (+) button, Active event card (conditional), Bottom navigation bar |
| **Bottom Nav** | Home, Budget, Journal, More |
| **Floating Add (+)** | Visible across all screens except PIN Entry and PIN Setup — navigates to Add Expense |
| **Notes** | Header adapts per user context. Keep the dashboard clean — avoid information overload per the Simplicity First principle. |

#### Contextual Header (Per Persona)

| Persona | Condition | Header Shows |
|---------|-----------|-------------|
| Casual Spender | No budget set | Total spent this month |
| Budget Planner | Monthly budget set | Spent vs. budget (e.g. $320 / $500) |
| Event Organizer | Active event running | Event name + remaining balance |

#### Screen Elements

| Element | Detail |
|---------|--------|
| Header | Contextual summary based on user setup (see above) |
| Active Event Card | Shown only when an event is currently active — displays event name and remaining balance |
| Recent Transactions | Last 5–10 entries with amount, category, and date |
| Floating Add (+) | Visible across all screens except PIN Entry and PIN Setup — navigates to Add Expense |
| Bottom Nav | Home, Budget, Journal, More |

---

### 04. Add Expense

| Field | Details |
|-------|---------|
| **Purpose** | Core logging screen. Allows users to record an expense in under 5 seconds. |
| **Flow** | Add Expense (Amount) → Add Expense (Details) → Home |
| **Notes** | Split into two sub-screens to keep focus. Amount first, details second. Receipt attachment not included in MVP. |
| **Post-MVP** | Voice input for amount and category — PRD requirement deferred to post-MVP |

#### Sub-screen 1 — Amount Input

| Element | Detail |
|---------|--------|
| Amount display | Large, centered, prominent |
| Numeric keypad | Auto-opens on screen load |
| Category chips | Horizontal scroll — "Food" auto-selected by default, changeable |
| Next button | Proceeds to Details screen — disabled until amount > $0 |
| Save button | Saves with amount + default category — disabled until amount > $0 |

#### Edge Cases — Amount Input

| Scenario | Behavior |
|----------|----------|
| Back tapped with no amount | Silent back navigation — no prompt, no save |
| Amount entered is $0 | Save and Next buttons disabled — inline error "Amount must be greater than $0" |
| Back tapped from Details screen | Returns to Amount screen with previously entered amount persisted |
| App force-closed mid-entry | Draft auto-saved — on next launch prompt shown before PIN Entry: "You have an unfinished expense. Continue or discard?" — user can save or discard without authenticating |
| Future date selected | Allowed — entries are ordered by created date, not expense date |

#### Sub-screen 2 — Details

| Element | Detail |
|---------|--------|
| Amount | Shown at top, read-only — tappable to go back and edit |
| Category chips | Required — pre-selected if chosen on Amount screen |
| Date | Defaults to today, tappable to change |
| Note | Optional, single line — max 200 characters |
| @ Tag | Optional — links expense to an active Event or Debt record (one type only). Hidden if no active events or debts. See Unified @ Tag Field rules. |
| Save button | Large, always visible |

#### Category Chips

| Type | Categories |
|------|-----------|
| Default | Food, Transport, Shopping, Bills, Health, Entertainment |
| Overflow | "+ More" opens full category list |
| Style | Horizontal scroll chips (Option B) — compact, stays in flow |

#### Unified @ Tag Field

| Condition | Behavior |
|-----------|----------|
| No active events or debts | @ field hidden in Details sub-screen |
| 1+ active events or debts | @ field shown as optional in Details sub-screen |
| Tapping @ | Opens combined picker grouped by type (Events / Debts) |
| Selection | Single selection only — either an Event OR a Debt, not both |
| After selection | The other type is greyed out and unselectable |
| Clear tag | Resets selection, both types become available again |
| Tagged to Event | Expense appears in Journal and linked Event Budget |
| Tagged to Debt | Expense appears as reference in the linked Debt record |
| Post-MVP | Multi-tag support per expense |



---

### 05. Journal

| Field | Details |
|-------|---------|
| **Purpose** | Diary-style view of all spending history, organized by day. |
| **History** | Shows all-time history — no cutoff or period limit |
| **Key Elements** | Date-grouped entries (always expanded), Daily total per group, Entry row (amount, category icon, note preview, time), Search bar (by keyword, note, or amount), Category filter, Empty state message |
| **Notes** | Days are always expanded for easy scanning. No collapsing. Tapping an entry opens Journal Detail for editing or adding notes. |
| **Quick Note** | Long press on any entry opens a quick note bottom sheet — type and save without leaving the Journal list |
| **Ordering** | Entries grouped by expense date — within each day group, ordered by created date (newest first) |
| **Search Results** | When search is active — flat list with no date grouping, shows amount, category, date and note |

---

### 06. Journal Detail

| Field | Details |
|-------|---------|
| **Purpose** | Shows full details of a single expense entry with options to edit or delete via a bottom sheet. |
| **Key Elements** | Amount (large, prominent), Category icon + label, Date & time, Note (editable inline), Action button (opens bottom sheet), Back navigation |
| **Notes** | Editing and deletion are handled via a bottom sheet to keep the detail view clean and uncluttered. |

#### Screen Elements

| Element | Detail |
|---------|--------|
| Amount | Large, shown prominently at top |
| Category | Icon + label |
| Date & Time | When the entry was logged |
| Note | Displayed inline — tappable to edit in place |
| Action button | Opens bottom sheet for edit or delete |
| Back navigation | Returns to Journal |

#### Bottom Sheet Actions

| Action | Behavior |
|--------|----------|
| Edit | Opens Add Expense (Details) pre-filled with current entry data — saves and returns to Journal list |
| Delete | Shows confirmation dialog before removing the entry — returns to Journal list after deletion |
| Cancel | Dismisses the bottom sheet |

---

### 07. Event Budget

| Field | Details |
|-------|---------|
| **Purpose** | Create and manage budgets for specific events. Track spending in real time via expense tagging. |
| **Key Elements** | Event cards list, Create event button, Each card shows event name, date range, remaining balance (live), mini progress bar |
| **Multi-event** | Multiple active events can coexist at the same time (e.g. Bali Trip + John's Wedding) |
| **Linking** | Expenses are linked via @ tag in Add Expense — tagged expenses appear here and in Journal |
| **Notes** | Event expenses follow the same flow as normal expenses. Remaining balance updates in real time on both the event card and Event Detail. |

#### Create Event (Bottom Sheet)

| Element | Detail |
|---------|--------|
| Event name | Text input — required, max 30 characters |
| Start date | Date picker — defaults to today, past dates allowed |
| End date | Date picker — must be same or after start date |
| Total budget | Amount input — required |
| Save button | Creates event and closes bottom sheet |
| Cancel | Dismisses bottom sheet without saving |

---

#### Event Card (List View)

| Element | Detail |
|---------|--------|
| Event name | Prominent, top of card |
| Date range | Start and end date |
| Remaining balance | Live — updates in real time as expenses are tagged |
| Mini progress bar | Visual indicator using budget color system |
| Tap | Opens Event Detail |

#### Screen Elements

| Element | Detail |
|---------|--------|
| Event name | User-defined (e.g. "Bali Trip", "John's Wedding") |
| Date range | Start and end date |
| Total budget | Set at creation, editable |
| Remaining balance | Live — total budget minus all linked expenses |
| Linked expense list | All expenses tagged to this event |
| Add expense shortcut | Opens Add Expense with this event pre-tagged |

---

### 08. Event Detail

| Field | Details |
|-------|---------|
| **Purpose** | Drill-down view for a specific event showing budget status and all linked expenses. |
| **Key Elements** | Event name, Date range, Budget summary (total, spent, remaining), Progress bar, Linked expense list, Add expense shortcut, Edit / Close event (bottom sheet) |
| **Notes** | Closed events are archived and read-only. Editing allowed within 24 hours of closing as a grace period. Over budget shows warning text with over amount. |

#### Progress Bar

| Range | State | Color | Hex |
|-------|-------|-------|-----|
| 0–100% | On track | Soft blue | `#B3D4E8` |
| 101–110% | Over budget | Warm yellow | `#F5E6A3` — warning text "Over budget by $X" |
| 110%+ | Significantly over | Soft red | `#E07070` — bold warning text "Over budget by $X" |

#### Edit Event (Bottom Sheet)

| Element | Detail |
|---------|--------|
| Event name | Pre-filled — editable |
| Start date | Pre-filled — editable, past dates allowed |
| End date | Pre-filled — editable, must be same or after start date |
| Total budget | Pre-filled — editable, must be greater than $0 |
| Save button | Updates event and closes bottom sheet |
| Cancel | Dismisses bottom sheet without saving |

#### Event State

| State | Behavior |
|-------|----------|
| Active | Fully editable, expenses can be linked via @ tag |
| Closed (within 24hrs) | Archived but editable — grace period for fixes |
| Closed (after 24hrs) | Read-only, no editing or new expense linking |
| Over budget | Progress bar red, warning text shown below bar with over amount |

#### Edge Cases — Event Budget

| Scenario | Behavior |
|----------|----------|
| Budget amount is $0 | Save button disabled — inline error "Budget must be greater than $0" |
| Duplicate event name | Blocked — inline error "An event with this name already exists" |
| Event name exceeds 30 characters | Input blocked at 30 characters — character counter shown below field |
| Start date in the past | Allowed — users can freely backdate events for personal tracking |
| Multiple overlapping active events | Home header shows the most recently created event |
| Event end date passes | Event remains active — manual close only, end date is reference only |
| Linked expense deleted from Journal | Event budget auto-recalculates immediately — remaining balance increases by deleted amount |

---

### 09. Debt Tracker

| Field | Details |
|-------|---------|
| **Purpose** | Simple personal record of money lent or owed — no bank connection required. |
| **Key Elements** | I Lent / I Owe toggle, Person name, Amount, Date, Optional note, Optional due date, Mark as settled, Settled history, Linked expense reference |
| **Colors** | I Lent → Sage `#C8D8C8` / I Owe → Warm Peach `#F5C5B0` — soft tones for easy distinction |
| **Notes** | No reminder notifications in MVP. Due date stored as reference only. Settled records visible but greyed out in the same list. |

#### Screen Elements

| Element | Detail |
|---------|--------|
| Toggle | Switches between "I Lent" and "I Owe" views |
| Add button | "+" in screen header — opens Add Record bottom sheet |
| Person name | Who you lent to or owe |
| Amount | How much |
| Date | When it was recorded |
| Note | Optional context e.g. "dinner at Nobu" — max 200 characters |
| Due date | Optional, stored as reference only — no reminders in MVP |
| Linked expense | Optional @ tag linking to an expense entry |
| Mark as settled | Resolves the record, moves to Settled section |

#### Add Record (Bottom Sheet)

| Element | Detail |
|---------|--------|
| Type | I Lent / I Owe toggle — pre-set based on current list view |
| Person name | Text input — required, max 30 characters |
| Amount | Amount input — required, must be greater than $0 |
| Date | Defaults to today, tappable to change |
| Due date | Optional date picker |
| Note | Optional, single line — max 200 characters |
| Linked expense | Optional @ tag linking to an expense entry |
| Save button | Creates record and closes bottom sheet |
| Cancel | Dismisses bottom sheet without saving |

#### List Structure

| Section | Behavior |
|---------|----------|
| Active | Shown at top, colored by type (Sage / Warm Peach) |
| Settled | Shown below active, greyed out, deletable |

#### Record Actions (Bottom Sheet)

| Action | Availability | Behavior |
|--------|-------------|----------|
| Edit | Active records only | Opens bottom sheet pre-filled with current data — save updates record |
| Mark as Settled | Active records only | Moves record to Settled section |
| Delete | Settled records only | Confirmation dialog before removing |
| Cancel | Always | Dismisses bottom sheet |

#### Edge Cases — Debt Tracker

| Scenario | Behavior |
|----------|----------|
| Debt amount is $0 | Save button disabled — inline error "Amount must be greater than $0" |
| Same person exists on opposite side | Soft warning shown — "John already has a record on the other side. Do you want to continue?" — Yes proceeds, No dismisses |
| Linked expense deleted from Journal | Warning shown — "This expense is linked to a debt record. Deleting it will remove the reference." — Yes deletes and removes link, No cancels |

#### Color System

| Type | Color | Hex |
|------|-------|-----|
| I Lent | Sage | `#C8D8C8` |
| I Owe | Warm Peach | `#F5C5B0` |

#### Deletion Rules

| Record Type | Deletable |
|-------------|-----------|
| Active record | ❌ Not deletable |
| Settled record | ✅ Deletable |
| Linked expense entry | ❌ Not deleted — only the debt link is removed |

#### Expense Linking (@ Tag)

| Condition | Behavior |
|-----------|----------|
| No debt records | @ field hidden in Add Expense |
| 1+ active records | @ field shown as optional in Add Expense Details |
| Tagged expense | Appears as reference in the debt record |
| MVP | Single debt record per expense |
| Post-MVP | Multiple debt links per expense |

---

### 10. Shared Costs

| Field | Details |
|-------|---------|
| **Purpose** | Quick bill splitter — enter total amount, number of people, and instantly see each person's share. |
| **Key Elements** | Total amount input, People count (stepper, min 2), Optional person names, Equal/custom split toggle, Per person amount display, Note (optional), Save button, History list |
| **Saving** | Saves the total bill amount as the expense entry — individual splits are reference only |
| **History** | Saved splits appear in Shared Costs history only — not in Journal |
| **Notes** | Names default to "Person 1, Person 2..." if not provided. Total is always the source of truth — no balance check needed. |
| **Use Cases** | Works for any one-time split — restaurant bills, travel expenses, roommate costs. Users repeat the flow for each new split. |
| **Post-MVP** | Running tab mode for recurring group splits (e.g. shared apartment expenses) |

#### Screen Elements

| Element | Detail |
|---------|--------|
| Total amount | Large, prominent input |
| People count | +/- stepper, minimum 2, maximum 20 |
| Person names | Optional — defaults to "Person 1, Person 2..." |
| Split mode | Equal by default, switchable to custom |
| Per person amount | Large, clear — updates in real time |
| Note | Optional e.g. "Dinner at Nobu" — max 200 characters |
| Save button | Saves total bill amount as expense entry |
| History list | Past splits for reference |

#### Split Modes

| Mode | Behavior |
|------|----------|
| Equal | Total divided evenly across all people |
| Custom | Each person's amount is manually adjustable — updates in real time |
| Saving | Always saves the original total amount regardless of split mode |

#### History Actions

| Action | Behavior |
|--------|----------|
| View | Tap a history record to see full split details |
| Delete | Swipe left to delete — confirmation dialog before removing |
| Edit | Not supported — history records are reference only |

#### Edge Cases — Shared Costs

| Scenario | Behavior |
|----------|----------|
| Total amount is $0 | Save button disabled — inline error "Total amount must be greater than $0" |
| Person's amount is $0 in custom split | Allowed — feature is a financial note tool, not a payment enforcer |
| People count at minimum (2) | Minus button disabled and greyed out — no error message needed |
| People count at maximum (20) | Plus button disabled and greyed out — no error message needed |
| Back tapped from Split Summary | Returns to input screen with all values persisted |

---

### 11. Category List

| Field | Details |
|-------|---------|
| **Purpose** | Manage expense categories used across the app — default categories are locked, custom ones are fully editable and reorderable. |
| **Key Elements** | Default categories (locked), Custom categories (editable), Icon picker, Color picker, Drag to reorder, Edit / Delete (bottom sheet), Empty state |
| **Order** | Category order here directly affects chip order in Add Expense |
| **Notes** | Default categories always appear first and are locked in position. Custom categories follow in user-defined order. |

#### Category Types

| Type | Editable | Deletable | Reorderable |
|------|----------|-----------|-------------|
| Default | ❌ Locked | ❌ Locked | ❌ Always first |
| Custom | ✅ Yes | ✅ Yes | ✅ Drag to reorder |
| Uncategorized | ❌ Locked | ❌ Locked | ❌ System only |

#### Default Categories

| # | Category |
|---|----------|
| 1 | Food |
| 2 | Transport |
| 3 | Shopping |
| 4 | Bills |
| 5 | Health |
| 6 | Entertainment |

#### Uncategorized (System Category)

| Rule | Behavior |
|------|----------|
| Auto-assigned | When a linked category is deleted, expenses move to Uncategorized |
| Selectable | ❌ Not selectable when logging an expense |
| Visible | ✅ Appears in Journal and Reports for reference |

#### Reorder Behavior

| Rule | Behavior |
|------|----------|
| Drag to reorder | Custom categories only |
| Affects Add Expense | ✅ Chip order in Add Expense mirrors category order here |
| Default position | Always at top, locked |

#### Edge Cases — Category List

| Scenario | Behavior |
|----------|----------|
| All custom categories deleted | List shows default categories only — no empty state, defaults always visible |
| Duplicate category name entered | Blocked — inline error "A category with this name already exists" |
| Category name exceeds 20 characters | Input blocked at 20 characters — character counter shown below field |

---

### 12. Reports

| Field | Details |
|-------|---------|
| **Purpose** | Simple at-a-glance view of monthly spending patterns — insight without complexity. |
| **Period** | Monthly only in MVP |
| **Key Elements** | Total spent (large, prominent), Donut chart by category, Top categories ranked list, Daily average |
| **Notes** | Read-only screen in MVP. One chart, one period, key numbers. Goal is to help users understand where their money went — not a full analytics dashboard. |

#### MVP Screen Elements

| Element | Detail |
|---------|--------|
| Period selector | Monthly only in MVP |
| Total spent | Large, prominent — most important number |
| Donut chart | Spending breakdown by category — simple and visual |
| Top categories | Ranked list with category name and amount |
| Daily average | Current month: total spent ÷ days elapsed so far. Past months: total spent ÷ total days in that month |
| Uncategorized | Only shown in chart and list if uncategorized expenses exist — hidden otherwise |

#### Edge Cases — Reports

| Scenario | Behavior |
|----------|----------|
| No data at all (new user) | Shows empty state illustration — "No data yet. Start logging to see your spending insights." |
| No expenses in current month | Auto-switches to last month with data — no empty state shown |
| All expenses are Uncategorized | Donut renders as single full-circle segment + subtle tip "Tip: categorize your expenses for better insights" |

#### Post-MVP Items

| Feature | Notes |
|---------|-------|
| Weekly / custom range | Adds flexibility for power users |
| Bar / line chart | Useful for trend analysis over time |
| Event budget summaries | Separate section once event feature matures |
| Export (CSV / PDF) | Low priority for casual users |

---

### 13. More / Settings

| Field | Details |
|-------|---------|
| **Purpose** | Combined hub for secondary features and app configuration — accessible via More in the bottom nav. |
| **Key Elements** | Debt Tracker, Shared Costs, Reports, Category List, Currency, Language, Theme, Data Export, Clear Data, App Version |
| **Notes** | No account or login required. All data is local. Single default currency in MVP — multi-currency per entry in post-MVP. |

#### More — Feature Links

| Item | Navigates To |
|------|-------------|
| Debt Tracker | Debt Tracker screen |
| Shared Costs | Shared Costs screen |
| Reports | Reports screen |
| Category List | Category List screen |

#### MVP Settings

| Setting | Detail |
|---------|--------|
| PIN Authentication | Optional 6-digit PIN — enable/disable, change PIN, recovery option |
| Biometric | Face ID / fingerprint as alternative to PIN — greyed out and disabled until PIN is enabled. Tapping while PIN disabled shows "Please enable PIN first to use biometric authentication." |
| Currency | Single default currency — applied to all entries. Selector for common currencies. |
| Monthly Budget | Optional — set a monthly spending limit. When set, Home header shows spent vs. budget for Budget Planner persona. Counter auto-resets on the 1st of each month. |
| Default Category | Set preferred auto-selected category for Add Expense — defaults to Food |
| Language | App language selector |
| Theme | Light / Dark / System default |
| Data Export | Exports separate CSV files per feature — zipped into one file (expenses.csv, events.csv, debts.csv, shared_costs.csv) |
| Clear Data | Selective clear — user chooses what to wipe, each option requires confirmation dialog |
| App Version | Display only |

#### Data Export Contents

| File | Columns |
|------|---------|
| expenses.csv | Date, Amount, Category, Note, @ Tag (Event/Debt), Created Date |
| events.csv | Event Name, Start Date, End Date, Budget, Total Spent, Remaining, Status |
| debts.csv | Type (Lent/Owe), Person Name, Amount, Date, Due Date, Note, Status (Active/Settled) |
| shared_costs.csv | Date, Total Amount, People Count, Names, Split Mode, Note |

#### Post-MVP Settings

| Setting | Notes |
|---------|-------|
| Multi-currency | Per entry currency support |
| Notifications | Due date reminders for Debt Tracker |
| Default view | Set landing screen preference |

---

### 14. PIN Setup

| Field | Details |
|-------|---------|
| **Purpose** | Let users enable and configure optional 6-digit PIN authentication for app privacy. |
| **Access** | Via More / Settings → PIN Authentication |
| **Key Elements** | Enable/disable toggle, PIN entry field (6 digits), Confirm PIN field, Biometric toggle, Recovery option setup |
| **Notes** | PIN is optional — disabled by default. Biometric requires PIN to be set first. Security question is mandatory — PIN cannot be enabled without setting one. |

#### PIN Setup Flow

| Step | Action |
|------|--------|
| 1 | User taps PIN Authentication in Settings |
| 2 | Toggle to enable — prompts to enter 6-digit PIN |
| 3 | Confirm PIN — must match |
| 4 | Confirm mismatch → inline error "PINs do not match" — confirm field clears, original PIN stays |
| 5 | Option to enable biometric (Face ID / fingerprint) |
| 6 | Set security question and answer — required, cannot proceed without it |
| 7 | PIN enabled — success message shown: "PIN is now active. You'll be asked to enter it on your next launch." |

#### PIN Change Flow

| Step | Action |
|------|--------|
| 1 | User taps Change PIN in Settings |
| 2 | Enter current PIN — verified before proceeding |
| 3 | Enter new 6-digit PIN |
| 4 | Confirm new PIN — must match |
| 5 | Confirm mismatch → inline error "PINs do not match" — confirm field clears, new PIN stays |
| 6 | PIN updated — confirmed with success message |

#### PIN Disable Flow

| Step | Action |
|------|--------|
| 1 | User toggles off PIN Authentication in Settings |
| 2 | Prompted to enter current PIN to confirm |
| 3 | Correct PIN → PIN disabled, biometric also disabled automatically |
| 4 | Incorrect PIN → "Incorrect PIN" error — toggle stays on |

#### Biometric Disable Flow

| Step | Action |
|------|--------|
| 1 | User toggles off Biometric in Settings |
| 2 | Biometric disabled immediately — no verification required |
| 3 | PIN remains active — user falls back to PIN entry on next launch |

#### Recovery Options

| Option | Behavior |
|--------|----------|
| Security question | User selects from a predefined list and provides an answer during PIN setup — used to verify identity on recovery |
| Reset app | Clear all data and disable PIN as last resort |

#### Predefined Security Questions

| # | Question |
|---|---------|
| 1 | What was your first pet's name? |
| 2 | What city were you born in? |
| 3 | What was the name of your first school? |
| 4 | What is your mother's maiden name? |
| 5 | What was your childhood nickname? |

#### Recovery Flow

| Step | Action |
|------|--------|
| 1 | User taps "Forgot PIN" on PIN Entry screen |
| 2 | Security question shown — user enters answer |
| 3 | Correct answer → prompts to set new 6-digit PIN |
| 4 | Confirm new PIN — must match |
| 5 | Confirm mismatch → inline error "PINs do not match" — confirm field clears, new PIN stays |
| 6 | PIN reset — navigates to Home |
| 7 | Wrong answer → "Incorrect answer. Try again." |
| 8 | 5 wrong answers → locked for 30 seconds — "Too many attempts. Try again in 30s" |
| 9 | After lockout → attempts reset, user can try again or reset app |

---

### 15. PIN Entry

| Field | Details |
|-------|---------|
| **Purpose** | Authenticate the user at the start of every session when PIN is enabled. |
| **Trigger** | Every app launch if PIN is enabled |
| **Key Elements** | 6-digit PIN input, Biometric prompt (if enabled), Forgot PIN link |
| **Notes** | No back navigation — user must authenticate to proceed. Biometric shown by default if enabled. |

#### Screen Elements

| Element | Detail |
|---------|--------|
| PIN display | 6 dot indicators — fills as user enters digits |
| Numeric keypad | Full screen, clean layout |
| Biometric prompt | Auto-triggered if enabled — Face ID / fingerprint |
| Forgot PIN | Opens recovery flow |

#### PIN Entry States

| State | Behavior |
|-------|----------|
| Correct PIN | Navigates to Home immediately |
| Incorrect PIN | Dots shake — "Incorrect PIN, try again" |
| 5 incorrect attempts | App locks for 30 seconds — countdown timer shown — "Too many attempts. Try again in 30s" |
| Locked state | PIN keypad disabled until countdown completes |
| After lockout | Attempts reset — user can try again |
| Biometric success | Navigates to Home immediately |
| Biometric failed | Falls back to PIN entry |
| Forgot PIN tapped | Opens recovery flow — verify then reset PIN |
