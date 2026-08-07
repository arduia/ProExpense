import SwiftUI
import ProExpenseKit

/// Shared 3×4 numeric keypad for both PIN screens (14 · PIN Setup, 15 · PIN Entry).
struct PinKeypad: View {
    let onDigit: (Int) -> Void
    let onBackspace: () -> Void
    var isEnabled: Bool = true

    private let rows: [[String]] = [["1", "2", "3"], ["4", "5", "6"], ["7", "8", "9"], ["", "0", "⌫"]]

    var body: some View {
        VStack(spacing: ProSpacing.md) {
            ForEach(rows, id: \.self) { row in
                HStack(spacing: ProSpacing.md) {
                    ForEach(row, id: \.self) { key in
                        keyButton(key)
                    }
                }
            }
        }
        .opacity(isEnabled ? 1 : 0.4)
        .disabled(!isEnabled)
    }

    @ViewBuilder
    private func keyButton(_ key: String) -> some View {
        if key.isEmpty {
            Color.clear.frame(width: 72, height: 64)
        } else {
            Button {
                if key == "⌫" {
                    onBackspace()
                } else if let digit = Int(key) {
                    onDigit(digit)
                }
            } label: {
                Text(key)
                    .font(.system(size: 26, weight: .regular))
                    .foregroundStyle(ProColor.ink)
                    .frame(width: 72, height: 64)
                    .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))
            }
            .accessibilityLabel(key == "⌫" ? "Delete" : key)
        }
    }
}

/// Filled/empty dots for a PIN buffer. Shakes and turns danger-toned on a wrong attempt.
struct PinDots: View {
    let filled: Int
    let total: Int
    var isError: Bool = false

    var body: some View {
        HStack(spacing: ProSpacing.md) {
            ForEach(0..<total, id: \.self) { index in
                Circle()
                    .strokeBorder(isError ? ProColor.danger : ProColor.primary, lineWidth: 1.5)
                    .background(
                        Circle().fill(index < filled ? (isError ? ProColor.danger : ProColor.primary) : .clear)
                    )
                    .frame(width: 14, height: 14)
            }
        }
        .modifier(ShakeEffect(animatableData: isError ? 1 : 0))
    }
}

private struct ShakeEffect: GeometryEffect {
    var animatableData: CGFloat

    func effectValue(size: CGSize) -> ProjectionTransform {
        let translation = 8 * sin(animatableData * .pi * 3)
        return ProjectionTransform(CGAffineTransform(translationX: translation, y: 0))
    }
}

/// Spec: `design-system-spec/screens/15-pin-entry.md`. Every keypad rule — buffer handling,
/// error-consumes-digits, lockout precedence, countdown label — lives in the shared
/// `PinEntryViewModel` on top of `PinEntryLogic`.
struct PinEntryView: View {
    @StateObject private var pin = Shell.pinEntry()
    let onUnlocked: () -> Void

    private var state: PinEntryScreenState { pin.state }

    var body: some View {
        VStack(spacing: ProSpacing.xl) {
            Spacer()

            Image(systemName: "lock.fill")
                .font(.system(size: 32))
                .foregroundStyle(ProColor.primary)

            Text("Enter your PIN")
                .font(.system(size: 22))
                .foregroundStyle(ProColor.ink)

            PinDots(
                filled: Int(state.filledDots),
                total: Int(state.pinLength),
                isError: state.mode == .error
            )
            .animation(.default, value: state.mode)

            Text(statusText)
                .font(ProFont.caption)
                .foregroundStyle(state.mode == .default_ ? .clear : ProColor.danger)
                .frame(height: 18)

            Spacer()

            PinKeypad(
                onDigit: { pin.viewModel.onDigit(digit: Int32($0)) },
                onBackspace: { pin.viewModel.onBackspace() },
                isEnabled: !state.isLockedOut
            )
            .padding(.bottom, ProSpacing.xxl)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(ProColor.paper)
        .onChange(of: state.unlocked) { unlocked in
            if unlocked { onUnlocked() }
        }
    }

    private var statusText: String {
        if let countdown = state.countdownLabel {
            return "Too many attempts — try again in \(countdown)"
        }
        if let error = state.errorMessage { return error }
        return state.mode == .error ? "Incorrect PIN, try again" : " "
    }
}

/// Spec: `design-system-spec/screens/14-pin-setup.md`. Enter → confirm → security question.
struct PinSetupView: View {
    @StateObject private var setup = Shell.pinSetup()
    @Environment(\.dismiss) private var dismiss
    let onConfigured: () -> Void

