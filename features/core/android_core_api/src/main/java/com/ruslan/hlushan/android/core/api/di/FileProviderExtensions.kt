package com.ruslan.hlushan.android.core.api.di

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.lang.IllegalStateException

/**
 * All paths should be declared in:
 * [com.ruslan.hlushan.android.core.api.R.xml.android_core_api_file_provider_paths]
 */
private val Context.fileProviderAuthority: String get() = "${this.packageName}.fileprovider"

fun Context.createExternalReportsFileWithReadPermissionsForOtherApps(
        fileName: String
): FileWithUri {
    val file = this.createExternalReportsFile(fileName)
    val reportUri = this.getUriForFile(file)
    this.grantUriPermission(this.packageName, reportUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return FileWithUri(file = file, uri = reportUri)
}

fun createOpenFileWithReadPermissionForOtherApps(
        contentResolver: ContentResolver,
        uri: Uri
): Intent {
    val originalIntent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, contentResolver.getType(uri))
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    //todo: hardcoded text to resources
    val chooserIntent = Intent.createChooser(originalIntent, "Select app")
    chooserIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    return chooserIntent
}

class FileWithUri(
        val file: File,
        val uri: Uri
)

fun Context.createExternalReportsFile(fileName: String): File {
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