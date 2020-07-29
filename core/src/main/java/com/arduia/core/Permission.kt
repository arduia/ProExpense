package com.arduia.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.requestStoragePermission(requestCode: Int) =
    ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ),
        requestCode
    )


fun Context.isStoragePermissionGrated():Boolean{
    val checkWritePm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val checkReadPm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
    val grantedPm = PackageManager.PERMISSION_GRANTED

    return (checkReadPm == grantedPm) && (checkWritePm == grantedPm)
}