    private var state: PinSetupScreenState { setup.state }

    var body: some View {
        VStack(spacing: ProSpacing.xl) {
            switch state.stage {
            case .securityQuestion: securityQuestionStep
            default: pinStep
            }
        }
        .padding(.horizontal, ProSpacing.screenHorizontal)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(ProColor.paper)
        .onChange(of: state.completed) { done in
            if done {
                onConfigured()
                dismiss()
            }
        }
    }

    private var pinStep: some View {
        VStack(spacing: ProSpacing.xl) {
            Spacer()
            Text(state.stage == .confirm ? "Confirm your PIN" : "Choose a PIN")
                .font(.system(size: 22))
                .foregroundStyle(ProColor.ink)

            PinDots(
                filled: state.activeBuffer.count,
                total: Int(state.pinLength),
                isError: state.mismatch
            )

            if state.mismatch {
                Button("PINs don't match — try again") { setup.viewModel.onRetryConfirm() }
                    .font(ProFont.caption)
                    .foregroundStyle(ProColor.danger)
            } else {
                Text(" ").font(ProFont.caption).frame(height: 18)
            }

            Spacer()

            PinKeypad(
                onDigit: { setup.viewModel.onDigit(digit: Int32($0)) },
                onBackspace: { setup.viewModel.onBackspace() }
            )
            .padding(.bottom, ProSpacing.xxl)
        }
    }

    private var securityQuestionStep: some View {
        VStack(alignment: .leading, spacing: ProSpacing.lg) {
            Text("Recovery question")
                .font(.system(size: 22))
                .foregroundStyle(ProColor.ink)
                .padding(.top, ProSpacing.xxl)

            Text("Used to reset your PIN if you forget it. Stored on this device only.")
                .font(ProFont.caption)
                .foregroundStyle(ProColor.ink3)

            ForEach(state.questionIds, id: \.self) { questionId in
                Button {
                    setup.viewModel.onQuestionSelected(questionId: questionId)
                } label: {
                    HStack {
                        Text(questionText(questionId))
                            .font(ProFont.body)
                            .foregroundStyle(ProColor.ink)
                            .multilineTextAlignment(.leading)
                        Spacer()
                        if questionId == state.questionId {
                            Image(systemName: "checkmark.circle.fill").foregroundStyle(ProColor.primary)
                        }
                    }
                    .padding(ProSpacing.md)
                    .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))
                }
            }

            TextField("Your answer", text: Binding(
                get: { state.answer },
                set: { setup.viewModel.onAnswerChange(answer: $0) }
            ))
            .font(ProFont.body)
            .padding(ProSpacing.md)
            .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))

            if let error = state.errorMessage {
                Text(error).font(ProFont.caption).foregroundStyle(ProColor.danger)
            }

            Spacer()

            Button {
                Task { _ = await setup.viewModel.save() }
            } label: {
                Text(state.isSaving ? "Saving…" : "Turn on PIN lock")
                    .font(ProFont.title)
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity, minHeight: 52)
                    .background(
                        RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous)
                            .fill(state.canSave ? ProColor.primary : ProColor.muted)
                    )
            }
            .disabled(!state.canSave)
            .padding(.bottom, ProSpacing.xxl)
        }
    }

    /// Ids come from the shared `SecurityQuestionCatalog`; only the text is per-platform.
    private func questionText(_ id: String) -> String {
        switch id {
        case "pet": return "What was your first pet's name?"
        case "city": return "What city were you born in?"
        case "school": return "What was the name of your first school?"
        case "maiden": return "What is your mother's maiden name?"
        case "nickname": return "What was your childhood nickname?"
        default: return id
        }
    }
}
