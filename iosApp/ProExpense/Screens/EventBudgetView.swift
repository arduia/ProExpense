import SwiftUI
import ProExpenseKit

/// Tone tiers from US-EVT-3, keyed off the *uncapped* spend ratio so the card and detail agree
/// past 100%.
private func budgetTone(_ spentRatio: Float) -> Color {
    switch spentRatio {
    case ..<1.0: return ProColor.success
    case ..<1.1: return ProColor.highlight
    default: return ProColor.danger
    }
}

/// Spec: `design-system-spec/screens/07-event-budget.md` and `08-event-detail.md`.
struct EventBudgetView: View {
    @StateObject private var events = Shell.eventBudget()
    @State private var showCreate = false

    private var state: EventBudgetUiState { events.state }

    var body: some View {
        NavigationStack {
            Group {
                if state.isLoading {
                    ProgressView()
                } else if state.isEmpty {
                    emptyState
                } else {
                    list
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(ProColor.paper)
            .navigationTitle("Budgets")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { showCreate = true } label: { Image(systemName: "plus") }
                        .accessibilityLabel("New event budget")
                }
            }
        }
        .sheet(isPresented: $showCreate) {
            EventEditorSheet(events: events, isPresented: $showCreate)
                .presentationDetents([.medium])
                .presentationCornerRadius(ProRadius.sheet)
        }
    }

    private var list: some View {
        ScrollView {
            LazyVStack(spacing: ProSpacing.md) {
                if state.activeCount > 0 {
                    HStack {
                        Text("\(state.activeCount) ACTIVE")
                            .font(ProFont.eyebrow).kerning(1.1).foregroundStyle(ProColor.primary)
                        Spacer()
                    }
                    .padding(.horizontal, ProSpacing.screenHorizontal)
                }

                ForEach(state.cards, id: \.eventId) { card in
                    NavigationLink {
                        EventDetailView(events: events, eventId: card.eventId)
                    } label: {
                        EventCardView(card: card)
                    }
                    .buttonStyle(.plain)
                    .padding(.horizontal, ProSpacing.screenHorizontal)
                }
            }
            .padding(.vertical, ProSpacing.lg)
            .padding(.bottom, 100)
        }
    }

    private var emptyState: some View {
        VStack(spacing: ProSpacing.sm) {
            Image(systemName: "calendar").font(.system(size: 40)).foregroundStyle(ProColor.muted)
            Text("No event budgets yet").font(ProFont.title).foregroundStyle(ProColor.ink)
            Text("Set a budget for a trip or occasion and track what's left.")
                .font(ProFont.caption).foregroundStyle(ProColor.ink3).multilineTextAlignment(.center)
        }
        .padding(.horizontal, ProSpacing.xxl)
    }
}

struct EventCardView: View {
    let card: EventCard

    var body: some View {
        VStack(alignment: .leading, spacing: ProSpacing.md) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(card.title).font(ProFont.title).foregroundStyle(ProColor.ink)
                    Text(card.dateRange).font(ProFont.caption).foregroundStyle(ProColor.ink3)
                }
                Spacer()
                if card.isClosed {
                    Text("CLOSED")
                        .font(ProFont.eyebrow)
                        .foregroundStyle(ProColor.muted)
                        .padding(.horizontal, ProSpacing.sm)
                        .padding(.vertical, 2)
                        .background(Capsule().fill(ProColor.muted.opacity(0.15)))
                }
            }

            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    Capsule().fill(ProColor.primaryTint)
                    Capsule()
                        .fill(budgetTone(card.spentRatio))
                        .frame(width: geo.size.width * CGFloat(card.progress))
                }
            }
            .frame(height: 8)

            HStack {
                Text("\(card.spentLabel) of \(card.budgetLabel)")
                    .font(ProFont.caption).foregroundStyle(ProColor.ink3)
                Spacer()
                Text(card.isOverBudget ? "\(card.overBudgetPercent)% over" : "\(card.remainingLabel) left")
                    .font(ProFont.caption)
                    .foregroundStyle(card.isOverBudget ? ProColor.danger : ProColor.success)
            }
        }
        .padding(ProSpacing.lg)
        .proCard()
    }
}

