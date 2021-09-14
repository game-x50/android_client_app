package com.ruslan.hlushan.core.impl.tools.initUtils

import com.ruslan.hlushan.core.extensions.exhaustive
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.UiMainThread
import leakcanary.AppWatcher
import leakcanary.DefaultOnHeapAnalyzedListener
import leakcanary.LeakCanary
import leakcanary.OnHeapAnalyzedListener
import shark.HeapAnalysis
import shark.HeapAnalysisFailure
import shark.HeapAnalysisSuccess
import java.util.concurrent.TimeUnit

@UiMainThread
internal fun initLeakCanary(appLogger: AppLogger) {
    @SuppressWarnings("MagicNumber")
    val watchDurationSeconds = 10L
    AppWatcher.config = AppWatcher.config.copy(
            enabled = true,
            watchActivities = true,
            watchFragments = true,
            watchFragmentViews = true,
            watchDurationMillis = TimeUnit.SECONDS.toMillis(watchDurationSeconds)
    )

    LeakCanary.config = LeakCanary.config.copy(
            requestWriteExternalStoragePermission = true,
            onHeapAnalyzedListener = AppAnalysisResultListener(appLogger)
    )
}

//TODO: CHECK is working?????
//TODO: uploading to server example: https://square.github.io/leakcanary/recipes/#uploading-to-bugsnag
private class AppAnalysisResultListener(private val appLogger: AppLogger) : OnHeapAnalyzedListener {

    private val defaultOnHeapAnalyzedListener = DefaultOnHeapAnalyzedListener.create()

    override fun onHeapAnalyzed(heapAnalysis: HeapAnalysis) {

        when (heapAnalysis) {
            is HeapAnalysisFailure -> {
                appLogger.logClass(
                        AppAnalysisResultListener::class.java,
                        heapAnalysis.toString(),
                        heapAnalysis.exception
                )
            }
            is HeapAnalysisSuccess -> {
                appLogger.logClass(AppAnalysisResultListener::class.java, heapAnalysis.toString())
            }
        }.exhaustive

        defaultOnHeapAnalyzedListener.onHeapAnalyzed(heapAnalysis)
    }
}