import SwiftUI
import ProExpenseKit

/// iOS counterpart to Android's `ExpenseApp` gate. The branching decision is *not* made here — it
/// comes from `AppShellUiState.gate`, computed in shared Kotlin, so both platforms cannot disagree
/// about splash/onboarding/PIN precedence.
struct RootView: View {
    @StateObject private var shell = KotlinViewModel<AppShellUiState, AppShellViewModel>.appShell()
    @Environment(\.scenePhase) private var scenePhase

    var body: some View {
        Group {
            switch shell.state.gate {
            case .splash:
                SplashView()
            case .onboarding:
                // Onboarding is not part of this vertical slice — the shared gate already routes
                // here, so the SwiftUI flow drops in without touching gate logic.
                PlaceholderView(title: "Onboarding", detail: "Not yet ported to SwiftUI.")
            case .pinLock:
                PlaceholderView(title: "Enter PIN", detail: "PIN entry is not yet ported to SwiftUI.")
            case .ready:
                MainTabView()
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
    @State private var showAddExpense = false

    var body: some View {
        ZStack(alignment: .bottom) {
            TabView {
                HomeView()
                    .tabItem { Label("Home", systemImage: "house.fill") }
                JournalView()
                    .tabItem { Label("Journal", systemImage: "list.bullet") }
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

struct PlaceholderView: View {
    let title: String
    let detail: String

    var body: some View {
        VStack(spacing: ProSpacing.sm) {
            Text(title).font(ProFont.title).foregroundStyle(ProColor.ink)
            Text(detail).font(ProFont.caption).foregroundStyle(ProColor.ink3)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(ProColor.paper)
    }
}
