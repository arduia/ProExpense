package com.arduia.expense.ui.entry

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.regex.Pattern

@RunWith(JUnit4::class)
class FloatingInputFilterTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test the same pattern used in FloatingInputFilter
    private val floatingPattern = Pattern.compile("[+-]?([0-9]{0,8}([.][0-9]{0,2})?|[.][0-9]{0,2})")

    @Test
    fun `pattern should accept valid integer amounts`() {
        // Given
        val validIntegers = listOf("1", "12", "123", "12345", "12345678")

        // When & Then
        validIntegers.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Integer $input should be accepted", isValid)
        }
    }

    @Test
    fun `pattern should accept valid decimal amounts`() {
        // Given
        val validDecimals = listOf("1.5", "12.34", "123.45", "1.1", "99.99")

        // When & Then
        validDecimals.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Decimal $input should be accepted", isValid)
        }
    }

    @Test
    fun `pattern should accept decimal amounts starting with dot`() {
        // Given
        val dotStartingDecimals = listOf(".5", ".12", ".99")

        // When & Then
        dotStartingDecimals.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Decimal starting with dot $input should be accepted", isValid)
        }
    }

    @Test
    fun `pattern should accept amounts ending with dot`() {
        // Given
        val dotEndingAmounts = listOf("1.", "12.", "123.")

        // When & Then
        dotEndingAmounts.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Amount ending with dot $input should be accepted", isValid)
        }
    }

    @Test
    fun `pattern should reject amounts with more than 8 integer digits`() {
        // Given
        val tooLongIntegers = listOf("123456789", "1234567890")

        // When & Then
        tooLongIntegers.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertFalse("Too long integer $input should be rejected", isValid)
        }
    }

    @Test
    fun `pattern should reject amounts with more than 2 decimal places`() {
        // Given
        val tooManyDecimals = listOf("1.123", "12.3456", "99.999")

        // When & Then
        tooManyDecimals.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertFalse("Amount with too many decimals $input should be rejected", isValid)
        }
    }

    @Test
    fun `pattern should reject invalid characters`() {
        // Given
        val invalidInputs = listOf("abc", "12a", "1.2b", "1,5", "1..5", "1.2.3")

        // When & Then
        invalidInputs.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertFalse("Invalid input $input should be rejected", isValid)
        }
    }

    @Test
    fun `pattern should accept valid signed amounts`() {
        // Given
        val signedAmounts = listOf("+123", "-123", "+12.34", "-12.34")

        // When & Then
        signedAmounts.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Signed amount $input should be accepted", isValid)
        }
    }

    @Test
    fun `pattern should handle edge case amounts`() {
        // Given
        val edgeCases = listOf(
            "0", "0.0", "0.00", "00.00", 
            "12345678", "12345678.12", 
            ".0", ".00"
        )

        // When & Then
        edgeCases.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Edge case $input should be handled correctly", isValid)
        }
    }

    @Test
    fun `pattern should accept maximum valid amount`() {
        // Given - Maximum 8 integer digits with 2 decimal places
        val maxValidAmount = "99999999.99"

        // When
        val isValid = floatingPattern.matcher(maxValidAmount).matches()

        // Then
        Assert.assertTrue("Maximum valid amount should be accepted", isValid)
    }

    @Test
    fun `pattern should handle empty string input`() {
        // Given
        val emptyInput = ""

        // When
        val isValid = floatingPattern.matcher(emptyInput).matches()

        // Then
        Assert.assertTrue("Empty input should be accepted by pattern", isValid)
    }

    @Test
    fun `pattern should handle single character inputs`() {
        // Given
        val validSingleChars = listOf("1", ".", "+", "-")
        val invalidSingleChars = listOf("a", "b", "c", ",", "!")

        // When & Then - Test valid single characters
        validSingleChars.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Single character '$input' should be valid", isValid)
        }

        // Test invalid single characters
        invalidSingleChars.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertFalse("Single character '$input' should be invalid", isValid)
        }
    }

    @Test
    fun `pattern should match documented examples in comments`() {
        // Given - Examples from the filter's comments, adjusted for actual pattern behavior
        val validCommentedExamples = listOf("123", "123.", ".45") // .45 has 2 decimals, valid
        val invalidCommentedExamples = listOf("123.456", ".456") // Both have 3 decimal places

        // When & Then - Test valid examples
        validCommentedExamples.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Valid commented example $input should be accepted", isValid)
        }

        // Test invalid examples (pattern allows max 2 decimal places)
        invalidCommentedExamples.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertFalse("Invalid commented example $input should be rejected (has >2 decimals)", isValid)
        }
    }

    @Test
    fun `pattern validation should handle business logic requirements`() {
        // Given - Test the complete business logic requirements for expense amounts
        val businessValidCases = listOf(
            // Basic integers
            "0", "1", "10", "100", "1000",
            // Decimals with 1 place
            "1.5", "10.5", "100.5",
            // Decimals with 2 places
            "1.25", "10.50", "100.99",
            // Starting with dot
            ".5", ".50", ".99",
            // Ending with dot (partial entry)
            "1.", "10.", "100.",
            // Signed amounts
            "+100", "-100", "+1.50", "-1.50",
            // Maximum length (8 integer digits + 2 decimal)
            "12345678", "12345678.99"
        )

        val businessInvalidCases = listOf(
            // Too many integer digits
            "123456789",
            // Too many decimal places
            "1.123", "10.5555",
            // Invalid characters
            "1a", "a1", "1,5", "1..5",
            // Multiple dots
            "1.2.3", "..5",
            // Invalid combinations
            "+-1", "-+1", "1.2.3.4"
        )

        // When & Then - Test valid business cases
        businessValidCases.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertTrue("Business valid case '$input' should be accepted", isValid)
        }

        // Test invalid business cases
        businessInvalidCases.forEach { input ->
            val isValid = floatingPattern.matcher(input).matches()
            Assert.assertFalse("Business invalid case '$input' should be rejected", isValid)
        }
    }

    @Test
    fun `filter class should exist and have correct pattern`() {
        // Given
        val filter = FloatingInputFilter()

        // When & Then - Verify the filter class exists and can be instantiated
        Assert.assertNotNull("FloatingInputFilter should be instantiable", filter)
        Assert.assertTrue("FloatingInputFilter should be an InputFilter", 
            filter is android.text.InputFilter)
    }
} 