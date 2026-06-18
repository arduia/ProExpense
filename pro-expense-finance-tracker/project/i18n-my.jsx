// Burmese (Myanmar) localization layer.
// Strategy: patch React.createElement so any UI *string* (element children, plus
// a whitelist of text-bearing props) that exactly matches a dictionary entry is
// swapped to Burmese. This localizes every screen without forking component files.
// Identifiers, style values, ids, css vars, etc. are never touched — only literal
// text the components render. Latin digits, currency ($) and brand name are kept.

(function () {
  const MM = {
    // ── Bottom nav ──
    "Home": "ပင်မ",
    "Budget": "ဘတ်ဂျက်",
    "Journal": "ဂျာနယ်",
    "More": "နောက်ထပ်",

    // ── Generic actions ──
    "Add": "ထည့်ရန်",
    "Save": "သိမ်းမည်",
    "Cancel": "ပယ်ဖျက်",
    "Edit": "ပြင်ရန်",
    "Delete": "ဖျက်ရန်",
    "Clear": "ရှင်းရန်",
    "Clear tag": "တက်ဂ်ဖျက်ရန်",
    "Apply": "အသုံးပြုမည်",
    "Next": "ရှေ့သို့",
    "Back": "နောက်သို့",
    "Skip": "ကျော်မည်",
    "Change": "ပြောင်းရန်",
    "See all": "အားလုံးကြည့်ရန်",
    "Customize": "စိတ်ကြိုက်ပြင်ရန်",

    // ── Home ──
    "Wed · May 25": "ဗုဒ္ဓဟူး · မေ ၂၅",
    "Hi,": "မင်္ဂလာပါ၊",
    "Welcome,": "ကြိုဆိုပါသည်၊",
    "Maya": "မေယာ",
    "Spent · May": "သုံးစွဲမှု · မေ",
    "from last month": "ပြီးခဲ့သည့်လထက်",
    "This month": "ဤလအတွင်း",
    "used": "သုံးပြီး",
    "left": "ကျန်",
    "Active event": "လက်ရှိပွဲ",
    "Quick access": "အမြန်သုံး",
    "Recent": "မကြာသေးမီက",
    "Reports": "အစီရင်ခံစာ",
    "Debt": "ကြွေးမြီ",
    "Split": "ခွဲဝေ",
    "Events": "ပွဲများ",
    "No spending logged yet this month": "ဤလအတွက် မှတ်တမ်းမရှိသေးပါ",
    "No expenses yet": "အသုံးစရိတ် မရှိသေးပါ",
    "Start by logging your first one — it takes about five seconds.": "ပထမဆုံး အသုံးစရိတ်ကို မှတ်တမ်းတင်ပါ — ၅ စက္ကန့်ခန့်သာ ကြာပါသည်။",
    "Log your first expense": "ပထမအသုံးစရိတ် မှတ်တမ်းတင်ရန်",
    "or tap": "သို့မဟုတ် နှိပ်ပါ",
    "below": "အောက်တွင်",

    // ── Categories ──
    "Food": "အစားအသောက်",
    "Transport": "သယ်ယူပို့ဆောင်",
    "Shopping": "ဈေးဝယ်",
    "Bills": "ဘီလ်များ",
    "Health": "ကျန်းမာရေး",
    "Entertainment": "ဖျော်ဖြေရေး",
    "Coffee runs": "ကော်ဖီ",
    "Pet care": "အိမ်မွေးတိရစ္ဆာန်",

    // ── Sample notes ──
    "Lunch with M.": "M နှင့် နေ့လယ်စာ",
    "Oat latte": "အုတ်လက်တေး",
    "Movie · Dune": "ရုပ်ရှင် · Dune",
    "Groceries": "ကုန်စုံ",
    "Dinner at Nobu": "Nobu ၌ ညစာ",
    "Hotel night": "ဟိုတယ်တစ်ည",
    "Lunch · beach": "နေ့လယ်စာ · ကမ်းခြေ",
    "Scooter rental": "စကူတာငှား",
    "Souvenirs": "အမှတ်တရပစ္စည်း",
    "Dinner · seafood": "ညစာ · ပင်လယ်စာ",

    // ── Add expense ──
    "New expense": "အသုံးစရိတ်အသစ်",
    "Amount · USD": "ပမာဏ · USD",
    "Amount": "ပမာဏ",
    "USD · home currency": "USD · ပင်မငွေကြေး",
    "Amount must be greater than $0": "ပမာဏသည် $0 ထက် များရပါမည်",
    "Default": "မူရင်း",
    "Custom": "စိတ်ကြိုက်",
    "+ More": "+ နောက်ထပ်",
    "+ Add": "+ ထည့်ရန်",
    "+ Create new category": "+ အမျိုးအစားအသစ် ဖန်တီးရန်",
    "Choose category": "အမျိုးအစား ရွေးပါ",
    "Details": "အသေးစိတ်",
    "Category": "အမျိုးအစား",
    "Category *": "အမျိုးအစား *",
    "Date & time": "ရက်စွဲနှင့် အချိန်",
    "Date": "ရက်စွဲ",
    "Time": "အချိန်",
    "Note": "မှတ်ချက်",
    "(optional)": "(ရွေးချယ်နိုင်)",
    "What was this for?": "ဘာအတွက်လဲ?",
    "Add a note (optional)": "မှတ်ချက် ထည့်ရန် (ရွေးချယ်နိုင်)",
    "Link to event or debt": "ပွဲ သို့ ကြွေးမြီနှင့် ချိတ်ဆက်ရန်",
    "Link to…": "ချိတ်ဆက်ရန်…",
    "future": "အနာဂတ်",
    "Future date — entries are ordered by created date, not expense date.": "အနာဂတ်ရက်စွဲ — မှတ်တမ်းများကို ဖန်တီးသည့်ရက်အလိုက် စီသည်။",
    "Pick one event or one debt. Tagged expenses appear in both Journal and the linked record.": "ပွဲတစ်ခု သို့ ကြွေးမြီတစ်ခု ရွေးပါ။ တက်ဂ်တပ်ထားသော အသုံးစရိတ်များသည် ဂျာနယ်နှင့် ချိတ်ဆက်မှတ်တမ်းတွင် ပေါ်မည်။",

    // ── Onboarding ──
    "Welcome": "ကြိုဆိုပါသည်",
    "Your personal finance notebook.": "သင့်ကိုယ်ပိုင် ငွေကြေးမှတ်စု။",
    "Quick Log": "အမြန်မှတ်တမ်း",
    "Log expenses in seconds — amount, category, done.": "စက္ကန့်ပိုင်းအတွင်း မှတ်တမ်းတင်ပါ — ပမာဏ၊ အမျိုးအစား၊ ပြီးပါပြီ။",
    "Shared Costs": "မျှဝေကုန်ကျစရိတ်",
    "Split bills instantly — total, people, done.": "ဘီလ်များကို ချက်ချင်းခွဲဝေပါ — စုစုပေါင်း၊ လူဦးရေ၊ ပြီးပါပြီ။",
    "Event Budget": "ပွဲဘတ်ဂျက်",
    "Plan and track any event budget — trips, weddings, parties.": "ခရီးစဉ်၊ မင်္ဂလာပွဲ၊ ပါတီ — မည်သည့်ပွဲဘတ်ဂျက်ကိုမဆို စီစဉ်ခြေရာခံပါ။",
    "Review your spending like a diary, day by day.": "နေ့စဉ်မှတ်တမ်းကဲ့သို့ သင့်အသုံးစရိတ်ကို ပြန်လည်သုံးသပ်ပါ။",
    "What's inside": "အထဲမှာ ဘာတွေပါလဲ",
    "Everything you need to track money — simply. Jump right in, or scroll to see what's inside.": "ငွေကြေး ခြေရာခံရန် လိုအပ်သမျှ — ရိုးရှင်းစွာ။ ချက်ချင်းစတင်ပါ၊ သို့မဟုတ် အထဲမှာ ဘာပါလဲ ဆင်းကြည့်ပါ။",
    "I'll explore on my own": "ကိုယ်တိုင် လေ့လာမည်",
    "Get started": "စတင်မည်",

    // ── Splash ──
    "Your finance notebook": "သင့်ငွေကြေးမှတ်စု",

    // ── PIN ──
    "PIN authentication": "PIN အတည်ပြုခြင်း",
    "Settings": "ဆက်တင်များ",
    "Enable": "ဖွင့်ရန်",
    "Use Face ID": "Face ID သုံးရန်",
    "Requires PIN to be enabled": "PIN ဖွင့်ထားရန် လိုအပ်သည်",
    "New PIN · 6 digits": "PIN အသစ် · ၆ လုံး",
    "Confirm PIN": "PIN အတည်ပြုပါ",
    "Enter PIN": "PIN ထည့်ပါ",
    "6 digits to unlock": "ဖွင့်ရန် ဂဏန်း ၆ လုံး",
    "Recovery · required": "ပြန်လည်ရယူရန် · မဖြစ်မနေ",
    "Security question": "လုံခြုံရေးမေးခွန်း",
    "Set PIN": "PIN သတ်မှတ်ရန်",
    "Save PIN": "PIN သိမ်းရန်",
    "Forgot PIN?": "PIN မေ့သွားပါသလား?",
    "Too many attempts. Try again in 30s": "ကြိုးစားမှု များလွန်းသည်။ ၃၀ စက္ကန့်အကြာ ပြန်ကြိုးစားပါ",
    "Keypad disabled until countdown ends": "ရေတွက်ပြီးသည်အထိ ကီးပက် ပိတ်ထားသည်",
    "Incorrect PIN, try again": "PIN မှားနေသည်၊ ပြန်ကြိုးစားပါ",

    // ── Journal ──
    "Search notes, amount…": "မှတ်ချက်၊ ပမာဏ ရှာရန်…",
    "All": "အားလုံး",
    "Today": "ယနေ့",
    "Yesterday": "မနေ့က",
    "Linked to": "ချိတ်ဆက်ထား",

    // ── Reports ──
    "Total spent": "စုစုပေါင်း သုံးစွဲမှု",
    "Daily avg": "နေ့စဉ်ပျမ်းမျှ",
    "Daily average": "နေ့စဉ်ပျမ်းမျှ",
    "Top categories": "ထိပ်တန်း အမျိုးအစားများ",
    "by category": "အမျိုးအစားအလိုက်",
    "This Month": "ဤလ",

    // ── More / Settings ──
    "Features": "လုပ်ဆောင်ချက်များ",
    "Debt Tracker": "ကြွေးမြီ ခြေရာခံ",
    "Category List": "အမျိုးအစား စာရင်း",
    "Currency": "ငွေကြေး",
    "Monthly budget": "လစဉ်ဘတ်ဂျက်",
    "Language": "ဘာသာစကား",
    "Theme": "အပြင်အဆင်",
    "Export data": "ဒေတာ ထုတ်ယူ",
    "Clear data": "ဒေတာ ရှင်းရန်",
    "Data": "ဒေတာ",
    "Off": "ပိတ်",
    "On": "ဖွင့်",
    "System": "စနစ်",
    "English": "မြန်မာ",

    // ── Category list ──
    "Categories": "အမျိုးအစားများ",
    "Default · always first": "မူရင်း · အမြဲရှေ့ဆုံး",
    "Custom · drag to reorder": "စိတ်ကြိုက် · ဆွဲ၍ ပြန်စီရန်",
    "locked": "လော့ခ်ချ",

    // ── Event budget ──
    "New event": "ပွဲအသစ်",
    "Create event": "ပွဲ ဖန်တီးရန်",
    "Over budget": "ဘတ်ဂျက်ကျော်",
    "over": "ကျော်",
    "Remaining": "ကျန်ရှိ",
    "Spent": "သုံးစွဲ",
    "spent": "သုံးပြီး",
    "Linked expenses": "ချိတ်ဆက် အသုံးစရိတ်",
    "Add tagged": "တက်ဂ် ထည့်ရန်",
    "active": "လက်ရှိ",

    // ── Debt ──
    "I Lent": "ငါ ချေးသည်",
    "You lent": "သင် ချေးထားသည်",
    "You owe": "သင် ပေးရန်ရှိသည်",
    "Date recorded": "မှတ်တမ်းတင်သည့်ရက်",
    "Due date": "ပေးရမည့်ရက်",
    "No due date": "ပေးရမည့်ရက် မရှိ",
    "Status": "အခြေအနေ",
    "Linked expense": "ချိတ်ဆက် အသုံးစရိတ်",
    "Reference only": "ကိုးကားရန်သာ",
    "Mark as settled": "ကျေပြီးအဖြစ် မှတ်ရန်",
    "I Owe": "ငါ့ အကြွေး",
    "Active": "လက်ရှိ",
    "Settled": "ကျေပြီး",
    "settled": "ကျေပြီး",
    "Lent out": "ချေးထားသည်",

    // ── Shared costs ──
    "Split a bill": "ဘီလ် ခွဲဝေရန်",
    "Total bill": "စုစုပေါင်း ဘီလ်",
    "People": "လူဦးရေ",
    "people": "လူ",
    "Equal": "ညီတူ",
    "Per person": "တစ်ဦးချင်း",
    "Save split": "ခွဲဝေမှု သိမ်းရန်",

    // ── Canvas section titles / subtitles ──
    "Flow 04 — First Launch": "အဆင့် ၀၄ — ပထမဆုံးဖွင့်ခြင်း",
    "Flow 04 — Profile Setup": "အဆင့် ၀၄ — ပရိုဖိုင် သတ်မှတ်ခြင်း",
    "First run · between Onboarding and Home · account-free personalization": "ပထမအကြိမ် · မိတ်ဆက်နှင့် ပင်မကြား · အကောင့်မလို စိတ်ကြိုက်ပြင်ဆင်ခြင်း",
    "Set up your profile": "သင့်ပရိုဖိုင် သတ်မှတ်ပါ",
    "Your name personalizes the app — greeting you on the home screen and identifying your records and exports. No account needed; everything stays on your device.": "သင့်အမည်သည် အက်ပ်ကို ကိုယ်ပိုင်ဆန်စေသည် — ပင်မမျက်နှာပြင်တွင် နှုတ်ဆက်ပြီး သင့်မှတ်တမ်းနှင့် ထုတ်ယူမှုများကို ခွဲခြားပေးသည်။ အကောင့်မလို၊ အရာအားလုံး သင့်စက်ပေါ်တွင်သာ ရှိသည်။",
    "Profile name": "ပရိုဖိုင် အမည်",
    "Used on your home screen and CSV exports.": "သင့်ပင်မမျက်နှာပြင်နှင့် CSV ထုတ်ယူမှုများတွင် သုံးသည်။",
    "Pick your home currency": "သင့် ပင်မငွေကြေး ရွေးပါ",
    "All entries default to this. You can still log in any currency per-expense.": "မှတ်တမ်းအားလုံး ၎င်းကို မူရင်းအဖြစ်သုံးသည်။ အသုံးစရိတ်တစ်ခုချင်းကို မည်သည့်ငွေကြေးဖြင့်မဆို မှတ်တမ်းတင်နိုင်သည်။",
    "Set a default category": "မူရင်း အမျိုးအစား သတ်မှတ်ပါ",
    "This is pre-selected each time you add an expense — change it anytime.": "အသုံးစရိတ်ထည့်တိုင်း ၎င်းကို ကြိုတင်ရွေးထားသည် — အချိန်မရွေး ပြောင်းနိုင်သည်။",
    "Lock the app with a PIN?": "PIN ဖြင့် အက်ပ်ကို လော့ခ်ချမလား?",
    "Add a 6-digit PIN to keep your finances private. Optional — you can enable it later in More.": "သင့်ငွေကြေးကို လုံခြုံစေရန် ၆ လုံး PIN ထည့်ပါ။ ရွေးချယ်နိုင် — နောက်ထပ်တွင် ဖွင့်နိုင်သည်။",
    "Set up PIN": "PIN သတ်မှတ်ရန်",
    "Not now — go to Home": "ယခုမဟုတ် — ပင်မသို့",
    "All currencies": "ငွေကြေးအားလုံး",
    "Start tracking": "စတင် ခြေရာခံရန်",
    "Search currency…": "ငွေကြေး ရှာရန်…",
    "Continue": "ဆက်လုပ်ရန်",
    "Skip": "ကျော်ရန်",
    "Splash → onboarding (Get started on every slide) → Home": "Splash → မိတ်ဆက် (ချပ်တိုင်းတွင် စတင်မည်) → ပင်မ",
    "Splash → 5 onboarding slides → Home": "Splash → မိတ်ဆက် ၅ ချပ် → ပင်မ",
    "Flow 05 — PIN Auth": "အဆင့် ၀၅ — PIN အတည်ပြုခြင်း",
    "Setup → Entry → Lockout (validation state)": "သတ်မှတ် → ထည့်သွင်း → ပိတ်ဆို့ (စစ်ဆေးမှု)",
    "Flow 01 — Quick Log": "အဆင့် ၀၁ — အမြန်မှတ်တမ်း",
    "Home → Add Expense → Save · with $0 validation state": "ပင်မ → အသုံးစရိတ်ထည့် → သိမ်း · $0 စစ်ဆေးမှုဖြင့်",
    "Flow 02 — Browse Journal": "အဆင့် ၀၂ — ဂျာနယ် ကြည့်ရှုခြင်း",
    "Diary view → entry detail": "မှတ်တမ်းမြင်ကွင်း → အသေးစိတ်",
    "Flow 03 — More": "အဆင့် ၀၃ — နောက်ထပ်",
    "Settings hub → Reports → Category List": "ဆက်တင် → အစီရင်ခံစာ → အမျိုးအစားစာရင်း",
    "Flow 06 — Event Budget": "အဆင့် ၀၆ — ပွဲဘတ်ဂျက်",
    "List of events → detail · includes over-budget state": "ပွဲစာရင်း → အသေးစိတ် · ဘတ်ဂျက်ကျော်အခြေအနေ ပါဝင်",
    "Empty → Create event → list → detail · includes over-budget state": "အလွတ် → ပွဲဖန်တီး → စာရင်း → အသေးစိတ် · ဘတ်ဂျက်ကျော် ပါဝင်",
    "No active events": "လက်ရှိ ပွဲ မရှိပါ",
    "Set a budget for a trip, wedding or party — then tag expenses to track it in real time.": "ခရီး၊ မင်္ဂလာပွဲ သို့မဟုတ် ပါတီအတွက် ဘတ်ဂျက် သတ်မှတ်ပါ — ပြီးနောက် အသုံးစရိတ်များ တွဲ၍ အချိန်နှင့်တပြေးညီ ခြေရာခံပါ။",
    "Event name": "ပွဲအမည်",
    "Dates": "ရက်စွဲများ",
    "Total budget": "စုစုပေါင်း ဘတ်ဂျက်",
    "Flow 07 — Debt & Lending": "အဆင့် ၀၇ — ကြွေးမြီနှင့် ချေးငွေ",
    "I Lent / I Owe toggle · active + settled": "ငါချေး / ငါ့အကြွေး · လက်ရှိ + ကျေပြီး",
    "Flow 08 — Shared Costs": "အဆင့် ၀၈ — မျှဝေကုန်ကျစရိတ်",
    "Quick bill splitter": "အမြန် ဘီလ်ခွဲစက်",
    "Edge Cases": "အနားသတ် ကိစ္စများ",
    "Validation, errors, empty & boundary states across every flow": "Flow အားလုံးအတွက် စစ်ဆေးမှု၊ အမှား၊ အလွတ်နှင့် နယ်နိမိတ် အခြေအနေများ",

    // ── Edge-case strings ──
    "Unfinished expense": "မပြီးသေးသော အသုံးစရိတ်",
    "Discard": "ဖျက်ရန်",
    "Continue": "ဆက်လုပ်ရန်",
    "Note can't exceed 200 characters": "မှတ်ချက်သည် စာလုံး ၂၀၀ ထက် မကျော်ရ",
    "Link to…": "ချိတ်ဆက်ရန်…",
    "No matches": "ကိုက်ညီမှု မတွေ့ပါ",
    "Quick note": "အမြန် မှတ်ချက်",
    "Save note": "မှတ်ချက် သိမ်းရန်",
    "A category with this name already exists": "ဤအမည်ဖြင့် အမျိုးအစား ရှိပြီးသား",
    "Add category": "အမျိုးအစား ထည့်ရန်",
    "Confirm your PIN": "သင့် PIN ကို အတည်ပြုပါ",
    "PINs do not match. Try again.": "PIN များ မကိုက်ညီပါ။ ထပ်ကြိုးစားပါ။",
    "Enter your PIN": "သင့် PIN ထည့်ပါ",
    "Incorrect PIN, try again": "PIN မှားနေသည်၊ ထပ်ကြိုးစားပါ",
    "Recover PIN": "PIN ပြန်လည်ရယူရန်",
    "Answer your security question": "သင့် လုံခြုံရေးမေးခွန်း ဖြေပါ",
    "Verify & reset PIN": "အတည်ပြု၍ PIN ပြန်သတ်မှတ်ရန်",
    "New event": "ပွဲအသစ်",
    "An event with this name already exists": "ဤအမည်ဖြင့် ပွဲ ရှိပြီးသား",
    "Budget must be greater than $0": "ဘတ်ဂျက်သည် $0 ထက် ကြီးရမည်",
    "Create event": "ပွဲ ဖန်တီးရန်",
    "Over budget": "ဘတ်ဂျက် ကျော်",
    "Closed": "ပိတ်ပြီး",
    "Delete this record?": "ဤမှတ်တမ်းကို ဖျက်မလား?",
    "Cancel": "မလုပ်တော့ပါ",
    "Delete": "ဖျက်ရန်",
    "Settled": "ကျေပြီး",
    "Total amount must be greater than $0": "စုစုပေါင်း ပမာဏသည် $0 ထက် ကြီးရမည်",
    "Total bill": "စုစုပေါင်း ဘီလ်",
    "Maximum of 20 people reached": "အများဆုံး ၂၀ ဦး ပြည့်ပြီ",
    "Custom split": "စိတ်ကြိုက် ခွဲဝေ",
    "Switch to equal": "ညီတူသို့ ပြောင်းရန်",
    "Read-only — the 24-hour edit window has passed. No new expenses can be linked.": "ဖတ်ရုံသာ — ၂၄ နာရီ ပြင်ဆင်ချိန် ကုန်ဆုံးပြီ။ အသုံးစရိတ်အသစ် ချိတ်၍မရပါ။",
  };

  // Props whose string values are user-facing and safe to localize.
  const TEXT_PROPS = ["title", "subtitle", "label", "placeholder"];

  function tr(s) {
    if (typeof s !== "string") return s;
    const key = s.trim();
    if (key === "") return s;
    const hit = MM[key];
    if (hit == null) return s;
    // preserve any surrounding whitespace the component relied on
    return s.replace(key, hit);
  }

  const _create = React.createElement;
  React.createElement = function (type, props, ...children) {
    // translate whitelisted text props
    if (props) {
      let cloned = null;
      for (const p of TEXT_PROPS) {
        if (typeof props[p] === "string") {
          const t = tr(props[p]);
          if (t !== props[p]) {
            if (!cloned) cloned = Object.assign({}, props);
            cloned[p] = t;
          }
        }
      }
      if (cloned) props = cloned;
    }
    // translate direct string children
    const mapped = children.map((c) => (typeof c === "string" ? tr(c) : c));
    return _create.call(React, type, props, ...mapped);
  };
})();
