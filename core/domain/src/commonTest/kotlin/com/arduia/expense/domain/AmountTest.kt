import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import com.arduia.expense.domain.Amount

class AmountTest {

    @Test
    fun acceptsValidAmount() {
        val amount = Amount(1_500)
        assertEquals(1_500, amount.valueInCents)
    }

    @Test
    fun rejectsNegativeAmount() {
        assertFailsWith<IllegalArgumentException> {
            Amount(-1)
        }
    }

    @Test
    fun rejectsAmountAboveCap() {
        assertFailsWith<IllegalArgumentException> {
            Amount(Amount.MAX_VALUE_IN_CENTS + 1)
        }
    }
}
