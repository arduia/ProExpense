package com.arduia.core.content

import android.content.Context
import android.os.Build

/**
 * Current Application Code
 * Current Application Version
 */
fun Context.getApplicationVersionCode(): Long {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageManager.getPackageInfo(packageName, 0).longVersionCode
    } else packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
}

fun Context.getApplicationVersionName(): String{
    return packageManager.getPackageInfo(packageName, 0).versionName
}