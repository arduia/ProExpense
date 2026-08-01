import SwiftUI
import ProExpenseKit

/// 03 · Home — greeting, month-spend card, optional budget/active-event summary, recent list.
/// Every string and figure below is computed by the shared `HomeViewModel` in `:shell`; this view
/// only lays them out. Spec: `design-system-spec/screens/03-home.md`.
struct HomeView: View {
    @ObservedObject var store: ShellStore
    @State private var showAddExpense = false

    private var home: HomeUiState { store.home }

    var body: some View {
        ZStack(alignment: .bottom) {
            Pro.Color.paper.ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: Pro.Space.x16) {
                    greeting
                    MonthSpendCard(home: home)
                    if let event = home.activeEvent {
                        ActiveEventCard(event: event)
                    }
                    if home.isEmpty {
                        emptyState
                    } else {
                        recentSection
                    }
                }
                .padding(.horizontal, Pro.Space.screenPadding)
                .padding(.top, Pro.Space.x16)
                .padding(.bottom, 96) // clears the bottom bar
            }

            bottomBar
        }
        .sheet(isPresented: $showAddExpense) {
            AddExpenseView(
                currencyCode: store.state.homeCurrencyCode,
                currencySymbol: store.state.homeCurrencySymbol,
                defaultCategoryId: store.state.defaultCategoryId
            )
        }
    }

    // MARK: Header

    private var greeting: some View {
        VStack(alignment: .leading, spacing: Pro.Space.x6) {
            Text(home.dateLabel)
                .font(Pro.Font.eyebrow)
                .kerning(1.1)
                .foregroundStyle(Pro.Color.ink3)

            // The emphasis word is italic in primary (spec §Type).
            (
                Text(home.greetingPrefixRes == "welcome" ? "Welcome" : "Hi, ")
                    .foregroundStyle(Pro.Color.ink)
                    + Text(home.greetingName)
                    .foregroundStyle(Pro.Color.clay)
                    .italic()
            )
            .font(Pro.Font.heroGreeting)
        }
    }

    // MARK: Recent list

    private var recentSection: some View {
        VStack(alignment: .leading, spacing: Pro.Space.x12) {
            HStack {
                Text("Recent")
                    .font(Pro.Font.sectionHead)
                    .foregroundStyle(Pro.Color.ink)
                Spacer()
                Text("See all")
                    .font(Pro.Font.emphasis)
                    .foregroundStyle(Pro.Color.clay)
            }

            ForEach(home.dayGroups, id: \.dayTitle) { group in
                VStack(alignment: .leading, spacing: 0) {
                    HStack {
                        Text(group.dayTitle)
                            .font(Pro.Font.sectionHead)
                            .foregroundStyle(Pro.Color.ink)
                        Spacer()
                        Text(group.dayTotal)
                            .font(Pro.Font.timestamp)
                            .foregroundStyle(Pro.Color.ink3)
                    }
                    .padding(.bottom, Pro.Space.x8)

                    ForEach(Array(group.transactions.enumerated()), id: \.element.id) { index, item in
                        TransactionRow(item: item)
                        if index < group.transactions.count - 1 {
                            Divider().background(Pro.Color.line2)
                        }
                    }
                }
                .proCard()
            }
        }
    }

    private var emptyState: some View {
        VStack(spacing: Pro.Space.x12) {
            Image(systemName: "tray")
                .font(.system(size: 44, weight: .light))
                .foregroundStyle(Pro.Color.muted2)
            Text("No expenses yet")
                .font(Pro.Font.sectionHead)
                .foregroundStyle(Pro.Color.ink)
            Text("Log your first expense to see it here.")
                .font(Pro.Font.body)
                .foregroundStyle(Pro.Color.ink3)
                .multilineTextAlignment(.center)
            Button { showAddExpense = true } label: {
                Text("Log your first expense")
                    .font(Pro.Font.emphasis)
                    .foregroundStyle(Pro.Color.onFilled)
                    .padding(.horizontal, Pro.Space.x26)
                    .padding(.vertical, Pro.Space.x12)
                    .background(Pro.Color.clay)
                    .clipShape(Capsule())
            }
            .padding(.top, Pro.Space.x6)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, Pro.Space.x26)
    }

    // MARK: Bottom bar

    private var bottomBar: some View {
        HStack {
            Spacer()
            Button { showAddExpense = true } label: {
                Image(systemName: "plus")
                    .font(.system(size: 26, weight: .medium))
                    .foregroundStyle(Pro.Color.onFilled)
                    .frame(width: 64, height: 64)
                    .background(Pro.Color.clay)
                    .clipShape(Circle())
                    .shadow(color: Pro.Shadow.heroColor, radius: 20, y: 8)
            }
            .accessibilityLabel("Add expense")
            Spacer()
        }
        .padding(.bottom, Pro.Space.x12)
    }
}

/// Persona-driven summary header: total spent this month, or spend-vs-budget when a budget is set.
private struct MonthSpendCard: View {
    let home: HomeUiState

