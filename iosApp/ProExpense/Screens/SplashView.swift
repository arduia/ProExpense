import SwiftUI

/// Spec: `design-system-spec/screens/01-splash.md` — logo tile + wordmark on paper, no tagline,
/// no interaction. Routing is owned by `AppShellUiState.gate`, not by this view.
struct SplashView: View {
    var body: some View {
        VStack(spacing: 18) {
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .fill(ProColor.primary)
                .frame(width: 84, height: 84)
                .overlay(
                    Image(systemName: "wallet.pass.fill")
                        .font(.system(size: 38, weight: .medium))
                        .foregroundStyle(.white)
                )

            Text("Pro Expense")
                .font(.system(size: 30, weight: .regular))
                .kerning(-0.45)
                .foregroundStyle(ProColor.ink)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(ProColor.paper)
        .ignoresSafeArea()
    }
}

#Preview {
    SplashView()
}
