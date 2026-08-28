import Combine
import SwiftUI
import ProExpenseKit

/// Bridges a Kotlin `StatefulViewModel<S>` into a SwiftUI `ObservableObject`.
///
/// Kotlin/Native cannot expose `Flow` or `suspend` functions to Swift, so state arrives through
/// `FlowBridgeKt.observeState`, which the shared module publishes for exactly this purpose. Generics
/// erase to `Any` across the Objective-C boundary, hence the casts — the type parameters restore
/// them, and every type named here is one `:appshell` owns rather than a kotlinx-coroutines class
/// whose exported name is generated from its module.
///
/// Ownership: the view holds this via `@StateObject`, so `deinit` fires when the view goes away and
/// both the flow subscription and the Kotlin ViewModel's coroutine scope are torn down.
@MainActor
final class KotlinViewModel<State: AnyObject, VM: ProViewModel>: ObservableObject {
    @Published private(set) var state: State

    let viewModel: VM
    private var subscription: FlowSubscription?

    init(_ viewModel: VM) {
        self.viewModel = viewModel
        // Rendered on the first frame, before the flow's first emission lands.
        self.state = FlowBridgeKt.currentState(viewModel: viewModel) as! State
        self.subscription = FlowBridgeKt.observeState(viewModel: viewModel) { [weak self] value in
            guard let typed = value as? State else { return }
            self?.state = typed
        }
    }

    deinit {
        subscription?.cancel()
        viewModel.onCleared()
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

/// One factory per screen. Each resolves through `KoinHelper.shared` because Koin's reified
/// `get<T>()` cannot be exported to Objective-C.
enum Shell {
    private static var koin: KoinHelper { KoinHelper.shared }

    static func appShell() -> AppShellVM { .init(koin.resolveAppShell()) }
    static func home() -> HomeVM { .init(koin.resolveHome()) }
    static func journal() -> JournalVM { .init(koin.resolveJournal()) }
    static func journalDetail() -> JournalDetailVM { .init(koin.resolveJournalDetail()) }
    static func addExpense() -> AddExpenseVM { .init(koin.resolveAddExpense()) }
    static func onboarding() -> OnboardingVM { .init(koin.resolveOnboarding()) }
    static func pinEntry() -> PinEntryVM { .init(koin.resolvePinEntry()) }
    static func pinSetup() -> PinSetupVM { .init(koin.resolvePinSetup()) }
    static func more() -> MoreVM { .init(koin.resolveMore()) }
    static func categories() -> CategoriesVM { .init(koin.resolveCategories()) }
    static func reports() -> ReportsVM { .init(koin.resolveReports()) }
    static func eventBudget() -> EventBudgetVM { .init(koin.resolveEventBudget()) }
    static func debt() -> DebtVM { .init(koin.resolveDebt()) }
    static func sharedCost() -> SharedCostVM { .init(koin.resolveSharedCost()) }
}
