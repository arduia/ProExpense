import SwiftUI

/// Blue Banking tokens, transcribed from `design-system-spec/tokens.md` (1 CSS px = 1 dp = 1 pt).
///
/// Values are duplicated here rather than read from the Kotlin framework because they are pure
/// presentation constants — the Compose side reads the same table from `ProExpenseTheme`. When
/// `tokens.md` changes, both sides update together.
enum ProColor {
    static func dynamic(light: UInt32, dark: UInt32) -> Color {
        Color(UIColor { traits in
            traits.userInterfaceStyle == .dark ? UIColor(hex: dark) : UIColor(hex: light)
        })
    }

    static let navy = dynamic(light: 0x01579B, dark: 0x081726)
    static let primaryDeep = dynamic(light: 0x0288D1, dark: 0x0D4368)
    static let primary = dynamic(light: 0x039BE5, dark: 0x2BA9E8)
    static let primarySoft = dynamic(light: 0x4FC3F7, dark: 0x4FC3F7)
    static let highlight = dynamic(light: 0xF2B33D, dark: 0xF2B33D)
    static let success = dynamic(light: 0x4CAF50, dark: 0x5CC86A)
    static let tag = dynamic(light: 0xFB8C00, dark: 0xFF9A3D)
    static let danger = dynamic(light: 0xEF5350, dark: 0xFF6F6B)
    static let paper = dynamic(light: 0xF5F5F5, dark: 0x0D1622)
    static let card = dynamic(light: 0xFFFFFF, dark: 0x18232F)
    static let ink = dynamic(light: 0x212121, dark: 0xF2F6FB)
    static let ink2 = dynamic(light: 0x424242, dark: 0xC2CEDB)
    static let ink3 = dynamic(light: 0x757575, dark: 0x8A97A7)
    static let muted = dynamic(light: 0x9E9E9E, dark: 0x69768A)

    /// Alpha-driven tints: the dark palette expresses these as washes over the surface, so a plain
    /// hex pair would not reproduce them.
    static let primaryTint = dynamic(light: 0xE1F5FE, dark: 0x2BA9E8).opacity(0.15)
    static let line = Color.primary.opacity(0.10)
    static let lineSoft = Color.primary.opacity(0.06)
}

enum ProSpacing {
    static let xs: CGFloat = 4
    static let sm: CGFloat = 8
    static let md: CGFloat = 12
    static let lg: CGFloat = 16
    static let xl: CGFloat = 20
    static let xxl: CGFloat = 24
    static let screenHorizontal: CGFloat = 20
}

enum ProRadius {
    static let sm: CGFloat = 8
    static let md: CGFloat = 12
    static let lg: CGFloat = 16
    static let card: CGFloat = 20
    static let sheet: CGFloat = 28
}

enum ProFont {
    static let eyebrow = Font.system(size: 12, weight: .semibold)
    static let caption = Font.system(size: 12, weight: .regular)
    static let label = Font.system(size: 13, weight: .medium)
    static let body = Font.system(size: 15, weight: .regular)
    static let title = Font.system(size: 17, weight: .semibold)
    static let heroAmount = Font.system(size: 40, weight: .bold)
    static let heroSymbol = Font.system(size: 22, weight: .semibold)
}

/// The navy → primary gradient every Blue Banking header uses.
extension LinearGradient {
    static var proHeader: LinearGradient {
        LinearGradient(
            colors: [ProColor.navy, ProColor.primary],
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
    }
}

extension UIColor {
    convenience init(hex: UInt32) {
        self.init(
            red: CGFloat((hex >> 16) & 0xFF) / 255,
            green: CGFloat((hex >> 8) & 0xFF) / 255,
            blue: CGFloat(hex & 0xFF) / 255,
            alpha: 1
        )
    }
}

/// Card surface shared by Home's spend card and the journal rows — drop shadow only, no tonal
/// elevation (see the theme note in `tokens.md`).
struct ProCard: ViewModifier {
    var radius: CGFloat = ProRadius.card

    func body(content: Content) -> some View {
        content
            .background(ProColor.card)
            .clipShape(RoundedRectangle(cornerRadius: radius, style: .continuous))
            .shadow(color: .black.opacity(0.08), radius: 12, x: 0, y: 4)
    }
}

extension View {
    func proCard(radius: CGFloat = ProRadius.card) -> some View {
        modifier(ProCard(radius: radius))
    }
}
