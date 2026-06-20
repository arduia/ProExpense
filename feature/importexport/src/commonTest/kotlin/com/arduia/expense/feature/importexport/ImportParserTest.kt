package com.arduia.expense.feature.importexport

import com.arduia.expense.domain.RecordType
import kotlin.test.Test
import kotlin.test.assertEquals

class ImportParserTest {
    @Test
    fun parseCsv_readsValidRows() {
        val csv = """
            id,amountCents,categoryId,note,recordedAt
            rec1,1250,food,Lunch,1700000000000
        """.trimIndent()
        val records = ImportParser.parseCsv(csv)
        assertEquals(1, records.size)
        assertEquals("rec1", records[0].id)
        assertEquals(1250L, records[0].homeCurrencyAmount.valueInCents)
        assertEquals("food", records[0].categoryId)
        assertEquals(RecordType.EXPENSE, records[0].type)
    }

    @Test
    fun parseJson_readsValidObjects() {
        val json = """[{"id":"rec2","cents":500,"category":"transport","recordedAt":1700000000000}]"""
        val records = ImportParser.parseJson(json)
        assertEquals(1, records.size)
        assertEquals("rec2", records[0].id)
        assertEquals(500L, records[0].homeCurrencyAmount.valueInCents)
        assertEquals("transport", records[0].categoryId)
    }
}
