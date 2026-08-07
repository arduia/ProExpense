import SwiftUI
import ProExpenseKit

/// Spec: `design-system-spec/screens/11-category-list.md`. Manage the category catalog.
/// Deleting a custom category reassigns its records to Uncategorized — enforced by the shared
/// use case, not here.
struct CategoryListView: View {
    @StateObject private var categories = Shell.categories()

    private var state: CategoriesUiState { categories.state }

    var body: some View {
        List {
            if state.isLoading {
                ProgressView()
            } else {
                ForEach(state.rows, id: \.id) { row in
                    Button {
                        categories.viewModel.openEdit(categoryId: row.id)
                    } label: {
                        HStack(spacing: ProSpacing.md) {
                            Circle()
                                .fill(row.isIncome ? ProColor.success.opacity(0.15) : ProColor.primaryTint)
                                .frame(width: 32, height: 32)
                                .overlay(
                                    Image(systemName: row.isIncome ? "arrow.down.left" : "arrow.up.right")
                                        .font(.system(size: 13, weight: .semibold))
                                        .foregroundStyle(row.isIncome ? ProColor.success : ProColor.primary)
                                )
                            Text(row.name).font(ProFont.body).foregroundStyle(ProColor.ink)
                            Spacer()
                            if !row.isCustom {
                                Text("DEFAULT")
                                    .font(ProFont.eyebrow)
                                    .foregroundStyle(ProColor.muted)
                            }
                        }
                    }
                }
                .onMove { indices, newOffset in
                    var ids = state.rows.map(\.id)
                    ids.move(fromOffsets: indices, toOffset: newOffset)
                    Task { await categories.viewModel.reorder(orderedIds: ids) }
                }
            }
        }
        .navigationTitle("Categories")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button { categories.viewModel.openCreate() } label: {
                    Image(systemName: "plus")
                }
                .accessibilityLabel("Add category")
            }
            ToolbarItem(placement: .topBarLeading) { EditButton() }
        }
        .sheet(isPresented: Binding(
            get: { state.isEditorOpen },
            set: { if !$0 { categories.viewModel.closeEditor() } }
        )) {
            CategoryEditorSheet(categories: categories)
                .presentationDetents([.medium])
                .presentationCornerRadius(ProRadius.sheet)
        }
    }
}

private struct CategoryEditorSheet: View {
    @ObservedObject var categories: CategoriesVM
    @FocusState private var nameFocused: Bool

    private var state: CategoriesUiState { categories.state }

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: ProSpacing.lg) {
                TextField("Category name", text: Binding(
                    get: { state.draftName },
                    set: { categories.viewModel.onNameChange(name: $0) }
                ))
                .font(ProFont.body)
                .focused($nameFocused)
                .padding(ProSpacing.md)
                .background(ProColor.card, in: RoundedRectangle(cornerRadius: ProRadius.md, style: .continuous))

                Picker("Type", selection: Binding(
                    get: { state.draftIsIncome },
                    set: { categories.viewModel.onTypeChange(isIncome: $0) }
                )) {
                    Text("Expense").tag(false)
                    Text("Income").tag(true)
                }
                .pickerStyle(.segmented)

                if state.canDeleteEditing, let editingId = state.editingId {
                    Button(role: .destructive) {
                        Task { await categories.viewModel.delete(categoryId: editingId) }
                    } label: {
                        Text("Delete category").font(ProFont.body)
                    }
                }

                Spacer()
            }
            .padding(ProSpacing.screenHorizontal)
            .background(ProColor.paper)
            .navigationTitle(state.editingId == nil ? "New category" : "Edit category")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { categories.viewModel.closeEditor() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") { Task { await categories.viewModel.save() } }
                        .disabled(!state.canSave)
                }
            }
            // The sheet exists to type a name — focus it immediately.
            .onAppear { nameFocused = true }
        }
    }
}
