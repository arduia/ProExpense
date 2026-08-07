import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/02-onboarding.md` (carousel) and `02P-profile-setup.md`.
/// Page order, the skip target, and what counts as "complete" all come from the shared
/// `OnboardingViewModel` — this view only renders and forwards intent.
struct OnboardingView: View {
    @StateObject private var onboarding = Shell.onboarding()
    /// Called once onboarding is durably persisted, so the shell can advance its gate.
    let onComplete: (String) -> Void

    private var state: OnboardingUiState { onboarding.state }

    var body: some View {
        Group {
            switch state.step {
            case .carousel: carousel
            case .profileSetup: ProfileSetupView(onboarding: onboarding, onComplete: onComplete)
            default: carousel
            }
        }
        .background(ProColor.paper)
    }

    private var carousel: some View {
        VStack(spacing: 0) {
            HStack {
                Spacer()
                Button("Skip") { onboarding.viewModel.onSkip() }
                    .font(ProFont.label)
                    .foregroundStyle(ProColor.ink3)
            }
            .padding(.horizontal, ProSpacing.screenHorizontal)
            .padding(.top, ProSpacing.lg)

            TabView(selection: Binding(
                get: { Int(state.pageIndex) },
                set: { onboarding.viewModel.onPageChange(index: Int32($0)) }
            )) {
                ForEach(Array(pages.enumerated()), id: \.offset) { index, page in
                    OnboardingPageView(page: page).tag(index)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))

            pageIndicator

            Button(state.isLastPage ? "Get started" : "Next") {
                onboarding.viewModel.onNext()
            }
            .font(ProFont.title)
            .foregroundStyle(.white)
            .frame(maxWidth: .infinity, minHeight: 52)
            .background(ProColor.primary, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))
            .padding(.horizontal, ProSpacing.screenHorizontal)
            .padding(.bottom, ProSpacing.xxl)
        }
    }

    private var pageIndicator: some View {
        HStack(spacing: ProSpacing.sm) {
            ForEach(0..<pages.count, id: \.self) { index in
                Capsule()
                    .fill(index == Int(state.pageIndex) ? ProColor.primary : ProColor.muted.opacity(0.4))
                    .frame(width: index == Int(state.pageIndex) ? 20 : 8, height: 8)
                    .animation(.easeInOut(duration: 0.2), value: state.pageIndex)
            }
        }
        .padding(.vertical, ProSpacing.xl)
    }

    private var pages: [OnboardingPageContent] {
        [
            .init(icon: "sparkles", title: "Welcome to Pro Expense",
                  detail: "Your private, offline finance notebook. No accounts, no tracking."),
            .init(icon: "bolt.fill", title: "Log in seconds",
                  detail: "Tap +, type an amount, pick a category. That's it."),
            .init(icon: "person.2.fill", title: "Split shared costs",
                  detail: "Divide a bill evenly or set custom shares per person."),
            .init(icon: "calendar", title: "Budget an event",
                  detail: "Set a budget for a trip and watch what's left as you spend."),
            .init(icon: "list.bullet.rectangle", title: "Review your journal",
                  detail: "Every record, grouped by day and searchable."),
        ]
    }
}

struct OnboardingPageContent {
    let icon: String
    let title: String
    let detail: String
}

private struct OnboardingPageView: View {
    let page: OnboardingPageContent

    var body: some View {
        VStack(spacing: ProSpacing.xl) {
            Spacer()
            Image(systemName: page.icon)
                .font(.system(size: 64, weight: .light))
                .foregroundStyle(ProColor.primary)
                .frame(height: 120)
            Text(page.title)
                .font(.system(size: 30))
                .kerning(-0.45)
                .foregroundStyle(ProColor.ink)
                .multilineTextAlignment(.center)
            Text(page.detail)
                .font(ProFont.body)
                .foregroundStyle(ProColor.ink3)
                .multilineTextAlignment(.center)
            Spacer()
        }
        .padding(.horizontal, ProSpacing.xxl)
    }
}

/// 02P — name and home currency merged onto one screen, per the spec's "P1 · Profile + currency".
struct ProfileSetupView: View {
    @ObservedObject var onboarding: OnboardingVM
    let onComplete: (String) -> Void

    @State private var showCurrencyPicker = false
    @FocusState private var nameFocused: Bool

    private var state: OnboardingUiState { onboarding.state }

    var body: some View {
        VStack(alignment: .leading, spacing: ProSpacing.xl) {
            VStack(alignment: .leading, spacing: ProSpacing.xs) {
                Text("ABOUT YOU")
                    .font(ProFont.eyebrow).kerning(1.2).foregroundStyle(ProColor.ink3)
                Text("What should we call you?")
                    .font(.system(size: 30)).kerning(-0.45).foregroundStyle(ProColor.ink)
            }
            .padding(.top, ProSpacing.xxl)

            TextField("Your name (optional)", text: Binding(
                get: { state.displayName },
                set: { onboarding.viewModel.onNameChange(name: $0) }
            ))
            .font(ProFont.body)
            .focused($nameFocused)
            .padding(ProSpacing.md)
            .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))

            Button {
                showCurrencyPicker = true
            } label: {
                HStack {
                    Text("Home currency").font(ProFont.body).foregroundStyle(ProColor.ink)
                    Spacer()
                    Text("\(state.currencySymbol) \(state.currencyCode)")
                        .font(ProFont.body).foregroundStyle(ProColor.primary)
                    Image(systemName: "chevron.right").font(.caption).foregroundStyle(ProColor.muted)
                }
                .padding(ProSpacing.md)
                .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))
            }

            if let error = state.errorMessage {
                Text(error).font(ProFont.caption).foregroundStyle(ProColor.danger)
            }

            Spacer()

            Button {
                Task {
                    if await onboarding.viewModel.finish() == true {
                        onComplete(state.displayName)
                    }
                }
            } label: {
                Text(state.isSaving ? "Saving…" : "Start tracking")
                    .font(ProFont.title)
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity, minHeight: 52)
                    .background(
                        RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous)
                            .fill(state.canContinue ? ProColor.primary : ProColor.muted)
                    )
            }
            .disabled(!state.canContinue)
            .padding(.bottom, ProSpacing.xxl)
        }
        .padding(.horizontal, ProSpacing.screenHorizontal)
        .background(ProColor.paper)
        // The screen's whole purpose is typing a name — start with the field focused and the
        // keyboard up rather than making the user tap first.
        .onAppear { nameFocused = true }
        .sheet(isPresented: $showCurrencyPicker) {
            CurrencyPickerSheet(onboarding: onboarding, isPresented: $showCurrencyPicker)
        }
    }
}

private struct CurrencyPickerSheet: View {
    @ObservedObject var onboarding: OnboardingVM
    @Binding var isPresented: Bool

    private var state: OnboardingUiState { onboarding.state }

    var body: some View {
        NavigationStack {
            List(state.currencyOptions, id: \.code) { choice in
                Button {
                    onboarding.viewModel.onCurrencySelected(code: choice.code)
                    isPresented = false
                } label: {
                    HStack {
                        Text(choice.symbol).font(ProFont.body).foregroundStyle(ProColor.primary).frame(width: 36, alignment: .leading)
                        VStack(alignment: .leading, spacing: 2) {
                            Text(choice.code).font(ProFont.body).foregroundStyle(ProColor.ink)
                            Text(choice.name).font(ProFont.caption).foregroundStyle(ProColor.ink3)
                        }
                        Spacer()
                        if choice.code == state.currencyCode {
                            Image(systemName: "checkmark").foregroundStyle(ProColor.primary)
                        }
                    }
                }
            }
            .searchable(text: Binding(
                get: { state.currencyQuery },
                set: { onboarding.viewModel.onCurrencyQueryChange(query: $0) }
            ), prompt: "Search currency")
            .navigationTitle("Home currency")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
