/**
 * Domain rules (design plan Screen 14 recovery):
 * - Five predefined security questions (no custom questions).
 * - Questions identified by stable id for storage.
 */
package com.arduia.expense.feature.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SecurityQuestionsTest {
    @Test
    fun all_containsFivePredefinedQuestions() {
        assertEquals(5, SecurityQuestions.all.size)
    }

    @Test
    fun byId_returnsQuestionForKnownId() {
        assertNotNull(SecurityQuestions.byId("pet"))
    }

    @Test
    fun byId_returnsNullForUnknownId() {
        assertNull(SecurityQuestions.byId("unknown"))
    }

    @Test
    fun all_idsAreUnique() {
        assertEquals(5, SecurityQuestions.all.map { it.id }.distinct().size)
    }
}
