import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/09-debt-tracker.md`. Lent/owed ledger.
/// The net total and the active/settled split come from the shared `AggregateDebtsUseCase`.
struct DebtTrackerView: View {
    @StateObject private var debt = Shell.debt()
    @State private var showCreate = false

    private var state: DebtUiState { debt.state }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                directionPicker
                netCard

                if state.isLoading {
                    Spacer(); ProgressView(); Spacer()
                } else if state.isEmpty {
                    Spacer(); emptyState; Spacer()
                } else {
                    list
                }
            }
            .background(ProColor.paper)
            .navigationTitle("Debts")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { showCreate = true } label: { Image(systemName: "plus") }
                        .accessibilityLabel("New debt")
                }
            }
        }
        .sheet(isPresented: $showCreate) {
            DebtEditorSheet(debt: debt, isPresented: $showCreate)
                .presentationDetents([.medium])
                .presentationCornerRadius(ProRadius.sheet)
        }
    }

    private var directionPicker: some View {
        Picker("Direction", selection: Binding(
            get: { state.direction },
            set: { debt.viewModel.onDirectionChange(direction: $0) }
        )) {
            Text("Owed to me").tag(DebtDirection.owedToMe)
            Text("I owe").tag(DebtDirection.iOwe)
        }
        .pickerStyle(.segmented)
        .padding(.horizontal, ProSpacing.screenHorizontal)
        .padding(.vertical, ProSpacing.md)
    }

    private var netCard: some View {
        VStack(alignment: .leading, spacing: ProSpacing.xs) {
            Text(state.isLentTab ? "OWED TO YOU" : "YOU OWE")
                .font(ProFont.eyebrow).kerning(1.1).foregroundStyle(ProColor.ink3)
            Text(state.netLabel)
                .font(ProFont.heroAmount)
                .kerning(-0.8)
                .foregroundStyle(state.isLentTab ? ProColor.success : ProColor.danger)
                .minimumScaleFactor(0.6)
                .lineLimit(1)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(ProSpacing.xl)
        .proCard()
        .padding(.horizontal, ProSpacing.screenHorizontal)
    }

    private var list: some View {
        List {
            if !state.active.isEmpty {
                Section("ACTIVE") {
                    ForEach(state.active, id: \.debtId) { row in
                        DebtRowView(row: row)
                            .swipeActions {
                                Button("Settle") { Task { await debt.viewModel.settle(debtId: row.debtId) } }
                                    .tint(ProColor.success)
                                Button("Delete", role: .destructive) {
                                    Task { await debt.viewModel.delete(debtId: row.debtId) }
                                }
                            }
                    }
                }
            }
            if !state.settled.isEmpty {
                Section("SETTLED") {
                    ForEach(state.settled, id: \.debtId) { row in
                        DebtRowView(row: row)
                            .swipeActions {
                                Button("Delete", role: .destructive) {
                                    Task { await debt.viewModel.delete(debtId: row.debtId) }
                                }
                            }
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
        .scrollContentBackground(.hidden)
    }

    private var emptyState: some View {
        VStack(spacing: ProSpacing.sm) {
            Image(systemName: "person.2").font(.system(size: 40)).foregroundStyle(ProColor.muted)
            Text("No debts tracked").font(ProFont.title).foregroundStyle(ProColor.ink)
            Text("Record money you lent or borrowed so nothing gets forgotten.")
                .font(ProFont.caption).foregroundStyle(ProColor.ink3).multilineTextAlignment(.center)
        }
        .padding(.horizontal, ProSpacing.xxl)
    }
}

private struct DebtRowView: View {
    let row: DebtRow

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(row.personName)
                    .font(ProFont.body)
                    .foregroundStyle(ProColor.ink)
                    .strikethrough(row.isSettled)
                if let due = row.dueLabel {
                    Text("Due \(due)").font(ProFont.caption).foregroundStyle(ProColor.ink3)
                } else if let note = row.note {
                    Text(note).font(ProFont.caption).foregroundStyle(ProColor.ink3).lineLimit(1)
                }
            }
            Spacer()
            Text(row.amount)
                .font(ProFont.body.weight(.semibold))
                .foregroundStyle(row.isSettled ? ProColor.muted : ProColor.ink)
        }
    }
}

private struct DebtEditorSheet: View {
    @ObservedObject var debt: DebtVM
    @Binding var isPresented: Bool

    @State private var personName = ""
    @State private var rawAmount = ""
    @State private var note = ""
    @State private var hasDueDate = false
    @State private var dueDate = Date()
    @State private var recordAsTransaction = false
    @FocusState private var nameFocused: Bool

    var body: some View {
        NavigationStack {
            Form {
                TextField("Person's name", text: $personName)
                    .focused($nameFocused)
                    .onSubmit { Task { await debt.viewModel.checkConflict(personName: personName) } }
                TextField("Amount", text: $rawAmount).keyboardType(.decimalPad)
                TextField("Note (optional)", text: $note)
                Toggle("Has a due date", isOn: $hasDueDate)
                if hasDueDate {
                    DatePicker("Due", selection: $dueDate, displayedComponents: .date)
                }
                Toggle("Also record as a transaction", isOn: $recordAsTransaction)

                if debt.state.conflictWarning {
                    Text("This person already has an active debt on the other side.")
                        .font(ProFont.caption)
                        .foregroundStyle(ProColor.highlight)
                }
            }
            .navigationTitle("New debt")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        debt.viewModel.clearConflictWarning()
                        isPresented = false
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        Task {
                            let saved = await debt.viewModel.create(
                                personName: personName,
                                rawAmount: rawAmount,
                                dueEpochMillis: hasDueDate
                                    ? KotlinLong(value: Int64(dueDate.timeIntervalSince1970 * 1000))
                                    : nil,
                                note: note.isEmpty ? nil : note,
                                recordAsTransaction: recordAsTransaction
                            )
                            if saved == true {
                                debt.viewModel.clearConflictWarning()
                                isPresented = false
                            }
                        }
                    }
                    .disabled(personName.isEmpty || rawAmount.isEmpty)
                }
            }
            .onAppear { nameFocused = true }
        }
    }
}
