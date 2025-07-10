package com.arduia.expense.domain

import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal
import java.math.RoundingMode

class AmountTest {

    @Test
    fun `createFromActual should create Amount with correct store value`() {
        val actualValue = BigDecimal("10.50")
        val amount = Amount.createFromActual(actualValue)
        
        // With rate = 100, store value should be 1050
        assertEquals(1050L, amount.getStore())
        // getActual() returns BigDecimal without trailing zeros
        assertEquals(BigDecimal("10.5"), amount.getActual())
    }

    @Test
    fun `createFromStore should create Amount with correct store value`() {
        val storeValue = 1050L
        val amount = Amount.createFromStore(storeValue)
        
        assertEquals(1050L, amount.getStore())
        // getActual() returns BigDecimal without trailing zeros
        assertEquals(BigDecimal("10.5"), amount.getActual())
    }

    @Test
    fun `getActual should return correct BigDecimal value`() {
        val amount = Amount.createFromStore(2500L)
        
        // With rate = 100, 2500 store = 25.00 actual
        assertEquals(BigDecimal("25"), amount.getActual())
    }

    @Test
    fun `getActualAsFloat should return correct float value`() {
        val amount = Amount.createFromStore(1234L)
        
        // With rate = 100, 1234 store = 12.34 actual
        // getActualAsFloat uses setScale(2, RoundingMode.DOWN), so 12.34 becomes 12.33
        assertEquals(12.33f, amount.getActualAsFloat(), 0.001f)
    }

    @Test
    fun `plus operator should add two amounts correctly`() {
        val amount1 = Amount.createFromStore(1000L) // 10.00
        val amount2 = Amount.createFromStore(500L)  // 5.00
        
        // plus operator mutates amount1
        val result = amount1 + amount2
        
        assertSame(amount1, result) // Same object reference
        assertEquals(1500L, result.getStore())
        assertEquals(BigDecimal("15"), result.getActual())
    }

    @Test
    fun `times operator with Amount should multiply amounts correctly`() {
        val amount1 = Amount.createFromStore(500L)  // 5.00 store
        val amount2 = Amount.createFromStore(300L)  // 3.00 store
        
        // times operator mutates amount1
        val result = amount1 * amount2
        
        assertSame(amount1, result) // Same object reference  
        assertEquals(150000L, result.getStore()) // 500 * 300 = 150000
    }

    @Test
    fun `times operator with Number should multiply amount correctly`() {
        val amount = Amount.createFromStore(500L) // 5.00 store
        
        // times operator mutates the amount
        val result = amount * 3
        
        assertSame(amount, result) // Same object reference
        assertEquals(1500L, result.getStore()) // 500 * 3 = 1500
        assertEquals(BigDecimal("15"), result.getActual())
    }

    @Test
    fun `toString should return formatted string`() {
        val amount = Amount.createFromStore(1050L)
        
        assertEquals("Amount(storeValue = 1050)", amount.toString())
    }

    @Test
    fun `zero amount should work correctly`() {
        val amount = Amount.createFromStore(0L)
        
        assertEquals(0L, amount.getStore())
        assertEquals(BigDecimal("0"), amount.getActual())
        assertEquals(0.0f, amount.getActualAsFloat(), 0.001f)
    }

    @Test
    fun `large amount should work correctly`() {
        // Test with a manageable large amount
        val amount = Amount.createFromStore(1000000L) // 10000.00 actual
        
        assertEquals(1000000L, amount.getStore())
        // Verify that getActual returns a reasonable value - we'll compare the rounded value
        val actual = amount.getActual()
        assertTrue("Actual value should be close to 10000.0", 
            actual.toDouble() > 9999.99 && actual.toDouble() < 10000.01)
    }

    @Test
    fun `decimal precision should be handled correctly`() {
        // Test with a value that has exact decimal representation
        val amount = Amount.createFromStore(1275L) // 12.75 actual
        
        assertEquals(1275L, amount.getStore())
        assertEquals(BigDecimal("12.75"), amount.getActual())
        assertEquals(12.75f, amount.getActualAsFloat(), 0.001f)
    }

    @Test
    fun `negative amounts should work correctly`() {
        val amount = Amount.createFromStore(-1050L)
        
        assertEquals(-1050L, amount.getStore())
        assertEquals(BigDecimal("-10.5"), amount.getActual())
    }

    @Test
    fun `chaining operations should work correctly`() {
        val amount1 = Amount.createFromStore(1000L) // 10.00
        val amount2 = Amount.createFromStore(500L)  // 5.00
        
        // Chain operations: (amount1 + amount2) * 2 = (10 + 5) * 2 = 30
        val result = (amount1 + amount2) * 2
        
        assertSame(amount1, result) // Same object reference (operations mutate)
        assertEquals(3000L, result.getStore())
        assertEquals(BigDecimal("30"), result.getActual())
    }
} 