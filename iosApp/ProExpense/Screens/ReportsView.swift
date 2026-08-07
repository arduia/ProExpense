import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/12-reports.md`. Month totals plus the top-5 category
/// breakdown with an "Other" rollup — all computed by the shared `GenerateReportPeriodUseCase`.
struct ReportsView: View {
    @StateObject private var reports = Shell.reports()

    private var state: ReportsUiState { reports.state }

    var body: some View {
        ScrollView {
            VStack(spacing: ProSpacing.lg) {
                periodSwitcher

                if state.isLoading {
                    ProgressView().padding(.top, ProSpacing.xxl)
                } else {
                    totalsCard
                    if state.isEmpty {
                        emptyState
                    } else {
                        breakdownCard
                    }
                }
            }
            .padding(.horizontal, ProSpacing.screenHorizontal)
            .padding(.vertical, ProSpacing.lg)
            .padding(.bottom, 100)
        }
        .background(ProColor.paper)
        .navigationTitle("Reports")
        .navigationBarTitleDisplayMode(.inline)
    }

    private var periodSwitcher: some View {
        ScrollViewReader { proxy in
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: ProSpacing.sm) {
                    ForEach(state.periods, id: \.index) { period in
                        let selected = period.index == state.selectedIndex
                        Button {
                            reports.viewModel.onPeriodSelected(index: period.index)
                        } label: {
                            Text(period.label)
                                .font(ProFont.label)
                                .foregroundStyle(selected ? .white : (period.isEmpty ? ProColor.muted : ProColor.ink2))
                                .padding(.horizontal, ProSpacing.lg)
                                .padding(.vertical, ProSpacing.sm)
                                .background(Capsule().fill(selected ? ProColor.primary : ProColor.card))
                        }
                        .id(period.index)
                    }
                }
                .padding(.horizontal, 1)
            }
            // Edge affordance: fade the trailing edge so a clipped chip reads as "more to scroll"
            // rather than as broken content.
            .mask(
                LinearGradient(
                    stops: [
                        .init(color: .black, location: 0),
                        .init(color: .black, location: 0.92),
                        .init(color: .clear, location: 1),
                    ],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )
            .onChange(of: state.selectedIndex) { index in
                withAnimation { proxy.scrollTo(index, anchor: .center) }
            }
        }
    }

    private var totalsCard: some View {
        VStack(alignment: .leading, spacing: ProSpacing.sm) {
            Text("SPENT IN \(state.selectedLabel)")
                .font(ProFont.eyebrow).kerning(1.1).foregroundStyle(ProColor.ink3)
            Text(state.total)
                .font(ProFont.heroAmount)
                .kerning(-0.8)
                .foregroundStyle(ProColor.ink)
                .minimumScaleFactor(0.6)
                .lineLimit(1)
            Text("\(state.dailyAverage) / day average")
                .font(ProFont.caption)
                .foregroundStyle(ProColor.ink3)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(ProSpacing.xl)
        .proCard()
    }

    private var breakdownCard: some View {
        VStack(alignment: .leading, spacing: ProSpacing.md) {
            Text("BY CATEGORY")
                .font(ProFont.eyebrow).kerning(1.1).foregroundStyle(ProColor.ink3)

            if state.allUncategorized {
                Text("Everything this month is uncategorized — add categories to see a breakdown.")
                    .font(ProFont.caption)
                    .foregroundStyle(ProColor.ink3)
            }

            ForEach(state.categories, id: \.categoryId) { row in
                VStack(alignment: .leading, spacing: ProSpacing.xs) {
                    HStack {
                        Text(row.label)
                            .font(ProFont.body)
                            .foregroundStyle(row.isOtherRollup ? ProColor.ink3 : ProColor.ink)
                        Spacer()
                        Text(row.amount).font(ProFont.body.weight(.semibold)).foregroundStyle(ProColor.ink)
                    }
                    GeometryReader { geo in
                        ZStack(alignment: .leading) {
                            Capsule().fill(ProColor.primaryTint)
                            Capsule()
                                .fill(row.isOtherRollup ? ProColor.muted : ProColor.primary)
                                .frame(width: geo.size.width * CGFloat(row.fraction))
                        }
                    }
                    .frame(height: 6)
                }
            }
        }
        .padding(ProSpacing.xl)
        .proCard()
    }

    private var emptyState: some View {
        VStack(spacing: ProSpacing.sm) {
            Image(systemName: "chart.pie").font(.system(size: 36)).foregroundStyle(ProColor.muted)
            Text("Nothing recorded in \(state.selectedLabel)")
                .font(ProFont.body)
                .foregroundStyle(ProColor.ink3)
        }
        .padding(.top, ProSpacing.xxl)
    }
}
