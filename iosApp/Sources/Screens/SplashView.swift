import SwiftUI

/// 01 · Splash — centered product mark on paper, wordmark below.
/// Spec: `design-system-spec/screens/01-splash.md`.
struct SplashView: View {
    var body: some View {
        ZStack {
            Pro.Color.paper.ignoresSafeArea()

            VStack(spacing: 18) {
                RoundedRectangle(cornerRadius: Pro.Radius.tile, style: .continuous)
                    .fill(Pro.Color.clay)
                    .frame(width: 72, height: 72)
                    .overlay(
                        Image(systemName: "wallet.pass")
                            .font(.system(size: 32, weight: .regular))
                            .foregroundStyle(Pro.Color.onFilled)
                    )
                    .shadow(color: Pro.Shadow.heroColor, radius: 20, y: 8)

                Text("Pro Expense")
                    .font(Pro.Font.heroGreeting)
                    .kerning(-0.015 * 30)
                    .foregroundStyle(Pro.Color.ink)
            }
        }
    }
}

#Preview {
    SplashView()
}
