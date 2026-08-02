import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/04-add-expense.md`. Amount editing and the save path both run
/// through shared Kotlin (`AmountInput` rules, `LogExpenseUseCase`), so the max-amount guard and
/// validation behave identically to Android.
struct AddExpenseView: View {
    @StateObject private var form = KotlinViewModel<AddExpenseUiState, AddExpenseViewModel>.addExpense()
    @Environment(\.dismiss) private var dismiss
    @State private var errorMessage: String?
    @State private var isSaving = false

    private var state: AddExpenseUiState { form.state }

    private let keys = ["1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0"]

    var body: some View {
        VStack(spacing: ProSpacing.lg) {
            amountDisplay
            categoryChips
            noteField
            keypad
            saveButton
        }
        .padding(.horizontal, ProSpacing.screenHorizontal)
        .padding(.top, ProSpacing.xxl)
        .padding(.bottom, ProSpacing.lg)
        .background(ProColor.paper)
        .alert("Couldn’t save", isPresented: .constant(errorMessage != nil)) {
            Button("OK") { errorMessage = nil }
        } message: {
            Text(errorMessage ?? "")
        }
    }

    private var amountDisplay: some View {
        HStack(alignment: .firstTextBaseline, spacing: 2) {
            Text(state.currencySymbol)
                .font(ProFont.heroSymbol)
                .foregroundStyle(ProColor.primary)
            Text(state.amountDisplay.isEmpty ? "0" : state.amountDisplay)
                .font(.system(size: 56, weight: .regular))
                .kerning(-1.4)
                .foregroundStyle(ProColor.ink)
                .lineLimit(1)
                .minimumScaleFactor(0.5)
        }
        .frame(maxWidth: .infinity, alignment: .center)
        .padding(.vertical, ProSpacing.lg)
    }

    private var categoryChips: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: ProSpacing.sm) {
                ForEach(state.categories, id: \.id) { category in
                    let selected = category.id == state.selectedCategoryId
                    Button {
                        form.viewModel.onCategorySelected(categoryId: category.id)
                    } label: {
                        Text(category.label)
                            .font(ProFont.label)
                            .foregroundStyle(selected ? Color.white : ProColor.ink2)
                            .padding(.horizontal, ProSpacing.lg)
                            .padding(.vertical, ProSpacing.sm)
                            .background(
                                Capsule().fill(selected ? ProColor.primary : ProColor.card)
                            )
                    }
                }
            }
            .padding(.horizontal, 1)
        }
        // Edge affordance: a hard clip at the scroll boundary reads as broken content, so the
        // trailing edge fades into the page background instead.
        .mask(
            LinearGradient(
                stops: [
                    .init(color: .black, location: 0),
                    .init(color: .black, location: 0.9),
                    .init(color: .clear, location: 1),
                ],
                startPoint: .leading,
                endPoint: .trailing
            )
        )
    }

    private var noteField: some View {
        TextField("Add a note", text: Binding(
            get: { state.note },
            set: { form.viewModel.onNoteChange(note: $0) }
        ))
        .font(ProFont.body)
        .padding(ProSpacing.md)
        .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))
    }

    private var keypad: some View {
        LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: ProSpacing.sm), count: 3), spacing: ProSpacing.sm) {
            ForEach(keys, id: \.self) { key in
                keyButton(key) { form.viewModel.onKey(key: key) }
            }
            keyButton("⌫") { form.viewModel.onBackspace() }
        }
    }

    private func keyButton(_ label: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 24, weight: .regular))
                .foregroundStyle(ProColor.ink)
                .frame(maxWidth: .infinity, minHeight: 56)
                .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))
        }
    }

    private var saveButton: some View {
        Button {
            Task { await save() }
        } label: {
            Text(isSaving ? "Saving…" : "Save")
                .font(ProFont.title)
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity, minHeight: 52)
                .background(
                    RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous)
                        .fill(state.canSave ? ProColor.primary : ProColor.muted)
                )
        }
        .disabled(!state.canSave || isSaving)
    }

    private func save() async {
        isSaving = true
        defer { isSaving = false }
        do {
            let outcome = try await form.viewModel.save()
            switch outcome {
            case is SaveExpenseOutcomeSaved:
                dismiss()
            case is SaveExpenseOutcomeInvalidAmount:
                errorMessage = "Enter an amount greater than zero."
            case is SaveExpenseOutcomeInvalidExchangeRate:
                errorMessage = "Enter a valid exchange rate."
            case let failed as SaveExpenseOutcomeFailed:
                errorMessage = failed.message
            default:
                errorMessage = "Unknown error."
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

extension KotlinViewModel where VM == AddExpenseViewModel, State == AddExpenseUiState {
    static func addExpense() -> KotlinViewModel<AddExpenseUiState, AddExpenseViewModel> {
        let vm: AddExpenseViewModel = KoinHelper.shared.resolveAddExpense()
        return KotlinViewModel(
            viewModel: vm,
            initialState: vm.uiState.value as! AddExpenseUiState,
            flow: { $0.uiState }
        )
    }
}
