import SwiftUI
import ProExpenseKit

/// 04 · Add Expense (amount step) — display amount + numeric keypad + quick Save.
///
/// Entry rules (max 7 whole digits, 2 fraction digits, single decimal, live comma grouping) and
/// the save itself come from KMP: `AmountInput` and `LoggingViewModel` are the exact same code the
/// Compose screen runs. Spec: `design-system-spec/screens/04-add-expense.md`.
struct AddExpenseView: View {
    let currencyCode: String
    let currencySymbol: String
    let defaultCategoryId: String

    @Environment(\.dismiss) private var dismiss
    @State private var rawAmount = ""
    @State private var errorMessage: String?
    @State private var isSaving = false

    private let viewModel = ProExpenseShell.shared.loggingViewModel()

    /// canProceed = value > 0 (spec).
    private var canProceed: Bool {
        (AmountInput.shared.numericValue(rawValue: rawAmount)?.doubleValue ?? 0) > 0
    }

    var body: some View {
        VStack(spacing: 0) {
            header
            Spacer(minLength: Pro.Space.x26)
            amountDisplay
            if let errorMessage {
                Text(errorMessage)
                    .font(Pro.Font.caption)
                    .foregroundStyle(Pro.Color.danger)
                    .padding(.top, Pro.Space.x8)
            }
            Spacer(minLength: Pro.Space.x26)
            keypad
            saveButton
        }
        .padding(.horizontal, Pro.Space.screenPadding)
        .padding(.bottom, Pro.Space.x16)
        .background(Pro.Color.paper.ignoresSafeArea())
    }

    // MARK: Header

    private var header: some View {
        ZStack {
            Text("New expense")
                .font(Pro.Font.screenHeader)
                .foregroundStyle(Pro.Color.ink)
            HStack {
                Button { dismiss() } label: {
                    Image(systemName: "xmark")
                        .font(.system(size: 17, weight: .regular))
                        .foregroundStyle(Pro.Color.ink3)
                        .frame(width: 44, height: 44)
                }
                .accessibilityLabel("Close")
                Spacer()
                Color.clear.frame(width: 44, height: 44) // equal spacer keeps the title centred
            }
        }
        .padding(.top, Pro.Space.x8)
    }

    // MARK: Amount

    /// The figure is not one flat string: the symbol is ≈0.47× the digits in primary, and the
    /// decimals are de-emphasised (spec §Amounts).
    private var amountDisplay: some View {
        let display = AmountInput.shared.formatDisplay(rawValue: rawAmount)
        let parts = display.split(separator: ".", maxSplits: 1, omittingEmptySubsequences: false)
        let whole = String(parts[0])
        let fraction = parts.count > 1 ? "." + String(parts[1]) : ""

        return HStack(alignment: .top, spacing: 2) {
            Text(currencySymbol)
                .font(.system(size: 30, weight: .regular))
                .foregroundStyle(Pro.Color.clay)
                .padding(.top, 6)
            Text(whole)
                .font(Pro.Font.displayAmount)
                .foregroundStyle(rawAmount.isEmpty ? Pro.Color.muted2 : Pro.Color.ink)
            if !fraction.isEmpty {
                Text(fraction)
                    .font(Pro.Font.displayAmount)
                    .foregroundStyle(Pro.Color.ink3)
            }
        }
        .frame(maxWidth: .infinity, alignment: .center)
    }

    // MARK: Keypad

    private let keys: [[String]] = [
        ["1", "2", "3"],
        ["4", "5", "6"],
        ["7", "8", "9"],
        [".", "0", "⌫"],
    ]

    private var keypad: some View {
        VStack(spacing: Pro.Space.x8) {
            ForEach(keys, id: \.self) { row in
                HStack(spacing: Pro.Space.x8) {
                    ForEach(row, id: \.self) { key in
                        Button { press(key) } label: {
                            Text(key)
                                .font(.system(size: 22, weight: .regular))
                                .foregroundStyle(Pro.Color.ink)
                                .frame(maxWidth: .infinity)
                                .frame(height: 56)
                                .background(Pro.Color.card)
                                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                        }
                    }
                }
            }
        }
    }

    private func press(_ key: String) {
        errorMessage = nil
        rawAmount = key == "⌫"
            ? AmountInput.shared.applyBackspace(rawValue: rawAmount)
            : AmountInput.shared.applyKey(rawValue: rawAmount, key: key)
    }

    // MARK: Save

    private var saveButton: some View {
        Button {
            Task { await save() }
        } label: {
            Text(isSaving ? "Saving…" : "Save")
                .font(Pro.Font.emphasis)
                .foregroundStyle(Pro.Color.onFilled)
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(canProceed ? Pro.Color.clay : Pro.Color.muted2)
                .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        }
        .disabled(!canProceed || isSaving)
        .padding(.top, Pro.Space.x16)
    }

    private func save() async {
        guard canProceed else {
            errorMessage = "Amount must be greater than \(currencySymbol)0"
            return
        }
        isSaving = true
        defer { isSaving = false }

        let input = SaveExpenseInput(
            rawAmount: rawAmount,
            currencyCode: currencyCode,
            categoryId: defaultCategoryId,
            note: "",
            recordedAtEpochMillis: Int64(Date().timeIntervalSince1970 * 1000),
            linkTagId: nil,
            linkTagKind: nil,
            homeCurrencyCode: currencyCode,
            exchangeRateRaw: "1",
            type: .expense
        )

        do {
            let outcome = try await viewModel.save(input: input)
            switch outcome {
            case is SaveExpenseOutcomeSaved:
                dismiss()
            case is SaveExpenseOutcomeInvalidAmount:
                errorMessage = "Amount must be greater than \(currencySymbol)0"
            case let failed as SaveExpenseOutcomeFailed:
                errorMessage = failed.message
            default:
                errorMessage = "Could not save this expense"
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
