package com.arduia.expense.shared

data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
)

/**
 * Single source of truth for supported currencies (code/name/symbol) — shared by the currency
 * settings picker, onboarding's currency step, and Quick Log's per-record currency picker so all
 * three offer the same list and none hardcodes a display symbol.
 */
object CurrencyCatalog {
    val ALL: List<CurrencyInfo> =
        listOf(
            CurrencyInfo("USD", "US Dollar", "$"),
            CurrencyInfo("EUR", "Euro", "€"),
            CurrencyInfo("GBP", "British Pound", "£"),
            CurrencyInfo("JPY", "Japanese Yen", "¥"),
            CurrencyInfo("CNY", "Chinese Yuan", "¥"),
            CurrencyInfo("INR", "Indian Rupee", "₹"),
            CurrencyInfo("AED", "UAE Dirham", "د.إ"),
            CurrencyInfo("AUD", "Australian Dollar", "A$"),
            CurrencyInfo("CAD", "Canadian Dollar", "C$"),
            CurrencyInfo("CHF", "Swiss Franc", "CHF"),
            CurrencyInfo("SGD", "Singapore Dollar", "S$"),
            CurrencyInfo("HKD", "Hong Kong Dollar", "HK$"),
            CurrencyInfo("NZD", "New Zealand Dollar", "NZ$"),
            CurrencyInfo("KRW", "South Korean Won", "₩"),
            CurrencyInfo("IDR", "Indonesian Rupiah", "Rp"),
            CurrencyInfo("MYR", "Malaysian Ringgit", "RM"),
            CurrencyInfo("THB", "Thai Baht", "฿"),
            CurrencyInfo("PHP", "Philippine Peso", "₱"),
            CurrencyInfo("VND", "Vietnamese Dong", "₫"),
            CurrencyInfo("MXN", "Mexican Peso", "MX$"),
            CurrencyInfo("BRL", "Brazilian Real", "R$"),
            CurrencyInfo("ZAR", "South African Rand", "R"),
            CurrencyInfo("NGN", "Nigerian Naira", "₦"),
            CurrencyInfo("KES", "Kenyan Shilling", "KSh"),
            CurrencyInfo("EGP", "Egyptian Pound", "E£"),
            CurrencyInfo("SAR", "Saudi Riyal", "﷼"),
            CurrencyInfo("QAR", "Qatari Riyal", "QR"),
            CurrencyInfo("TRY", "Turkish Lira", "₺"),
            CurrencyInfo("RUB", "Russian Ruble", "₽"),
            CurrencyInfo("SEK", "Swedish Krona", "kr"),
            CurrencyInfo("NOK", "Norwegian Krone", "kr"),
            CurrencyInfo("DKK", "Danish Krone", "kr"),
            CurrencyInfo("PLN", "Polish Zloty", "zł"),
            CurrencyInfo("PKR", "Pakistani Rupee", "₨"),
            CurrencyInfo("BDT", "Bangladeshi Taka", "৳"),
            CurrencyInfo("TWD", "New Taiwan Dollar", "NT$"),
            CurrencyInfo("MMK", "Myanmar Kyat", "K"),
        )

    private val byCode = ALL.associateBy { it.code }

    fun infoFor(code: String): CurrencyInfo? = byCode[code]

    /** Falls back to the bare code if it isn't in the catalog (e.g. legacy/unsupported data). */
    fun symbolFor(code: String): String = byCode[code]?.symbol ?: code
}
