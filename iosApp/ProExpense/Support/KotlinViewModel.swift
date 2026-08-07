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

/// One factory per screen. Each resolves through `KoinHelper.shared` because Koin's reified
/// `get<T>()` cannot be exported to Objective-C, and reads `uiState.value` for the first frame so
/// the view never flashes a placeholder before the flow's first emission arrives.
extension KotlinViewModel {
    static func make<S: AnyObject, V: ProViewModel>(
        _ viewModel: V,
        _ flow: @escaping (V) -> Kotlinx_coroutines_coreStateFlow
    ) -> KotlinViewModel<S, V> {
        KotlinViewModel<S, V>(
            viewModel: viewModel,
            initialState: flow(viewModel).value as! S,
            flow: flow
        )
    }
}

typealias AppShellVM = KotlinViewModel<AppShellUiState, AppShellViewModel>
typealias HomeVM = KotlinViewModel<HomeUiState, HomeViewModel>
typealias JournalVM = KotlinViewModel<JournalUiState, JournalViewModel>
typealias JournalDetailVM = KotlinViewModel<JournalDetailUiState, JournalDetailViewModel>
typealias AddExpenseVM = KotlinViewModel<AddExpenseUiState, AddExpenseViewModel>
typealias OnboardingVM = KotlinViewModel<OnboardingUiState, OnboardingViewModel>
typealias PinEntryVM = KotlinViewModel<PinEntryScreenState, PinEntryViewModel>
typealias PinSetupVM = KotlinViewModel<PinSetupScreenState, PinSetupViewModel>
typealias MoreVM = KotlinViewModel<MoreUiState, MoreViewModel>
typealias CategoriesVM = KotlinViewModel<CategoriesUiState, CategoriesViewModel>
typealias ReportsVM = KotlinViewModel<ReportsUiState, ReportsViewModel>
typealias EventBudgetVM = KotlinViewModel<EventBudgetUiState, EventBudgetViewModel>
typealias DebtVM = KotlinViewModel<DebtUiState, DebtViewModel>
typealias SharedCostVM = KotlinViewModel<SharedCostUiState, SharedCostViewModel>

enum Shell {
    private static var koin: KoinHelper { KoinHelper.shared }

    static func appShell() -> AppShellVM { .make(koin.resolveAppShell(), { $0.uiState }) }
    static func home() -> HomeVM { .make(koin.resolveHome(), { $0.uiState }) }
    static func journal() -> JournalVM { .make(koin.resolveJournal(), { $0.uiState }) }
    static func journalDetail() -> JournalDetailVM { .make(koin.resolveJournalDetail(), { $0.uiState }) }
    static func addExpense() -> AddExpenseVM { .make(koin.resolveAddExpense(), { $0.uiState }) }
    static func onboarding() -> OnboardingVM { .make(koin.resolveOnboarding(), { $0.uiState }) }
    static func pinEntry() -> PinEntryVM { .make(koin.resolvePinEntry(), { $0.uiState }) }
    static func pinSetup() -> PinSetupVM { .make(koin.resolvePinSetup(), { $0.uiState }) }
    static func more() -> MoreVM { .make(koin.resolveMore(), { $0.uiState }) }
    static func categories() -> CategoriesVM { .make(koin.resolveCategories(), { $0.uiState }) }
    static func reports() -> ReportsVM { .make(koin.resolveReports(), { $0.uiState }) }
    static func eventBudget() -> EventBudgetVM { .make(koin.resolveEventBudget(), { $0.uiState }) }
    static func debt() -> DebtVM { .make(koin.resolveDebt(), { $0.uiState }) }
    static func sharedCost() -> SharedCostVM { .make(koin.resolveSharedCost(), { $0.uiState }) }
}
