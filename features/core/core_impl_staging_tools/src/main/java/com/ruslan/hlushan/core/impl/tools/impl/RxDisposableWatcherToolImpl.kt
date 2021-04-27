package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ruslan.hlushan.android.core.api.di.createExternalReportsFile
import com.ruslan.hlushan.android.core.api.di.getUriForFile
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

        val originalIntent = Intent(Intent.ACTION_VIEW)
                .setDataAndType(reportUri, appContext.contentResolver.getType(reportUri))
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooserIntent = Intent.createChooser(originalIntent, "Select app")
        chooserIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        appContext.startActivity(chooserIntent)
    }

    private fun createReport(): Uri {
        val result = RxDisposableWatcher.probe()
        val report = HtmlReportBuilder(result).build()

        val fileName = (appContext.packageName
                        + "_"
                        + SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.US).format(Date())
                        + ".html")

        val file = appContext.createExternalReportsFile(fileName)
        val reportUri = appContext.getUriForFile(file)
        appContext.grantUriPermission(appContext.packageName, reportUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val stream = FileOutputStream(file)
        stream.use { s -> s.write(report.toByteArray()) }

        return reportUri
    }
}