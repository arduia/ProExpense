# Research — Cloud Sync with Google Drive (via App Settings)

> Status: **research only — no implementation yet** · Branch base: `refactor/v2-migration`
> Date: 2026-07-17

## 1. Product-scope position

Cloud sync is explicitly **out of MVP scope** and lands in the roadmap later:

- `AGENTS.md` product constraints: "No cloud sync or online backup in MVP".
- `docs/project_philosophy.md`: MVP is fully local; "no sync layer in MVP".
- `docs/finance_tracker_product.md`:
  - Phase 3 (Month 7–12): "Cloud Sync (optional)".
  - Future-compatible design notes: "Local storage → ready for optional cloud sync layer";
    "Export/Import → foundation for migration, backup and cross-device support".
  - Backend/Cloud table: "Post-MVP: TBD — optional cloud sync layer".

**Consequence:** before implementation, the product docs and user stories must be updated first
(Step 4.5 — requirements must live in `docs/user_stories/`, not chat). A new service folder
`docs/user_stories/cloudsync/` with `US-CS-*.md` stories is the entry ticket, plus a scoped
amendment to the "no cloud" constraint (opt-in, privacy-preserving backup).

## 2. Where it plugs into the current codebase

### Settings surface (More tab)

- `app/src/main/java/com/arduia/expense/ui/more/MoreFlow.kt` — private `MoreStep` enum drives an
  `AnimatedContent` step machine (Hub → Currency/Export/Import/Clear/…). A cloud-sync screen is a
  new `MoreStep` + a new row id handled in `onSettingClick`.
- Hub rows come from `previewMoreHub` (`app/.../ui/preview/MorePreviewData.kt`) as
  `MoreSettingRowUi(id, icon, label, kind, value/toggle)`. Rows support `Nav` and `Toggle` kinds;
  conditional insertion is already done (biometric rows appear only when PIN is on — same pattern
  works for "Back up now"/"Disconnect" appearing only when an account is connected).
- Feature UIs reach the app shell via `FeatureUiRegistry`
  (`app/.../ui/FeatureUiRegistry.kt`) — each feature module exposes an `entry/<X>FeatureEntry`
  interface + `<X>FeatureUi` object. A `feature:cloudsync` module would follow the same pattern.

### Existing machinery to reuse (backup artifact)

- `ExportGroupedDataUseCase` (`feature/importexport/commonMain`) → per-record-type CSVs.
- `ExportFileWriter` (`feature/importexport/androidMain`) → zips CSVs with **optional AES
  encryption** (zip4j) into `cacheDir/exports`. This encrypted zip is the natural Drive backup
  artifact.
- Restore path exists: `ImportZipReader` + `PreviewImportUseCase` / `ImportDataUseCase`
  (US-IE-2), including encrypted-zip password prompt.
- Storage: SQLDelight + SQLCipher behind repository contracts; simple prefs via
  `PlatformKeyValueStore` (`core/storage`) — fine for storing sync settings (account e-mail,
  last-backup timestamp, auto-backup flag).
- DI: Koin modules per feature (`feature/importexport/.../di/ImportExportModule.kt` pattern).

### What does NOT exist yet

- **No networking at all**: `AndroidManifest.xml` has no `INTERNET` permission; the version
  catalog has no HTTP client (no Ktor/OkHttp/Retrofit), no Google Play Services, no
  kotlinx-serialization, no WorkManager. Any Drive integration introduces the app's first
  network dependency — a deliberate philosophy decision, so it must be strictly opt-in.
- v1 (`main` branch) had only a **local** backup system (Room `BackupEnt`, WorkManager
  `ExportWorker`/`ImportWorker`); no Drive code exists anywhere to port.

## 3. Google Drive integration options (Android, 2026)

| Concern | Current-generation choice | Notes |
|---|---|---|
| Sign-in | **Credential Manager** (`androidx.credentials` + `googleid`) | Legacy `GoogleSignIn` API is deprecated |
| Drive scope grant | **`AuthorizationClient`** (`play-services-auth`) | Separates auth-n from auth-z; returns OAuth access token for Drive |
| Drive access | **Drive REST API v3** | The old Drive Android SDK was shut down in 2019 — REST is the only path |
| REST client | Plain HTTP via **Ktor** (KMP-friendly) or `google-api-services-drive` (Java, heavy) | Ktor keeps upload/download logic in `commonMain`; only token acquisition is platform `actual` |
| Scope | `drive.appdata` (hidden app folder, classic WhatsApp-style backup) **or** `drive.file` (only files the app creates, non-sensitive scope, user-visible folder) | `drive.file` avoids Google's sensitive-scope verification; `drive.appdata` hides backups from the user's Drive UI |
| Console setup | Google Cloud project, OAuth consent screen, Android OAuth client ID (package + SHA-1 for dev/prod flavors) | Needed per applicationId: `com.arduia.expense.dev` and prod |
| iOS parity | GoogleSignIn iOS SDK + same REST calls | Fits the mandated `expect`/`actual` seam; iOS actuals may stay `TODO()` stubs per `docs/ios_compatibility_plan.md` |

## 4. Recommended shape (v1 = backup/restore, not true sync)

True bidirectional sync needs change tracking, tombstones, and conflict resolution across
devices — a large lift. The roadmap's "foundation" notes point at the pragmatic first step:
**encrypted backup to Drive + restore**, which reuses US-IE-1/US-IE-2 machinery almost entirely.

- New KMP module **`feature:cloudsync`** (iOS targets mandatory — `checkIosTargets` gate):
  - `commonMain`: `CloudBackupRepository` contract, models (`CloudAccount`,
    `BackupSnapshot(timestamp, sizeBytes, fileId)`), use cases (`ConnectAccount`,
    `BackupNowUseCase`, `RestoreBackupUseCase`, `DisconnectUseCase`), Ktor Drive client keyed
    off an `expect` token provider.
  - `androidMain`: Credential Manager + AuthorizationClient actuals, feature entry + Compose
    screens; `iosMain`: stubs.
- **Settings UI**: new "Cloud backup" row in the More hub → screen with: connect Google
  account, connected-account row, last-backup time, "Back up now", "Restore from backup",
  optional auto-backup toggle (would need WorkManager dep), disconnect.
- **Privacy defaults**: feature fully opt-in; artifact encrypted (reuse zip4j AES with a
  user passphrase) *before* upload; `drive.appdata` or `drive.file` only — never full Drive
  scope; no other network calls.
- **New deps to add**: `androidx.credentials`, `googleid`, `play-services-auth`
  (authorization), Ktor client (+ kotlinx-serialization), optionally WorkManager. Manifest
  gains `INTERNET` permission.

## 5. Open decisions (need product-owner input before implementation)

1. **Backup/restore v1 vs. full multi-device sync** — recommendation: backup/restore first.
2. **Scope choice** — `drive.appdata` (hidden, needs sensitive-scope verification) vs
   `drive.file` (visible "ProExpense" folder, lighter verification). Recommendation: `drive.file`.
3. **Encryption policy** — mandatory passphrase vs optional (align with encrypted-export UX).
4. **Auto-backup scheduling** (WorkManager) in v1 or manual-only first.
5. **Doc updates** — amend PRD/philosophy "no cloud" wording to "opt-in encrypted backup,
   post-MVP" and add `US-CS-*` stories before any code (Step 4.5 gate).
