package com.arduia.expense.data.ext

import android.content.Context
import android.os.Build


@SuppressWarnings("versionCode")
fun Context.getAppVersionCode(): Long{

    val info = packageManager.getPackageInfo(packageName, 0)

    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
        info.longVersionCode
    }else info.versionCode.toLong()
}