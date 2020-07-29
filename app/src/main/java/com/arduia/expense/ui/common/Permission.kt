package com.arduia.expense.ui.common

import android.Manifest
import androidx.core.app.ActivityCompat
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