package com.arduia.expense.domain

import org.junit.Test
import org.junit.Assert.*

class AmountTest {

    @Test
    fun `creation with positive value creates valid amount`() {
        // Given
        val value = 100.50

        // When
        val amount = Amount(value)

        // Then
        assertEquals(value, amount.value, 0.01)
    }

    @Test
    fun `creation with zero value creates valid amount`() {
        // Given
        val value = 0.0

        // When
        val amount = Amount(value)

        // Then
        assertEquals(value, amount.value, 0.01)
        assertEquals(Amount.ZERO, amount)
    }

    @Test
    fun `creation with negative value creates valid amount`() {
        // Given
        val value = -50.25

        // When
        val amount = Amount(value)

        // Then
        assertEquals(value, amount.value, 0.01)
    }

    @Test
    fun `Amount ZERO constant has zero value`() {
        // When/Then
        assertEquals(0.0, Amount.ZERO.value, 0.01)
    }

    @Test
    fun `equals returns true for same values`() {
        // Given
        val amount1 = Amount(100.0)
        val amount2 = Amount(100.0)

        // When/Then
        assertEquals(amount1, amount2)
        assertTrue(amount1 == amount2)
    }

    @Test
    fun `equals returns false for different values`() {
        // Given
        val amount1 = Amount(100.0)
        val amount2 = Amount(200.0)

        // When/Then
        assertNotEquals(amount1, amount2)
        assertFalse(amount1 == amount2)
    }

    @Test
    fun `hashCode is consistent for equal amounts`() {
        // Given
        val amount1 = Amount(75.50)
        val amount2 = Amount(75.50)

        // When/Then
        assertEquals(amount1.hashCode(), amount2.hashCode())
    }

    @Test
    fun `toString returns readable format`() {
        // Given
        val amount = Amount(123.45)

        // When
        val result = amount.toString()

        // Then
        assertTrue(result.contains("123.45"))
    }

    @Test
    fun `addition works correctly`() {
        // Given
        val amount1 = Amount(50.25)
        val amount2 = Amount(25.75)

        // When
        val result = amount1.plus(amount2)

        // Then
        assertEquals(76.0, result.value, 0.01)
    }

    @Test
    fun `subtraction works correctly`() {
        // Given
        val amount1 = Amount(100.0)
        val amount2 = Amount(30.0)

        // When
        val result = amount1.minus(amount2)

        // Then
        assertEquals(70.0, result.value, 0.01)
    }

    @Test
    fun `multiplication works correctly`() {
        // Given
        val amount = Amount(25.0)
        val multiplier = 3.0

        // When
        val result = amount.times(multiplier)

        // Then
        assertEquals(75.0, result.value, 0.01)
    }

    @Test
    fun `division works correctly`() {
        // Given
        val amount = Amount(100.0)
        val divisor = 4.0

        // When
        val result = amount.div(divisor)

        // Then
        assertEquals(25.0, result.value, 0.01)
    }

    @Test
    fun `addition with zero returns same amount`() {
        // Given
        val amount = Amount(50.0)

        // When
        val result = amount.plus(Amount.ZERO)

        // Then
        assertEquals(amount, result)
    }

    @Test
    fun `subtraction of zero returns same amount`() {
        // Given
        val amount = Amount(75.0)

        // When
        val result = amount.minus(Amount.ZERO)

        // Then
        assertEquals(amount, result)
    }

    @Test
    fun `multiplication by one returns same amount`() {
        // Given
        val amount = Amount(42.0)

        // When
        val result = amount.times(1.0)

        // Then
        assertEquals(amount, result)
    }

    @Test
    fun `multiplication by zero returns zero amount`() {
        // Given
        val amount = Amount(99.99)

        // When
        val result = amount.times(0.0)

        // Then
        assertEquals(Amount.ZERO, result)
    }

    @Test
    fun `division by one returns same amount`() {
        // Given
        val amount = Amount(87.5)

        // When
        val result = amount.div(1.0)

        // Then
        assertEquals(amount, result)
    }

    @Test(expected = ArithmeticException::class)
    fun `division by zero throws exception`() {
        // Given
        val amount = Amount(100.0)

        // When
        amount.div(0.0)

        // Then - exception should be thrown
    }

