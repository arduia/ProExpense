package com.arduia.expense.data

import com.arduia.expense.data.exception.RepositoryException
import com.arduia.expense.data.local.AboutUpdateDataModel
import com.arduia.expense.data.network.CheckUpdateDto
import com.arduia.expense.data.network.ExpenseVersionDto
import com.arduia.expense.data.network.FeedbackDto
import com.arduia.expense.model.ErrorResult
import com.arduia.expense.model.FlowResult
import com.arduia.expense.model.Result
import com.arduia.expense.model.SuccessResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ProExpenseServerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val remoteConfig: FirebaseRemoteConfig
) : ProExpenseServerRepository {

    companion object {
        private const val FEEDBACK_COLLECTION = "user_feedbacks_beta"
    }

    init {
        remoteConfig.fetchAndActivate()
    }

    override fun postFeedback(comment: FeedbackDto.Request): FlowResult<FeedbackDto.Response> =
        flow {
            try {
                firestore.collection(FEEDBACK_COLLECTION)
                    .add(comment)
                emit(FeedbackDto.Response(0, "success"))
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
            .map {
                SuccessResult(it) as Result<FeedbackDto.Response>
            }
            .catch { e ->
                emit(ErrorResult(RepositoryException(e)))
            }

    override fun getVersionStatus(): FlowResult<ExpenseVersionDto> = flow {
        val info = remoteConfig.getString("version_info")
        if (!info.isBlank()) {
            emit(Gson().fromJson(info, ExpenseVersionDto::class.java))
        }
    }.map { SuccessResult(it) }
        .catch { e -> ErrorResult(RepositoryException(e)) }

    override suspend fun getAboutUpdateSync(deviceInfo: CheckUpdateDto.Request): Result<CheckUpdateDto.Response> {
        return try {
            val minVersion = remoteConfig.getLong("minimum_version")
            val criticalVersion = remoteConfig.getLong("force_upgrade_version")
            val newVersionModel = remoteConfig.getString("about_new_version_info")

            val versionInfo = Gson().fromJson(newVersionModel, AboutUpdateDataModel::class.java)

            if (deviceInfo.currentVersion <= minVersion) {
                return SuccessResult(
                    CheckUpdateDto.Response(
                        isShouldUpdate = true,
                        isCriticalUpdate = deviceInfo.currentVersion <= criticalVersion,
                        versionInfo
                    )
                )
            } else {
                return SuccessResult(
                    CheckUpdateDto.Response(
                        isShouldUpdate = false,
                        isCriticalUpdate = deviceInfo.currentVersion <= criticalVersion,
                        versionInfo
                    )
                )
            }

        } catch (e: java.lang.Exception) {
            ErrorResult(e)
        }
    }
}