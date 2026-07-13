package com.arduia.expense.ui.design

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.arduia.expense.shared.R
import com.arduia.expense.ui.theme.ProExpenseTheme

enum class ProIconGlyph(
    @DrawableRes val resId: Int,
) {
    Home(R.drawable.ic_home),
    Budget(R.drawable.ic_budget),
    Journal(R.drawable.ic_journal),
    More(R.drawable.ic_more),
    Plus(R.drawable.ic_plus),
    Minus(R.drawable.ic_minus),
    Back(R.drawable.ic_back),
    Backspace(R.drawable.ic_backspace),
    Close(R.drawable.ic_close),
    ChevronDown(R.drawable.ic_chevron_down),
    ChevronRight(R.drawable.ic_chevron_right),
    Search(R.drawable.ic_search),
    Bell(R.drawable.ic_bell),
    Check(R.drawable.ic_check),
    Sparkle(R.drawable.ic_sparkle),
    At(R.drawable.ic_at),
    Calendar(R.drawable.ic_calendar),
    Clock(R.drawable.ic_clock),
    Note(R.drawable.ic_note),
    Edit(R.drawable.ic_edit),
    Lock(R.drawable.ic_lock),
    User(R.drawable.ic_user),
    Eye(R.drawable.ic_eye),
    EyeOff(R.drawable.ic_eye_off),
    Fingerprint(R.drawable.ic_fingerprint),
    FeatReports(R.drawable.ic_feat_reports),
    FeatDebt(R.drawable.ic_feat_debt),
    FeatSplit(R.drawable.ic_feat_split),
    FeatEvents(R.drawable.ic_feat_events),
    CatFood(R.drawable.ic_cat_food),
    CatTransport(R.drawable.ic_cat_transport),
    CatShopping(R.drawable.ic_cat_shopping),
    CatBills(R.drawable.ic_cat_bills),
    CatHealth(R.drawable.ic_cat_health),
    CatEntertainment(R.drawable.ic_cat_entertainment),
    CatCoffee(R.drawable.ic_cat_coffee),
    CatPet(R.drawable.ic_cat_pet),
    CatHome(R.drawable.ic_cat_home),
    CatEducation(R.drawable.ic_cat_education),
    CatTravel(R.drawable.ic_cat_travel),
    CatFitness(R.drawable.ic_cat_fitness),
    CatSubscription(R.drawable.ic_cat_subscription),
    CatGift(R.drawable.ic_cat_gift),
    CatInsurance(R.drawable.ic_cat_insurance),
    CatUtilities(R.drawable.ic_cat_utilities),
    CatFamily(R.drawable.ic_cat_family),
    CatSalary(R.drawable.ic_cat_salary),
    CatSavings(R.drawable.ic_cat_savings),
    CatBeauty(R.drawable.ic_cat_beauty),
    CatDefault(R.drawable.ic_cat_default),
}

private val categoryIconByCatalogueId: Map<String, ProIconGlyph> =
    mapOf(
        "food" to ProIconGlyph.CatFood,
        "transport" to ProIconGlyph.CatTransport,
        "shopping" to ProIconGlyph.CatShopping,
        "bills" to ProIconGlyph.CatBills,
        "health" to ProIconGlyph.CatHealth,
        "entertainment" to ProIconGlyph.CatEntertainment,
        "coffee" to ProIconGlyph.CatCoffee,
        "pet" to ProIconGlyph.CatPet,
        "home" to ProIconGlyph.CatHome,
        "education" to ProIconGlyph.CatEducation,
        "travel" to ProIconGlyph.CatTravel,
        "fitness" to ProIconGlyph.CatFitness,
        "subscription" to ProIconGlyph.CatSubscription,
        "gift" to ProIconGlyph.CatGift,
        "insurance" to ProIconGlyph.CatInsurance,
        "utilities" to ProIconGlyph.CatUtilities,
        "family" to ProIconGlyph.CatFamily,
        "salary" to ProIconGlyph.CatSalary,
        "savings" to ProIconGlyph.CatSavings,
        "beauty" to ProIconGlyph.CatBeauty,
    )

fun categoryIcon(categoryId: String): ProIconGlyph =
    categoryIconByCatalogueId[categoryId] ?: ProIconGlyph.CatDefault

@Composable
fun ProIcon(
    glyph: ProIconGlyph,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    size: Dp = ProExpenseTheme.dimensions.iconNav,
) {
    Icon(
        painter = painterResource(glyph.resId),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint,
    )
}