    @Test
    fun `comparison operators work correctly`() {
        // Given
        val smaller = Amount(25.0)
        val larger = Amount(50.0)
        val equal = Amount(25.0)

        // When/Then
        assertTrue(smaller < larger)
        assertTrue(larger > smaller)
        assertTrue(smaller <= equal)
        assertTrue(larger >= smaller)
        assertTrue(smaller == equal)
    }

    @Test
    fun `isPositive returns true for positive amounts`() {
        // Given
        val amount = Amount(10.0)

        // When/Then
        assertTrue(amount.isPositive())
    }

    @Test
    fun `isPositive returns false for zero amount`() {
        // Given
        val amount = Amount.ZERO

        // When/Then
        assertFalse(amount.isPositive())
    }

    @Test
    fun `isPositive returns false for negative amounts`() {
        // Given
        val amount = Amount(-10.0)

        // When/Then
        assertFalse(amount.isPositive())
    }

    @Test
    fun `isZero returns true for zero amount`() {
        // Given
        val amount = Amount.ZERO

        // When/Then
        assertTrue(amount.isZero())
    }

    @Test
    fun `isZero returns false for non-zero amounts`() {
        // Given
        val positiveAmount = Amount(1.0)
        val negativeAmount = Amount(-1.0)

        // When/Then
        assertFalse(positiveAmount.isZero())
        assertFalse(negativeAmount.isZero())
    }

    @Test
    fun `isNegative returns true for negative amounts`() {
        // Given
        val amount = Amount(-15.0)

        // When/Then
        assertTrue(amount.isNegative())
    }

    @Test
    fun `isNegative returns false for positive amounts`() {
        // Given
        val amount = Amount(15.0)

        // When/Then
        assertFalse(amount.isNegative())
    }

    @Test
    fun `isNegative returns false for zero amount`() {
        // Given
        val amount = Amount.ZERO

        // When/Then
        assertFalse(amount.isNegative())
    }

    @Test
    fun `abs returns positive amount for negative values`() {
        // Given
        val amount = Amount(-75.0)

        // When
        val result = amount.abs()

        // Then
        assertEquals(Amount(75.0), result)
    }

    @Test
    fun `abs returns same amount for positive values`() {
        // Given
        val amount = Amount(75.0)

        // When
        val result = amount.abs()

        // Then
        assertEquals(amount, result)
    }

    @Test
    fun `abs returns zero for zero amount`() {
        // Given
        val amount = Amount.ZERO

        // When
        val result = amount.abs()

        // Then
        assertEquals(Amount.ZERO, result)
    }

    @Test
    fun `very large values are handled correctly`() {
        // Given
        val largeValue = 999999999.99

        // When
        val amount = Amount(largeValue)

        // Then
        assertEquals(largeValue, amount.value, 0.01)
    }

    @Test
    fun `very small decimal values are handled correctly`() {
        // Given
        val smallValue = 0.01

        // When
        val amount = Amount(smallValue)

        // Then
        assertEquals(smallValue, amount.value, 0.001)
    }

    @Test
    fun `precision is maintained in calculations`() {
        // Given
        val amount1 = Amount(10.01)
        val amount2 = Amount(20.02)

        // When
        val result = amount1.plus(amount2)

        // Then
        assertEquals(30.03, result.value, 0.001)
    }

    @Test
    fun `chain operations work correctly`() {
        // Given
        val amount = Amount(100.0)

        // When
        val result = amount
            .plus(Amount(50.0))
            .minus(Amount(25.0))
            .times(2.0)
            .div(5.0)

        // Then
        assertEquals(50.0, result.value, 0.01)
    }

    @Test
    fun `formatted string includes currency symbol when available`() {
        // Given
        val amount = Amount(123.45)

        // When
        val formatted = amount.toFormattedString("$")

        // Then
        assertTrue(formatted.contains("$"))
        assertTrue(formatted.contains("123.45"))
    }

    @Test
    fun `copy returns new instance with same value`() {
        // Given
        val original = Amount(55.55)

        // When
        val copy = original.copy()

        // Then
        assertEquals(original, copy)
        assertNotSame(original, copy)
    }
}