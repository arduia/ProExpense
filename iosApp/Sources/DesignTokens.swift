import SwiftUI

/// Design tokens transcribed from `design-system-spec/tokens.md` — the same source the Compose
/// theme reads. Values are literal so the two shells can be diffed against one spec.
enum Pro {
    // MARK: Color (single light theme — no dark variant in the spec)

    enum Color {
        static let clay = SwiftUI.Color(hex: 0x039BE5) // primary
        static let clayDeep = SwiftUI.Color(hex: 0x0288D1)
        static let clayTint = SwiftUI.Color(hex: 0xB3E5FC)
        static let sage = SwiftUI.Color(hex: 0x4CAF50) // success / income
        static let danger = SwiftUI.Color(hex: 0xEF5350)
        static let tag = SwiftUI.Color(hex: 0xFB8C00) // event @-tags
        static let tagTint = SwiftUI.Color(hex: 0xFFE0B2)

        static let paper = SwiftUI.Color(hex: 0xF5F5F5) // app background
        static let card = SwiftUI.Color.white
        static let ink = SwiftUI.Color(hex: 0x212121)
        static let ink3 = SwiftUI.Color(hex: 0x757575)
        static let muted = SwiftUI.Color(hex: 0x9E9E9E)
        static let muted2 = SwiftUI.Color(hex: 0xBDBDBD)
        static let lineStrong = SwiftUI.Color(hex: 0xE0E0E0)
        static let line = SwiftUI.Color(hex: 0x212121).opacity(0.10)
        static let line2 = SwiftUI.Color(hex: 0x212121).opacity(0.06)
        static let onFilled = SwiftUI.Color(hex: 0xFFFDF6) // warm white on filled surfaces
    }

    // MARK: Typography
    //
    // The spec's Inter / Manrope / Geist Mono families ship as Android `res/font` assets. They are
    // not bundled in this target yet, so display roles fall back to the system serif-free face at
    // the specified sizes/tracking; swap in the real families when they are added to Resources.

    enum Font {
        static let heroGreeting = SwiftUI.Font.system(size: 30, weight: .regular)
        static let cardAmount = SwiftUI.Font.system(size: 40, weight: .regular)
        static let displayAmount = SwiftUI.Font.system(size: 64, weight: .regular)
        static let sectionHead = SwiftUI.Font.system(size: 18, weight: .regular)
        static let screenHeader = SwiftUI.Font.system(size: 17, weight: .regular)
        static let rowAmount = SwiftUI.Font.system(size: 18, weight: .regular)
        static let body = SwiftUI.Font.system(size: 14, weight: .regular)
        static let emphasis = SwiftUI.Font.system(size: 14, weight: .semibold)
        static let caption = SwiftUI.Font.system(size: 11.5, weight: .regular)
        static let eyebrow = SwiftUI.Font.system(size: 11, weight: .medium).monospaced()
        static let timestamp = SwiftUI.Font.system(size: 11.5, weight: .regular).monospaced()
    }

    // MARK: Spacing (8dp base rhythm)

    enum Space {
        static let x6: CGFloat = 6
        static let x8: CGFloat = 8
        static let x10: CGFloat = 10
        static let x12: CGFloat = 12
        static let x16: CGFloat = 16
        static let x26: CGFloat = 26
        static let cardPadding: CGFloat = 18
        static let screenPadding: CGFloat = 16
    }

    // MARK: Shape

    enum Radius {
        static let button: CGFloat = 12
        static let tile: CGFloat = 14
        static let card: CGFloat = 18
        static let sheet: CGFloat = 22
    }

    /// Card elevation is a drop shadow, never a tonal tint (spec §3).
    enum Shadow {
        static let cardRadius: CGFloat = 16
        static let cardY: CGFloat = 6
        static let cardColor = SwiftUI.Color(hex: 0x212121).opacity(0.04)
        static let heroColor = SwiftUI.Color(hex: 0x039BE5).opacity(0.28)
    }
}

extension Color {
    init(hex: UInt32) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255,
            opacity: 1
        )
    }
}

extension View {
    /// The spec's standard card surface: white on paper, hairline border, soft drop shadow.
    func proCard(padding: CGFloat = Pro.Space.cardPadding) -> some View {
        self
            .padding(padding)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Pro.Color.card)
            .clipShape(RoundedRectangle(cornerRadius: Pro.Radius.card, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: Pro.Radius.card, style: .continuous)
                    .stroke(Pro.Color.line, lineWidth: 1)
            )
            .shadow(color: Pro.Shadow.cardColor, radius: Pro.Shadow.cardRadius, y: Pro.Shadow.cardY)
    }
}
