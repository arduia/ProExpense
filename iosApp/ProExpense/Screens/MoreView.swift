import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/13-more.md`. Settings hub — theme, language, currency, PIN,
/// and the destructive data actions. Every toggle writes through and re-reads via the shared
/// ViewModel, so a switch never shows a state that failed to persist.
struct MoreView: View {
    /// Reported up to `AppShellViewModel` so the launch gate learns about a PIN enabled here —
    /// without it the session would not re-lock on background until the next cold start.
    let onPinConfigured: (Bool) -> Void

    @StateObject private var more = Shell.more()
    @State private var showPinSetup = false
    @State private var confirmClear = false

    private var state: MoreUiState { more.state }

    var body: some View {
        NavigationStack {
            List {
                Section("APPEARANCE") {
                    Picker("Theme", selection: Binding(
                        get: { state.themeMode },
                        set: { mode in Task { await more.viewModel.setThemeMode(mode: mode) } }
                    )) {
                        Text("Light").tag(ThemeMode.light)
                        Text("Dark").tag(ThemeMode.dark)
                        Text("System").tag(ThemeMode.system)
                    }
                    .pickerStyle(.segmented)

                    NavigationLink {
                        LanguagePickerView(more: more)
                    } label: {
                        settingRow("Language", value: state.languageTag.uppercased())
                    }
                }

                Section("MONEY") {
                    NavigationLink {
                        HomeCurrencyPickerView(more: more)
                    } label: {
                        settingRow("Home currency", value: "\(state.currencySymbol) \(state.currencyCode)")
                    }
                    NavigationLink {
                        CategoryListView()
                    } label: {
                        settingRow("Categories", value: "")
                    }
                    NavigationLink {
                        ReportsView()
                    } label: {
                        settingRow("Reports", value: "")
                    }
                }

                Section("PEOPLE") {
                    NavigationLink {
                        DebtTrackerView()
                    } label: {
                        settingRow("Debts", value: "")
                    }
                    NavigationLink {
                        SharedCostsView()
                    } label: {
                        settingRow("Shared costs", value: "")
                    }
                }

                Section("SECURITY") {
                    Button {
                        showPinSetup = true
                    } label: {
                        settingRow("PIN lock", value: state.pinConfigured ? "On" : "Off")
                    }

                    if state.showsSessionLockOptions {
                        Toggle("Stay unlocked while switching apps", isOn: Binding(
                            get: { state.stayUnlockedInBackground },
                            set: { on in Task { await more.viewModel.setStayUnlockedInBackground(enabled: on) } }
                        ))
                        .font(ProFont.body)
                    }
                }

                Section("DATA") {
                    Button(role: .destructive) {
                        confirmClear = true
                    } label: {
                        Text("Clear all data").font(ProFont.body)
                    }
                }

                Section {
                    Text("Pro Expense keeps everything on this device. No accounts, no tracking.")
                        .font(ProFont.caption)
                        .foregroundStyle(ProColor.ink3)
                }
            }
            .navigationTitle("More")
        }
        .sheet(isPresented: $showPinSetup) {
            NavigationStack {
                PinSetupView {
                    onPinConfigured(true)
                    Task { await more.viewModel.reload() }
                }
                    .navigationTitle("PIN lock")
                    .navigationBarTitleDisplayMode(.inline)
            }
        }
        .confirmationDialog(
            "Delete every record, event, debt and split? This cannot be undone.",
            isPresented: $confirmClear,
            titleVisibility: .visible
        ) {
            Button("Clear everything", role: .destructive) {
                Task { _ = await more.viewModel.clearAllData() }
            }
            Button("Cancel", role: .cancel) {}
        }
    }

    private func settingRow(_ label: String, value: String) -> some View {
        HStack {
            Text(label).font(ProFont.body).foregroundStyle(ProColor.ink)
            Spacer()
            Text(value).font(ProFont.body).foregroundStyle(ProColor.ink3)
        }
    }
}

private struct LanguagePickerView: View {
    @ObservedObject var more: MoreVM
    @Environment(\.dismiss) private var dismiss

    private let languages = [("en", "English"), ("th", "ไทย"), ("my", "မြန်မာ")]

    var body: some View {
        List(languages, id: \.0) { tag, name in
            Button {
                Task {
                    await more.viewModel.setLanguage(languageTag: tag)
                    dismiss()
                }
            } label: {
                HStack {
                    Text(name).font(ProFont.body).foregroundStyle(ProColor.ink)
                    Spacer()
                    if tag == more.state.languageTag {
                        Image(systemName: "checkmark").foregroundStyle(ProColor.primary)
                    }
                }
            }
        }
        .navigationTitle("Language")
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct HomeCurrencyPickerView: View {
    @ObservedObject var more: MoreVM
    @Environment(\.dismiss) private var dismiss
    @State private var query = ""

    private var options: [CurrencyInfo] {
        let all = CurrencyCatalog.shared.ALL
        guard !query.isEmpty else { return all }
        return all.filter {
            $0.code.localizedCaseInsensitiveContains(query) || $0.name.localizedCaseInsensitiveContains(query)
        }
    }

    var body: some View {
        List(options, id: \.code) { info in
            Button {
                Task {
                    await more.viewModel.setHomeCurrency(code: info.code)
                    dismiss()
                }
            } label: {
                HStack {
                    Text(info.symbol).font(ProFont.body).foregroundStyle(ProColor.primary).frame(width: 36, alignment: .leading)
                    VStack(alignment: .leading, spacing: 2) {
                        Text(info.code).font(ProFont.body).foregroundStyle(ProColor.ink)
                        Text(info.name).font(ProFont.caption).foregroundStyle(ProColor.ink3)
                    }
                    Spacer()
                    if info.code == more.state.currencyCode {
                        Image(systemName: "checkmark").foregroundStyle(ProColor.primary)
                    }
                }
            }
        }
        .searchable(text: $query, prompt: "Search currency")
        .navigationTitle("Home currency")
        .navigationBarTitleDisplayMode(.inline)
    }
}
