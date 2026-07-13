package com.arduia.expense.domain

/**
 * Split/Debt filter selection for Journal — separate from [CategoryId] because Split and Debt
 * records aren't real user categories (they're bookkeeped under a sentinel category id, see
 * [RecordKind]). Selecting one of these instead of a category is mutually exclusive with
 * category-id filtering.
 */
enum class RecordKindFilter { SPLIT, DEBT }
