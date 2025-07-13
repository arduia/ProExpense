package com.arduia.expense.ui.about

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AboutFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `fragment class should exist and be instantiable`() {
        // When
        val fragment = AboutFragment()

        // Then
        Assert.assertNotNull(fragment)
    }

    @Test
    fun `fragment should have proper class structure`() {
        // When
        val fragmentClass = AboutFragment::class.java

        // Then
        Assert.assertNotNull(fragmentClass)
        Assert.assertTrue(fragmentClass.superclass.name.contains("Fragment"))
    }

    @Test
    fun `fragment should be a valid Android Fragment`() {
        // Given
        val fragment = AboutFragment()

        // When & Then - Should not crash on instantiation
        Assert.assertNotNull(fragment)
        Assert.assertNotNull(fragment.javaClass)
    }
} 