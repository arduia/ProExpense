package com.arduia.expense.storage.mapping

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JsonStringCodecTest {
    @Test
    fun escapeThenUnescape_roundTripsSpecialCharacters() {
        val original = "quote\" backslash\\ newline\n carriage\r"

        val escaped = escapeJsonString(original)
        val unescaped = unescapeJsonString(escaped)

        assertEquals(original, unescaped)
    }

    @Test
    fun extractJsonString_stopsAtUnescapedQuote_notAtAnEscapedOne() {
        val json = """{"name":"Alice \"the Great\""}"""

        assertEquals("""Alice "the Great"""", extractJsonString(json, "name"))
    }

    @Test
    fun extractJsonString_missingKey_returnsNull() {
        assertNull(extractJsonString("""{"name":"Alice"}""", "missing"))
    }

    @Test
    fun extractJsonNumber_readsTopLevelInteger() {
        assertEquals(1_700_000_000_000L, extractJsonNumber("""{"recordedAtEpochMillis":1700000000000}""", "recordedAtEpochMillis"))
    }

    @Test
    fun extractNestedString_andNumber_readFieldsInsideANestedObject() {
        val json = """{"money":{"cents":1250,"code":"USD"}}"""

        assertEquals(1250L, extractNestedNumber(json, "money", "cents"))
        assertEquals("USD", extractNestedString(json, "money", "code"))
    }
}
