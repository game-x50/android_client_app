package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import android.net.Uri
import com.ruslan.hlushan.android.core.api.di.createExternalReportsFileWithReadPermissionsForOtherApps
import com.ruslan.hlushan.android.core.api.di.createOpenFileWithReadPermissionForOtherApps
import com.ruslan.hlushan.core.api.tools.RxDisposableWatcherTool
import ru.fomenkov.rxdisposablewatcher.RxDisposableWatcher
import ru.fomenkov.rxdisposablewatcher.report.HtmlReportBuilder
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

internal class RxDisposableWatcherToolImpl
@Inject
constructor(
        private val appContext: Context
) : RxDisposableWatcherTool {

    init {
        RxDisposableWatcher.init()
    }

    override fun showReport() {
        val reportUri = createReport()
        val chooserIntent = createOpenFileWithReadPermissionForOtherApps(
                contentResolver = appContext.contentResolver,
                uri = reportUri
        )
        appContext.startActivity(chooserIntent)
    }

    private fun createReport(): Uri {
        val result = RxDisposableWatcher.probe()
        val report = HtmlReportBuilder(result).build()

        val fileName = (appContext.packageName
                        + "_"
                        + SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.US).format(Date())
                        + ".html")

        val fileWithUri = appContext.createExternalReportsFileWithReadPermissionsForOtherApps(fileName)

        val stream = FileOutputStream(fileWithUri.file)
        stream.use { s -> s.write(report.toByteArray()) }

        return fileWithUri.uri
    }
}