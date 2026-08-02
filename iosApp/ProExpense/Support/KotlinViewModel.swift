import Combine
import SwiftUI
import ProExpenseKit

/// Bridges a Kotlin `StatefulViewModel<S>` into a SwiftUI `ObservableObject`.
///
/// Kotlin/Native cannot expose `Flow` or `suspend` functions to Swift, so collection goes through
/// `FlowBridgeKt.subscribe`, which the shared module publishes for exactly this purpose. `uiState`
/// crosses the boundary type-erased (`Any`), hence the cast — the generic parameter restores it.
///
/// Ownership: the view holds this via `@StateObject`, so `deinit` fires when the view goes away and
/// both the flow subscription and the Kotlin ViewModel's coroutine scope are torn down.
@MainActor
final class KotlinViewModel<State: AnyObject, VM: ProViewModel>: ObservableObject {
    @Published private(set) var state: State

    let viewModel: VM
    private var subscription: FlowSubscription?

    init(viewModel: VM, initialState: State, flow: @escaping (VM) -> Kotlinx_coroutines_coreStateFlow) {
        self.viewModel = viewModel
        self.state = initialState
        self.subscription = FlowBridgeKt.subscribe(flow: flow(viewModel)) { [weak self] value in
            guard let typed = value as? State else { return }
            self?.state = typed
        }
    }

    deinit {
        subscription?.cancel()
        viewModel.onCleared()
    }
}

extension KotlinViewModel where VM == AppShellViewModel, State == AppShellUiState {
    static func appShell() -> KotlinViewModel<AppShellUiState, AppShellViewModel> {
        let vm: AppShellViewModel = KoinHelper.shared.resolveAppShell()
        return KotlinViewModel(
            viewModel: vm,
            initialState: vm.uiState.value as! AppShellUiState,
            flow: { $0.uiState }
        )
    }
}

extension KotlinViewModel where VM == HomeViewModel, State == HomeUiState {
    static func home() -> KotlinViewModel<HomeUiState, HomeViewModel> {
        let vm: HomeViewModel = KoinHelper.shared.resolveHome()
        return KotlinViewModel(
            viewModel: vm,
            initialState: vm.uiState.value as! HomeUiState,
            flow: { $0.uiState }
        )
    }
}