/// 08 · Event Detail — the card plus its linked expenses and lifecycle actions.
struct EventDetailView: View {
    @ObservedObject var events: EventBudgetVM
    let eventId: String

    private var state: EventBudgetUiState { events.state }

    var body: some View {
        ScrollView {
            VStack(spacing: ProSpacing.lg) {
                if let card = state.selectedCard {
                    EventCardView(card: card)

                    if !card.isReadOnly {
                        actions(card)
                    } else {
                        Text("This event is closed and can no longer be edited.")
                            .font(ProFont.caption).foregroundStyle(ProColor.ink3)
                    }
                }

                linkedSection
            }
            .padding(.horizontal, ProSpacing.screenHorizontal)
            .padding(.vertical, ProSpacing.lg)
        }
        .background(ProColor.paper)
        .navigationTitle(state.selectedCard?.title ?? "Event")
        .navigationBarTitleDisplayMode(.inline)
        .task { events.viewModel.onEventSelected(eventId: eventId) }
        .onDisappear { events.viewModel.onEventSelected(eventId: nil) }
    }

    private func actions(_ card: EventCard) -> some View {
        HStack(spacing: ProSpacing.md) {
            if !card.isClosed {
                Button("Close event") { Task { _ = await events.viewModel.close(eventId: eventId) } }
                    .font(ProFont.label).foregroundStyle(ProColor.primary)
            }
            Button("Archive") { Task { _ = await events.viewModel.archive(eventId: eventId) } }
                .font(ProFont.label).foregroundStyle(ProColor.ink2)
            Spacer()
            Button(role: .destructive) {
                Task { await events.viewModel.delete(eventId: eventId) }
            } label: {
                Text("Delete").font(ProFont.label)
            }
        }
        .padding(ProSpacing.lg)
        .proCard()
    }

    private var linkedSection: some View {
        VStack(alignment: .leading, spacing: ProSpacing.md) {
            Text("LINKED EXPENSES")
                .font(ProFont.eyebrow).kerning(1.1).foregroundStyle(ProColor.ink3)

            if state.linkedRows.isEmpty {
                Text("Nothing linked to this event yet.")
                    .font(ProFont.caption).foregroundStyle(ProColor.muted)
            } else {
                VStack(spacing: 0) {
                    ForEach(state.linkedRows, id: \.id) { row in
                        TransactionRowView(row: row)
                        if row.id != state.linkedRows.last?.id {
                            Divider().background(ProColor.lineSoft).padding(.leading, 60)
                        }
                    }
                }
                .proCard()
            }
        }
    }
}

private struct EventEditorSheet: View {
    @ObservedObject var events: EventBudgetVM
    @Binding var isPresented: Bool

    @State private var name = ""
    @State private var rawBudget = ""
    @State private var start = Date()
    @State private var end = Date()
    @FocusState private var nameFocused: Bool

    var body: some View {
        NavigationStack {
            Form {
                TextField("Event name", text: $name).focused($nameFocused)
                TextField("Budget", text: $rawBudget).keyboardType(.decimalPad)
                DatePicker("Starts", selection: $start, displayedComponents: .date)
                DatePicker("Ends", selection: $end, in: start..., displayedComponents: .date)
            }
            .navigationTitle("New budget")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { isPresented = false }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Create") {
                        Task {
                            let created = await events.viewModel.create(
                                name: name,
                                rawBudget: rawBudget,
                                startEpochMillis: KotlinLong(value: Int64(start.timeIntervalSince1970 * 1000)),
                                endEpochMillis: KotlinLong(value: Int64(end.timeIntervalSince1970 * 1000))
                            )
                            if created != nil { isPresented = false }
                        }
                    }
                    .disabled(name.isEmpty || rawBudget.isEmpty)
                }
            }
            .onAppear { nameFocused = true }
        }
    }
}
