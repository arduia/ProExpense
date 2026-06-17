package com.arduia.expense.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class ProExpenseTypographyTest {

  private val typography = ProExpenseTypography()

  @Test
  fun displayRoles_useInstrumentSerif() {
    assertEquals(ProExpenseSerif, typography.displayAmount.fontFamily)
    assertEquals(ProExpenseSerif, typography.screenTitle.fontFamily)
    assertEquals(ProExpenseSerif, typography.sectionHead.fontFamily)
    assertEquals(ProExpenseSerif, typography.rowAmount.fontFamily)
  }

  @Test
  fun bodyRoles_useManropeSans() {
    assertEquals(ProExpenseSans, typography.body.fontFamily)
    assertEquals(ProExpenseSans, typography.bodyEmphasis.fontFamily)
    assertEquals(ProExpenseSans, typography.caption.fontFamily)
    assertEquals(ProExpenseSans, typography.tagline.fontFamily)
    assertEquals(ProExpenseSans, typography.buttonMedium.fontFamily)
  }

  @Test
  fun labelRoles_useGeistMono() {
    assertEquals(ProExpenseMono, typography.eyebrow.fontFamily)
    assertEquals(ProExpenseMono, typography.monoCaption.fontFamily)
  }
}