    var body: some View {
        VStack(alignment: .leading, spacing: Pro.Space.x8) {
            Text(home.monthLabel)
                .font(Pro.Font.eyebrow)
                .kerning(1.1)
                .foregroundStyle(Pro.Color.ink3)

            Text(home.monthSpend)
                .font(Pro.Font.cardAmount)
                .kerning(-0.02 * 40)
                .foregroundStyle(Pro.Color.ink)

            if let summary = home.budgetSummary {
                Text("\(summary.statusLabel) · \(summary.spentLabel) \(summary.budgetLabel)")
                    .font(Pro.Font.body)
                    .foregroundStyle(summary.isOverBudget ? Pro.Color.danger : Pro.Color.ink3)

                ProgressBar(progress: summary.progress, isOver: summary.isOverBudget)
            } else if let delta = home.monthDelta {
                Text(delta)
                    .font(Pro.Font.body)
                    .foregroundStyle(Pro.Color.ink3)
            }
        }
        .proCard()
    }
}

private struct ActiveEventCard: View {
    let event: HomeActiveEventState

    var body: some View {
        VStack(alignment: .leading, spacing: Pro.Space.x8) {
            HStack {
                Text(event.title)
                    .font(Pro.Font.sectionHead)
                    .foregroundStyle(Pro.Color.ink)
                Spacer()
                Text(event.dateRange)
                    .font(Pro.Font.timestamp)
                    .foregroundStyle(Pro.Color.ink3)
            }
            Text("\(event.spentLabel) \(event.budgetLabel)")
                .font(Pro.Font.body)
                .foregroundStyle(event.isOverBudget ? Pro.Color.danger : Pro.Color.ink3)
            ProgressBar(progress: event.progress, isOver: event.isOverBudget)
        }
        .proCard()
    }
}

private struct ProgressBar: View {
    let progress: Float
    let isOver: Bool

    var body: some View {
        GeometryReader { geo in
            ZStack(alignment: .leading) {
                Capsule().fill(Pro.Color.line2)
                Capsule()
                    .fill(isOver ? Pro.Color.danger : Pro.Color.clay)
                    .frame(width: geo.size.width * CGFloat(min(max(progress, 0), 1)))
            }
        }
        .frame(height: 6)
    }
}

private struct TransactionRow: View {
    let item: HomeTransactionItem

    /// Mirrors `TransactionRow`'s rule in `shared/androidMain/.../Lists.kt`: income is green, and
    /// Home Recents is the one feed where an **Owe** row is also green. Lent and Split deliberately
    /// keep the default expense color here — Lent/Owe get their own treatment in Debt Tracker.
    private var amountColor: Color {
        if item.isIncome { return Pro.Color.sage }
        switch item.rowKind {
        case .debtOwed: return Pro.Color.sage
        default: return Pro.Color.ink
        }
    }

    /// Badge tint/glyph follow the row kind, not the category — a Split/Debt row carries a category
    /// for bucketing but never renders its badge from it (see `TransactionRowModel.ProRowKind`).
    private var badgeTint: Color {
        switch item.rowKind {
        case .split: return Pro.Color.tagTint
        case .debtLent: return Pro.Color.sage.opacity(0.25)
        case .debtOwed: return Pro.Color.danger.opacity(0.25)
        default: return Pro.Color.clayTint
        }
    }

    private var badgeGlyph: String {
        switch item.rowKind {
        case .split: return "person.2"
        case .debtLent, .debtOwed: return "arrow.left.arrow.right"
        case .income: return "arrow.down"
        default: return "creditcard"
        }
    }

    var body: some View {
        HStack(spacing: Pro.Space.x12) {
            Circle()
                .fill(badgeTint)
                .frame(width: 36, height: 36)
                .overlay(
                    Image(systemName: badgeGlyph)
                        .font(.system(size: 15, weight: .regular))
                        .foregroundStyle(Pro.Color.ink)
                )

            VStack(alignment: .leading, spacing: 2) {
                Text(item.note)
                    .font(Pro.Font.body)
                    .foregroundStyle(Pro.Color.ink)
                    .lineLimit(1)
                HStack(spacing: Pro.Space.x6) {
                    Text(item.meta)
                        .font(Pro.Font.timestamp)
                        .foregroundStyle(Pro.Color.ink3)
                    if let tag = item.tag {
                        Text("@\(tag)")
                            .font(Pro.Font.caption)
                            .foregroundStyle(Pro.Color.tag)
                            .padding(.horizontal, Pro.Space.x6)
                            .padding(.vertical, 1)
                            .background(Pro.Color.tagTint)
                            .clipShape(Capsule())
                    }
                }
            }

            Spacer()

            Text(item.amount)
                .font(Pro.Font.rowAmount)
                .foregroundStyle(amountColor)
        }
        .padding(.vertical, Pro.Space.x12)
    }
}
