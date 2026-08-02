import SwiftUI
import ProExpenseKit

@main
struct ProExpenseApp: App {
    init() {
        // Opens the encrypted database and builds the same repository graph the Android shell uses.
        // Must run before any view resolves a ViewModel.
        KoinIosKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
