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

enum class ProIconGlyph(@DrawableRes val resId: Int) {
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
    CatDefault(R.drawable.ic_cat_default),
}

fun categoryIcon(categoryId: String): ProIconGlyph = when (categoryId) {
    "food" -> ProIconGlyph.CatFood
    "transport" -> ProIconGlyph.CatTransport
    "shopping" -> ProIconGlyph.CatShopping
    "bills" -> ProIconGlyph.CatBills
    "health" -> ProIconGlyph.CatHealth
    "entertainment" -> ProIconGlyph.CatEntertainment
    "coffee" -> ProIconGlyph.CatCoffee
    "pet" -> ProIconGlyph.CatPet
    else -> ProIconGlyph.CatDefault
}

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
