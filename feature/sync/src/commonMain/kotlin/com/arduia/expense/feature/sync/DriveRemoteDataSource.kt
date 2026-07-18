package com.arduia.expense.feature.sync

import com.arduia.expense.data.Result
import com.arduia.expense.data.YearMonth

/** One month's remote Drive file identity/version — used to decide files.create vs files.update on push. */
data class RemoteMonthFileMeta(
    val yearMonth: YearMonth,
    val remoteFileId: String,
    val modifiedAtEpochMillis: Long,
)

data class RemoteMonthFilePage(
    val files: List<RemoteMonthFileMeta>,
    val nextPageToken: String?,
)

/**
 * Drive REST v3 transport for the monthly sync-payload SQLite files (US-SYNC-3/US-SYNC-4), scoped
 * to the private `appDataFolder`. A plain interface (not `HttpClient`-typed) so `commonTest` can
 * fake it with no Ktor test harness, per the Testing Contract's "fakes at repository boundary."
 * Implemented in androidMain (`KtorDriveRemoteDataSource`); not yet called by any use case in
 * Phase 1 — the push/pull logic that will call this lands in Phase 2/3.
 */
interface DriveRemoteDataSource {
    suspend fun listRemoteMonthFiles(pageToken: String?): Result<RemoteMonthFilePage>

    suspend fun uploadMonthFile(
        yearMonth: YearMonth,
        existingRemoteFileId: String?,
        dbFileBytes: ByteArray,
    ): Result<RemoteMonthFileMeta>

    suspend fun downloadMonthFile(remoteFileId: String): Result<ByteArray>
}
