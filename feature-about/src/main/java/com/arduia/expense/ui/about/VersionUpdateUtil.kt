package com.arduia.expense.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

object VersionUpdateUtil {

    fun openAppStoreLink(context: Context){
        val intent = Intent().apply {
            val url = "https://play.google.com/store/apps/details?id=com.arduia.expense"
            action = Intent.ACTION_VIEW
            data = url.toUri()
        }
        context.startActivity(intent)
    }
}