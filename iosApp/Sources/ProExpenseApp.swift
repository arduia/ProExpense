import SwiftUI
import ProExpenseKit

@main
struct ProExpenseApp: App {
    init() {
        // Counterpart to ExpenseApplication.ensureStarted() on Android: opens the encrypted
        // database (Keychain-held key) and registers every shared feature module.
        KoinIosKt.startProExpenseKoin()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}

/// Splash → Home. Onboarding, the PIN gate and the remaining tabs are still Android-only —
/// see docs/ios_compatibility_plan.md Phase 2.
struct RootView: View {
    @StateObject private var store = ShellStore()
    @State private var showSplash = true

    private let splashDuration: TimeInterval = 1.8

    var body: some View {
        ZStack {
            if showSplash {
                SplashView()
                    .transition(.opacity)
            } else {
                HomeView(store: store)
            }
        }
        .task {
            try? await Task.sleep(nanoseconds: UInt64(splashDuration * 1_000_000_000))
            withAnimation(.easeInOut(duration: 0.25)) { showSplash = false }
        }
    }
}
