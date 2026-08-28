import SwiftUI
import ProExpenseKit

/// iOS counterpart to Android's `ExpenseApp` gate. The branching decision is *not* made here — it
/// comes from `AppShellUiState.gate`, computed in shared Kotlin, so both platforms cannot disagree
/// about splash/onboarding/PIN precedence.
struct RootView: View {
    @StateObject private var shell = Shell.appShell()
    @Environment(\.scenePhase) private var scenePhase

    var body: some View {
        Group {
            switch shell.state.gate {
            case .splash:
                SplashView()
            case .onboarding:
                OnboardingView { name in
                    shell.viewModel.onOnboardingCompleted(displayName: name)
                }
            case .pinLock:
                PinEntryView { shell.viewModel.onUnlocked() }
            case .ready:
                MainTabView(onPinConfigured: { shell.viewModel.onPinConfigured(configured: $0) })
            default:
                SplashView()
            }
        }
        .animation(.easeInOut(duration: 0.25), value: shell.state.gate)
        .onChange(of: scenePhase) { phase in
            // Mirrors the Android ON_STOP observer: the shared ViewModel decides whether this
            // actually re-locks, honouring the "stay unlocked while switching apps" setting.
            if phase == .background {
                shell.viewModel.onEnterBackground()
            }
        }
    }
}

struct MainTabView: View {
    let onPinConfigured: (Bool) -> Void

    @State private var showAddExpense = false

    var body: some View {
        ZStack(alignment: .bottom) {
            TabView {
                HomeView()
                    .tabItem { Label("Home", systemImage: "house.fill") }
                JournalView()
                    .tabItem { Label("Journal", systemImage: "list.bullet") }
                EventBudgetView()
                    .tabItem { Label("Budgets", systemImage: "calendar") }
                MoreView(onPinConfigured: onPinConfigured)
                    .tabItem { Label("More", systemImage: "ellipsis") }
            }
            .tint(ProColor.primary)

            AddExpenseButton { showAddExpense = true }
                .padding(.bottom, 64)
        }
        .sheet(isPresented: $showAddExpense) {
            AddExpenseView()
                .presentationDetents([.large])
                .presentationCornerRadius(ProRadius.sheet)
        }
    }
}

private struct AddExpenseButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "plus")
                .font(.system(size: 22, weight: .semibold))
                .foregroundStyle(.white)
                .frame(width: 56, height: 56)
                .background(ProColor.primary, in: Circle())
                .shadow(color: ProColor.primary.opacity(0.4), radius: 10, y: 4)
        }
        .accessibilityLabel("Add expense")
    }
}
