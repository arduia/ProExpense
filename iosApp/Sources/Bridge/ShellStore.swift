import Foundation
import ProExpenseKit

/// Bridges the KMP `StateFlow<ShellUiState>` into SwiftUI's observation system.
///
/// Kotlin `Flow` has no Objective-C representation SwiftUI can bind to, so `:shell`'s `iosMain`
/// exposes `StateWatcher` — a closure subscription that delivers on the main queue and hands back
/// a `Cancellable`. This class owns exactly one such subscription for the app's lifetime.
@MainActor
final class ShellStore: ObservableObject {
    @Published private(set) var state: ShellUiState

    private let viewModel: HomeViewModel
    private let watcher: ShellStateWatcher
    private var subscription: Cancellable?

    init() {
        let viewModel = ProExpenseShell.shared.homeViewModel()
        self.viewModel = viewModel
        let watcher = FlowBridgeKt.stateWatcher(viewModel)
        self.watcher = watcher
        self.state = watcher.value

        // Callbacks already arrive on Dispatchers.Main; hop through MainActor for Swift's sake.
        subscription = watcher.watch { [weak self] next in
            Task { @MainActor in self?.state = next }
        }
    }

    deinit {
        subscription?.cancel()
    }

    var home: HomeUiState { state.home }

    func setUserName(_ name: String) {
        viewModel.onUserNameChanged(name: name)
    }

    func setHomeCurrency(_ code: String) {
        viewModel.onHomeCurrencyChanged(code: code)
    }
}
