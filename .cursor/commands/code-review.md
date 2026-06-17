# Code Review — Pro Expense

Perform a structured review of the current changes (staged, unstaged, or branch diff vs `main`).

## 1. Overview
What the change does and a quality verdict (approve / approve with notes / request changes).

## 2. Product Perspective
- UX states covered (loading, empty, error, success)
- Happy-path assumptions
- Error messaging clarity
- Product constraint compliance (privacy, no cloud backup, amount cap, single currency)

## 3. Architecture & Conventions
- Layer violations (UI calling DAO directly, etc.)
- Hilt module completeness for new bindings
- Repository/Mapper pattern adherence
- Module boundary respect (`app` vs library modules)
- v2 migration consistency (Fragment vs Compose)

## 4. Security & Privacy
- Input validation (amounts, dates, user text)
- File/URI handling in backup flows
- Network calls necessity
- No hardcoded secrets
- `allowBackup="false"` respected

## 5. Performance
- Main-thread blocking in ViewModels/Fragments
- Room query efficiency
- Paging usage correctness
- Bitmap/large object lifecycle in UI

## 6. Kotlin & Coroutines Quality
- Race conditions in Flow/LiveData collection
- `viewModelScope` vs `lifecycleScope` usage
- Exception handling in repositories
- `Result<T>` usage consistency

## 7. Test Quality
- Backbone tests present for touched classes
- Tests trace to documented rules
- Fake vs mock appropriateness
- G3 edge-case admission for boundary tests

## 8. Issues & Risks
Table: Severity | File | Finding (Critical / Major / Minor / Speculative)

## 9. Recommended Follow-ups
Ordered by priority. Only include actionable items.

Keep the review concise. Quote specific file:line references where possible.
