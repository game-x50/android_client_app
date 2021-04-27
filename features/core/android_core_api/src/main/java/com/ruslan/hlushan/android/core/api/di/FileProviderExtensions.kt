package com.ruslan.hlushan.android.core.api.di

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.lang.IllegalStateException

/**
 * All paths should be declared in:
 * [com.ruslan.hlushan.android.core.api.R.xml.android_core_api_file_provider_paths]
 */
private val Context.fileProviderAuthority: String get() = "${this.packageName}.fileprovider"

fun Context.createExternalReportsFile(
        fileName: String,
): File {
    val reportsPath = this.getExternalFilesDir("reports")!!
    if (!reportsPath.exists()) {
        reportsPath.mkdirs()
    }
    val file = File(reportsPath, fileName)
    if (file.createNewFile()) {
        return file
    } else {
        throw IllegalStateException("Can't create file")
    }
}

fun Context.getUriForFile(
        file: File,
): Uri {
    return FileProvider.getUriForFile(
            this,
            this.fileProviderAuthority,
            file
    )
}