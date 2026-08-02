import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/05-journal.md`. Filtering and day grouping are done by the
/// shared `JournalViewModel`; this view only renders and forwards the query.
struct JournalView: View {
    @StateObject private var journal = KotlinViewModel<JournalUiState, JournalViewModel>.journal()
    @State private var query = ""

    private var state: JournalUiState { journal.state }

    var body: some View {
        VStack(spacing: 0) {
            header

            if state.isLoading {
                Spacer()
                ProgressView()
                Spacer()
            } else if state.isEmpty {
                Spacer()
                Text(query.isEmpty ? "No records yet" : "No matches for “\(query)”")
                    .font(ProFont.body)
                    .foregroundStyle(ProColor.ink3)
                Spacer()
            } else {
                ScrollView {
                    LazyVStack(spacing: ProSpacing.md) {
                        ForEach(state.dayGroups, id: \.dayTitle) { group in
                            DayGroupCard(group: group)
                                .padding(.horizontal, ProSpacing.screenHorizontal)
                        }
                    }
                    .padding(.vertical, ProSpacing.lg)
                    .padding(.bottom, 120)
                }
            }
        }
        .background(ProColor.paper)
        .ignoresSafeArea(edges: .top)
    }

    private var header: some View {
        VStack(alignment: .leading, spacing: ProSpacing.md) {
            Text("Journal")
                .font(.system(size: 30))
                .kerning(-0.45)
                .foregroundStyle(.white)

            HStack(spacing: ProSpacing.sm) {
                Image(systemName: "magnifyingglass")
                    .foregroundStyle(ProColor.muted)
                TextField("Search notes or categories", text: $query)
                    .font(ProFont.body)
                    .foregroundStyle(ProColor.ink)
                    .autocorrectionDisabled()
                if !query.isEmpty {
                    Button {
                        query = ""
                    } label: {
                        Image(systemName: "xmark.circle.fill").foregroundStyle(ProColor.muted)
                    }
                    .accessibilityLabel("Clear search")
                }
            }
            .padding(.horizontal, ProSpacing.md)
            .padding(.vertical, ProSpacing.md)
            .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, ProSpacing.screenHorizontal)
        .padding(.top, 72)
        .padding(.bottom, ProSpacing.xl)
        .background(LinearGradient.proHeader)
        .onChange(of: query) { newValue in
            journal.viewModel.onQueryChange(query: newValue)
        }
    }
}

extension KotlinViewModel where VM == JournalViewModel, State == JournalUiState {
    static func journal() -> KotlinViewModel<JournalUiState, JournalViewModel> {
        let vm: JournalViewModel = KoinHelper.shared.resolveJournal()
        return KotlinViewModel(
            viewModel: vm,
            initialState: vm.uiState.value as! JournalUiState,
            flow: { $0.uiState }
        )
    }
}
