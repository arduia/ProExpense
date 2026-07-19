package com.arduia.expense.feature.sync

import android.util.Log
import com.arduia.expense.data.Result
import com.arduia.expense.data.YearMonth
import com.arduia.expense.shared.currentEpochMillis
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.datetime.Instant

/**
 * Drive REST v3 transport (US-SYNC-3/US-SYNC-4), scoped to the private `appDataFolder`. **Not yet
 * called by any use case in Phase 1** — this establishes the network client foundation Phase 2/3's
 * push/pull logic will consume directly, per the approved implementation plan.
 *
 * JSON handling is a small hand-rolled regex extractor (mirrors `core:storage`'s
 * `JsonStringCodec.kt` precedent for the same reason: a handful of flat, known-shape fields don't
 * justify a new `kotlinx-serialization` dependency — see plan decision 2, Ktor is transport only).
 */
class KtorDriveRemoteDataSource(
    private val tokenStore: EncryptedSyncTokenStore,
    private val client: HttpClient = HttpClient(OkHttp),
) : DriveRemoteDataSource {
    override suspend fun listRemoteMonthFiles(pageToken: String?): Result<RemoteMonthFilePage> =
        runCatchingResult(TAG, "listRemoteMonthFiles") {
            Log.d(TAG, "listRemoteMonthFiles: GET $DRIVE_FILES_BASE (pageToken=${pageToken != null})")
            val json =
                client
                    .get(DRIVE_FILES_BASE) {
                        bearerAuth()
                        parameter("spaces", "appDataFolder")
                        parameter("fields", "nextPageToken,files(id,name,modifiedTime)")
                        parameter("pageSize", "100")
                        pageToken?.let { parameter("pageToken", it) }
                    }.bodyAsBytes()
                    .decodeToString()
            val files =
                Regex("""\{[^{}]*"id"\s*:\s*"[^"]*"[^{}]*\}""")
                    .findAll(json)
                    .mapNotNull { match ->
                        val fileJson = match.value
                        val id = extractJsonString(fileJson, "id") ?: return@mapNotNull null
                        val name = extractJsonString(fileJson, "name") ?: return@mapNotNull null
                        val yearMonth = yearMonthFromFileName(name) ?: return@mapNotNull null
                        val modifiedTime = extractJsonString(fileJson, "modifiedTime")
                        RemoteMonthFileMeta(
                            yearMonth = yearMonth,
                            remoteFileId = id,
                            modifiedAtEpochMillis = parseRfc3339ToEpochMillis(modifiedTime),
                        )
                    }.toList()
            val page = RemoteMonthFilePage(files = files, nextPageToken = extractJsonString(json, "nextPageToken"))
            Log.d(
                TAG,
                "listRemoteMonthFiles: got ${page.files.size} file(s), nextPageToken=${page.nextPageToken != null}",
            )
            page
        }

    override suspend fun uploadMonthFile(
        yearMonth: YearMonth,
        existingRemoteFileId: String?,
        dbFileBytes: ByteArray,
    ): Result<RemoteMonthFileMeta> =
        runCatchingResult(TAG, "uploadMonthFile(${yearMonth.value})") {
            val fileName = fileNameFor(yearMonth)
            val json =
                if (existingRemoteFileId == null) {
                    Log.d(TAG, "uploadMonthFile: creating new file $fileName (${dbFileBytes.size} bytes)")
                    client
                        .post("$DRIVE_UPLOAD_BASE") {
                            bearerAuth()
                            parameter("uploadType", "multipart")
                            setBody(
                                MultiPartFormDataContent(
                                    formData {
                                        append(
                                            "metadata",
                                            """{"name":"$fileName","parents":["appDataFolder"]}""",
                                            Headers.build {
                                                append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
                                            },
                                        )
                                        append(
                                            "file",
                                            dbFileBytes,
                                            Headers.build {
                                                append(HttpHeaders.ContentType, "application/octet-stream")
                                            },
                                        )
                                    },
                                ),
                            )
                        }.bodyAsBytes()
                        .decodeToString()
                } else {
                    Log.d(
                        TAG,
                        "uploadMonthFile: updating file $existingRemoteFileId " +
                            "($fileName, ${dbFileBytes.size} bytes)",
                    )
                    client
                        .patch("$DRIVE_UPLOAD_BASE/$existingRemoteFileId") {
                            bearerAuth()
                            parameter("uploadType", "media")
                            contentType(ContentType.Application.OctetStream)
                            setBody(dbFileBytes)
                        }.bodyAsBytes()
                        .decodeToString()
                }
            val id = extractJsonString(json, "id") ?: error("Drive did not return a file id")
            Log.i(TAG, "uploadMonthFile: ${yearMonth.value} -> remoteFileId=$id")
            RemoteMonthFileMeta(yearMonth, id, currentEpochMillis())
        }

    override suspend fun downloadMonthFile(remoteFileId: String): Result<ByteArray> =
        runCatchingResult(TAG, "downloadMonthFile($remoteFileId)") {
            Log.d(TAG, "downloadMonthFile: GET $DRIVE_FILES_BASE/$remoteFileId?alt=media")
            val bytes =
                client
                    .get("$DRIVE_FILES_BASE/$remoteFileId") {
                        bearerAuth()
                        parameter("alt", "media")
                    }.bodyAsBytes()
            Log.d(TAG, "downloadMonthFile: received ${bytes.size} bytes for $remoteFileId")
            bytes
        }

    private fun HttpRequestBuilder.bearerAuth() {
        val token =
            tokenStore.readAccessToken() ?: run {
                Log.e(TAG, "bearerAuth: no stored access token — not connected")
                error("Not connected — no Drive access token")
            }
        header(HttpHeaders.Authorization, "Bearer $token")
    }

    private companion object {
        const val TAG = "KtorDriveRemoteDataSource"
        const val DRIVE_FILES_BASE = "https://www.googleapis.com/drive/v3/files"
        const val DRIVE_UPLOAD_BASE = "https://www.googleapis.com/upload/drive/v3/files"

        fun fileNameFor(yearMonth: YearMonth) = "records-${yearMonth.value}.sqlite"

        fun yearMonthFromFileName(name: String): YearMonth? {
            val match = Regex("""records-(\d{4}-\d{2})\.sqlite""").find(name) ?: return null
            return YearMonth(match.groupValues[1])
        }

        fun extractJsonString(
            json: String,
            key: String,
        ): String? = Regex("\"$key\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"").find(json)?.groupValues?.get(1)

        fun parseRfc3339ToEpochMillis(value: String?): Long =
            if (value == null) {
                0L
            } else {
                runCatching { Instant.parse(value).toEpochMilliseconds() }.getOrDefault(0L)
            }
    }
}

private inline fun <T> runCatchingResult(
    tag: String,
    operation: String,
    block: () -> T,
): Result<T> =
    try {
        Result.Success(block())
    } catch (e: Exception) {
        Log.e(tag, "$operation: failed", e)
        Result.Error(e.message ?: "Drive request failed", e)
    }
