import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/03-home.md`. Every label rendered here is built by the shared
/// `HomeViewModel` — this view formats nothing itself, so Home reads identically on both platforms.
struct HomeView: View {
    @StateObject private var home = KotlinViewModel<HomeUiState, HomeViewModel>.home()

    private var state: HomeUiState { home.state }

    var body: some View {
        NavigationStack {
            content
        }
    }

    private var content: some View {
        ScrollView {
            VStack(spacing: ProSpacing.lg) {
                header
                spendCard
                    .padding(.horizontal, ProSpacing.screenHorizontal)
                    // Floats up into the gradient header, matching the canvas.
                    .offset(y: -44)
                    .padding(.bottom, -44)

                if state.isLoading {
                    ProgressView().padding(.top, ProSpacing.xxl)
                } else if state.isEmpty {
                    emptyState
                } else {
                    recentSection
                }
            }
            .padding(.bottom, 120)
        }
        .background(ProColor.paper)
        .ignoresSafeArea(edges: .top)
    }

    private var header: some View {
        VStack(alignment: .leading, spacing: ProSpacing.xs) {
            Text(state.isEmpty ? "WELCOME" : "HI")
                .font(ProFont.eyebrow)
                .kerning(1.2)
                .foregroundStyle(.white.opacity(0.75))
            Text(state.greetingName.isEmpty ? "there" : state.greetingName)
                .font(.system(size: 30))
                .kerning(-0.45)
                .foregroundStyle(.white)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, ProSpacing.screenHorizontal)
        .padding(.top, 72)
        .padding(.bottom, 64)
        .background(LinearGradient.proHeader)
    }

    private var spendCard: some View {
        VStack(alignment: .leading, spacing: ProSpacing.md) {
            Text("SPENT THIS MONTH")
                .font(ProFont.eyebrow)
                .kerning(1.1)
                .foregroundStyle(ProColor.ink3)

            Text(state.monthSpend)
                .font(ProFont.heroAmount)
                .kerning(-0.8)
                .foregroundStyle(ProColor.ink)
                .minimumScaleFactor(0.6)
                .lineLimit(1)

            if let budget = state.budgetSummary {
                budgetBar(budget)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(ProSpacing.xl)
        .proCard()
    }

    private func budgetBar(_ budget: HomeBudgetSummary) -> some View {
        VStack(alignment: .leading, spacing: ProSpacing.sm) {
            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    Capsule().fill(ProColor.primaryTint)
                    Capsule()
                        .fill(budget.isOverBudget ? ProColor.danger : ProColor.primary)
                        .frame(width: geo.size.width * CGFloat(budget.progress))
                }
            }
            .frame(height: 8)

            Text("\(budget.spentLabel) of \(budget.budgetLabel)")
                .font(ProFont.caption)
                .foregroundStyle(budget.isOverBudget ? ProColor.danger : ProColor.ink3)
        }
    }

    private var recentSection: some View {
        VStack(alignment: .leading, spacing: ProSpacing.md) {
            Text("RECENT")
                .font(ProFont.eyebrow)
                .kerning(1.1)
                .foregroundStyle(ProColor.ink3)
                .padding(.horizontal, ProSpacing.screenHorizontal)

            ForEach(state.dayGroups, id: \.dayTitle) { group in
                DayGroupCard(group: group) { row in
                    JournalDetailView(recordId: row.id)
                }
                .padding(.horizontal, ProSpacing.screenHorizontal)
            }
        }
    }

    private var emptyState: some View {
        VStack(spacing: ProSpacing.sm) {
            Image(systemName: "tray")
                .font(.system(size: 40))
                .foregroundStyle(ProColor.muted)
            Text("No records yet")
                .font(ProFont.title)
                .foregroundStyle(ProColor.ink)
            Text("Tap + to log your first expense.")
                .font(ProFont.caption)
                .foregroundStyle(ProColor.ink3)
        }
        .padding(.top, ProSpacing.xxl * 2)
    }
}

/// A day's header plus its rows. Rows become tappable only when a `destination` is supplied, so
/// the same card renders inside a `NavigationStack` (Home, Journal) and outside one (Event Detail).
struct DayGroupCard<Destination: View>: View {
    let group: HomeDayGroup
    private let destination: ((ProTransactionRowModel) -> Destination)?

    init(group: HomeDayGroup, @ViewBuilder destination: @escaping (ProTransactionRowModel) -> Destination) {
        self.group = group
        self.destination = destination
    }

    fileprivate init(group: HomeDayGroup, destination: ((ProTransactionRowModel) -> Destination)?) {
        self.group = group
        self.destination = destination
    }

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Text(group.dayTitle)
                    .font(ProFont.label)
                    .foregroundStyle(ProColor.ink2)
                Spacer()
                Text(group.dayTotal)
                    .font(ProFont.label)
                    .foregroundStyle(ProColor.ink3)
            }
            .padding(.horizontal, ProSpacing.lg)
            .padding(.vertical, ProSpacing.md)

            ForEach(group.rows, id: \.id) { row in
                if let destination {
                    NavigationLink { destination(row) } label: { TransactionRowView(row: row) }
                        .buttonStyle(.plain)
                } else {
                    TransactionRowView(row: row)
                }
                if row.id != group.rows.last?.id {
                    Divider().background(ProColor.lineSoft).padding(.leading, 60)
                }
            }
        }
        .proCard()
    }
}

extension DayGroupCard where Destination == EmptyView {
    init(group: HomeDayGroup) {
        self.init(group: group, destination: nil)
    }
}

struct TransactionRowView: View {
    let row: ProTransactionRowModel

    var body: some View {
        HStack(spacing: ProSpacing.md) {
            Circle()
                .fill(ProColor.primaryTint)
                .frame(width: 36, height: 36)
                .overlay(
                    Image(systemName: row.isIncome ? "arrow.down.left" : "arrow.up.right")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(row.isIncome ? ProColor.success : ProColor.primary)
                )

            VStack(alignment: .leading, spacing: 2) {
                Text(row.note)
                    .font(ProFont.body)
                    .foregroundStyle(ProColor.ink)
                    .lineLimit(1)
                Text(row.meta)
                    .font(ProFont.caption)
                    .foregroundStyle(ProColor.ink3)
            }

            Spacer()

            Text(row.amount)
                .font(ProFont.body.weight(.semibold))
                .foregroundStyle(row.isIncome ? ProColor.success : ProColor.ink)
        }
        .padding(.horizontal, ProSpacing.lg)
        .padding(.vertical, ProSpacing.md)
    }
}
