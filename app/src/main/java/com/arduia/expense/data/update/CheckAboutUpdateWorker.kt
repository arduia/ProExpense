package com.arduia.expense.data.update

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.expense.data.ProExpenseServerRepository
import com.arduia.expense.data.SettingsRepository
import com.arduia.expense.data.ext.getAppVersionCode
import com.arduia.expense.data.local.UpdateStatusDataModel
import com.arduia.expense.data.network.CheckUpdateDto
import com.arduia.expense.model.getDataOrError
import timber.log.Timber
import java.lang.Exception

class CheckAboutUpdateWorker @WorkerInject constructor(
    @Assisted private val context: Context,
    @Assisted param: WorkerParameters,
    private val settingRepo: SettingsRepository,
    private val serverRepository: ProExpenseServerRepository
) : CoroutineWorker(context, param) {
    override suspend fun doWork(): Result {

        Timber.d("CheckAbout doWork")
        try {
            val versionCode = context.getAppVersionCode()
            val status =
                serverRepository.getAboutUpdateSync(CheckUpdateDto.Request(versionCode, "test"))
                    .getDataOrError()
            Timber.d("status $status")

            if (status.isShouldUpdate) {

                Timber.d("isShouldUpdate true")
                val updateStatusLevel =
                    if (status.isCriticalUpdate) UpdateStatusDataModel.STATUS_CRITICAL_UPDATE
                    else UpdateStatusDataModel.STATUS_NORMAL_UPDATE

                val info = status.info
                return if (info == null) {
                    settingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NO_UPDATE)
                    Timber.d("no info $info")
                    Result.success()
                } else {
                    settingRepo.setAboutUpdate(info)
                    settingRepo.setUpdateStatus(updateStatusLevel)
                    Timber.d("has Info $info $updateStatusLevel")
                    Result.success()
                }

            } else {
                settingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NO_UPDATE)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            settingRepo.setUpdateStatus(UpdateStatusDataModel.STATUS_NO_UPDATE)
            return Result.failure()
        }

        return Result.success()
    }
}