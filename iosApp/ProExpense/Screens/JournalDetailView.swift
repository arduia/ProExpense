import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/06-journal-detail.md`. One record, with inline note editing
/// and delete. The row is rebuilt by the shared ViewModel after each write, so labels stay correct.
struct JournalDetailView: View {
    @StateObject private var detail = Shell.journalDetail()
    @Environment(\.dismiss) private var dismiss

    let recordId: String
    @State private var isEditingNote = false
    @State private var confirmDelete = false

    private var state: JournalDetailUiState { detail.state }

    var body: some View {
        Group {
            if state.isLoading {
                ProgressView()
            } else if state.notFound {
                Text("This record no longer exists.")
                    .font(ProFont.body)
                    .foregroundStyle(ProColor.ink3)
            } else if let row = state.row {
                content(row)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(ProColor.paper)
        .navigationTitle("Record")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(role: .destructive) { confirmDelete = true } label: {
                    Image(systemName: "trash")
                }
                .accessibilityLabel("Delete record")
            }
        }
        .task { detail.viewModel.load(recordId: recordId) }
        .onChange(of: state.deleted) { deleted in
            if deleted { dismiss() }
        }
        .confirmationDialog("Delete this record?", isPresented: $confirmDelete, titleVisibility: .visible) {
            Button("Delete", role: .destructive) {
                Task { await detail.viewModel.delete() }
            }
            Button("Cancel", role: .cancel) {}
        }
    }

    private func content(_ row: ProTransactionRowModel) -> some View {
        VStack(spacing: ProSpacing.lg) {
            VStack(spacing: ProSpacing.sm) {
                Text(row.amount)
                    .font(.system(size: 40))
                    .kerning(-0.8)
                    .foregroundStyle(row.isIncome ? ProColor.success : ProColor.ink)
                Text(row.detailDateTimeLabel ?? row.meta)
                    .font(ProFont.caption)
                    .foregroundStyle(ProColor.ink3)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, ProSpacing.xxl)
            .proCard()

            detailRow(label: "Category", value: row.categoryId)
            if let tag = row.tag {
                detailRow(label: "Tag", value: tag)
            }

            noteCard

            Spacer()
        }
        .padding(.horizontal, ProSpacing.screenHorizontal)
        .padding(.top, ProSpacing.lg)
    }

    private var noteCard: some View {
        VStack(alignment: .leading, spacing: ProSpacing.sm) {
            HStack {
                Text("NOTE").font(ProFont.eyebrow).kerning(1.1).foregroundStyle(ProColor.ink3)
                Spacer()
                Button(isEditingNote ? "Save" : "Edit") {
                    if isEditingNote {
                        Task { await detail.viewModel.saveNote() }
                    }
                    isEditingNote.toggle()
                }
                .font(ProFont.label)
                .foregroundStyle(ProColor.primary)
            }

            if isEditingNote {
                TextField("Add a note", text: Binding(
                    get: { state.noteDraft },
                    set: { detail.viewModel.onNoteDraftChange(note: $0) }
                ), axis: .vertical)
                .font(ProFont.body)
                .lineLimit(3...6)
            } else {
                Text(state.noteDraft.isEmpty ? "No note" : state.noteDraft)
                    .font(ProFont.body)
                    .foregroundStyle(state.noteDraft.isEmpty ? ProColor.muted : ProColor.ink)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(ProSpacing.lg)
        .proCard()
    }

    private func detailRow(label: String, value: String) -> some View {
        HStack {
            Text(label).font(ProFont.label).foregroundStyle(ProColor.ink3)
            Spacer()
            Text(value).font(ProFont.body).foregroundStyle(ProColor.ink)
        }
        .padding(ProSpacing.lg)
        .proCard()
    }
}
