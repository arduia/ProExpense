package com.arduia.expense.storage.mapping

import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.toRecordType
import com.arduia.expense.storage.db.Category as CategoryRow

internal fun CategoryRow.toDomain(): Category =
    Category(
        id = CategoryId(id),
        name = name,
        isCustom = is_custom != 0L,
        sortOrder = sort_order.toInt(),
        iconId = icon_id,
        colorId = color_id,
        type = type.toRecordType(),
    )
