package com.arduia.expense.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arduia.expense.ui.design.ProExpensePaperBackground
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w414dp-h868dp")
class FontWeightSpecimenScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun WeightRow(label: String, family: FontFamily, weight: FontWeight) {
        Text(
            text = "$label — The quick brown fox 1240",
            style = TextStyle(fontFamily = family, fontWeight = weight, fontSize = 18.sp),
        )
    }

    @Test
    fun fontWeights_specimen() {
        composeTestRule.setContent {
            ProExpenseTheme {
                ProExpensePaperBackground {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("Manrope (sans)", style = ProExpenseTheme.typography.eyebrow)
                        WeightRow("300 Light", ProExpenseSans, FontWeight.Light)
                        WeightRow("400 Regular", ProExpenseSans, FontWeight.Normal)
                        WeightRow("500 Medium", ProExpenseSans, FontWeight.Medium)
                        WeightRow("600 SemiBold", ProExpenseSans, FontWeight.SemiBold)
                        WeightRow("700 Bold", ProExpenseSans, FontWeight.Bold)
                        WeightRow("800 ExtraBold", ProExpenseSans, FontWeight.ExtraBold)
                        Text("Geist Mono", style = ProExpenseTheme.typography.eyebrow)
                        WeightRow("400 Regular", ProExpenseMono, FontWeight.Normal)
                        WeightRow("500 Medium", ProExpenseMono, FontWeight.Medium)
                        WeightRow("600 SemiBold", ProExpenseMono, FontWeight.SemiBold)
                        Text("Instrument Serif", style = ProExpenseTheme.typography.eyebrow)
                        WeightRow("400 Regular", ProExpenseSerif, FontWeight.Normal)
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
