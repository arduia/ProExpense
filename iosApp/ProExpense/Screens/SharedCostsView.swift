import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/10-shared-costs.md`. Split a bill across people.
/// Every split rule — equal share, custom-share sync, "Person N"/"You" name defaults, and the
/// does-the-custom-sum-match check — comes from the shared `SharedCostSplitLogic`.
struct SharedCostsView: View {
    @StateObject private var shared = Shell.sharedCost()

    private var state: SharedCostUiState { shared.state }

    var body: some View {
        NavigationStack {
            Group {
                if state.isLoading {
                    ProgressView()
                } else if state.isEmpty {
                    emptyState
                } else {
                    list
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(ProColor.paper)
            .navigationTitle("Splits")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { shared.viewModel.openEditor() } label: { Image(systemName: "plus") }
                        .accessibilityLabel("New split")
                }
            }
        }
        .sheet(isPresented: Binding(
            get: { state.isEditorOpen },
            set: { if !$0 { shared.viewModel.closeEditor() } }
        )) {
            SplitEditorSheet(shared: shared)
                .presentationDetents([.large])
                .presentationCornerRadius(ProRadius.sheet)
        }
    }

    private var list: some View {
        List {
            ForEach(state.rows, id: \.sharedCostId) { row in
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(row.title.isEmpty ? "Untitled split" : row.title)
                            .font(ProFont.body).foregroundStyle(ProColor.ink)
                        Text("\(row.peopleCount) people · \(row.dateLabel)")
                            .font(ProFont.caption).foregroundStyle(ProColor.ink3)
                    }
                    Spacer()
                    Text(row.total).font(ProFont.body.weight(.semibold)).foregroundStyle(ProColor.ink)
                }
                .swipeActions {
                    Button("Archive") { Task { await shared.viewModel.archive(sharedCostId: row.sharedCostId) } }
                        .tint(ProColor.ink3)
                    Button("Delete", role: .destructive) {
                        Task { await shared.viewModel.delete(sharedCostId: row.sharedCostId) }
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
        .scrollContentBackground(.hidden)
    }

    private var emptyState: some View {
        VStack(spacing: ProSpacing.sm) {
            Image(systemName: "divide.circle").font(.system(size: 40)).foregroundStyle(ProColor.muted)
            Text("No splits yet").font(ProFont.title).foregroundStyle(ProColor.ink)
            Text("Split a bill evenly or set a custom share per person.")
                .font(ProFont.caption).foregroundStyle(ProColor.ink3).multilineTextAlignment(.center)
        }
        .padding(.horizontal, ProSpacing.xxl)
    }
}

private struct SplitEditorSheet: View {
    @ObservedObject var shared: SharedCostVM
    @FocusState private var totalFocused: Bool

    private var state: SharedCostUiState { shared.state }

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("What was it for?", text: Binding(
                        get: { state.title },
                        set: { shared.viewModel.onTitleChange(title: $0) }
                    ))
                    TextField("Total", text: Binding(
                        get: { state.rawTotal },
                        set: { shared.viewModel.onTotalChange(rawTotal: $0) }
                    ))
                    .keyboardType(.decimalPad)
                    .focused($totalFocused)
                }

                Section("PEOPLE") {
                    Stepper(
                        "\(state.peopleCount) people · \(state.perPersonLabel) each",
                        value: Binding(
                            get: { Int(state.peopleCount) },
                            set: { shared.viewModel.onPeopleCountChange(count: Int32($0)) }
                        ),
                        in: 1...20
                    )
                    .font(ProFont.body)

                    Picker("Split", selection: Binding(
                        get: { state.mode },
                        set: { shared.viewModel.onModeChange(mode: $0) }
                    )) {
                        Text("Equal").tag(SharedSplitMode.equal)
                        Text("Custom").tag(SharedSplitMode.custom)
                    }
                    .pickerStyle(.segmented)
                }

                Section("SHARES") {
                    ForEach(Array(state.participants.enumerated()), id: \.offset) { index, participant in
                        HStack {
                            TextField("Name", text: Binding(
                                get: { participant.name },
                                set: { shared.viewModel.onNameChange(index: Int32(index), name: $0) }
                            ))
                            Spacer()
                            if state.mode == .custom {
                                TextField("0", text: Binding(
                                    get: { state.customShareRaws[safe: index] ?? "" },
                                    set: { shared.viewModel.onCustomShareChange(index: Int32(index), rawShare: $0) }
                                ))
                                .keyboardType(.decimalPad)
                                .multilineTextAlignment(.trailing)
                                .frame(width: 90)
                            } else {
                                Text(participant.share).foregroundStyle(ProColor.ink3)
                            }
                        }
                    }

                    if !state.customSumMatchesTotal {
                        // Shares are never auto-rebalanced (US-SHC-2/4) — surface the divergence
                        // rather than silently correcting the user's numbers.
                        Text("Shares don't add up to \(state.totalLabel). That's allowed — the total stays as entered.")
                            .font(ProFont.caption)
                            .foregroundStyle(ProColor.highlight)
                    }
                }

                Section {
                    Toggle("Also record as a transaction", isOn: Binding(
                        get: { state.recordAsTransaction },
                        set: { shared.viewModel.onRecordAsTransactionChange(enabled: $0) }
                    ))
                }
            }
            .navigationTitle("New split")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { shared.viewModel.closeEditor() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") { Task { _ = await shared.viewModel.save() } }
                        .disabled(!state.canSave)
                }
            }
            // The split starts with an amount — focus it so the keypad is already up.
            .onAppear { totalFocused = true }
        }
    }
}

private extension Array {
    subscript(safe index: Int) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}
