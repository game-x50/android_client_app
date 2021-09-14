package com.ruslan.hlushan.core.impl.utils.exceptions

import com.ruslan.hlushan.core.error.isNoNetworkException
import com.ruslan.hlushan.core.logger.api.AppLogger
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins

/*
EN - https://proandroiddev.com/rxjava2-undeliverableexception-f01d19d18048
EN - https://medium.com/@bherbst/the-rxjava2-default-error-handler-e50e0cab6f9f
RU - https://habr.com/post/422611/
*/

internal object RxErrorHandlingUtil {

    class AppConsumer(private val appLogger: AppLogger) : Consumer<Throwable> {

        override fun accept(originalError: Throwable) {
            val undeliverableExceptionCause: Throwable? = (originalError as? UndeliverableException)?.cause

            if (undeliverableExceptionCause != null
                && !undeliverableExceptionCause.isNoNetworkException()
                && (undeliverableExceptionCause !is InterruptedException)) {

                val currentThread = Thread.currentThread()
                currentThread.uncaughtExceptionHandler.uncaughtException(currentThread, originalError)
            } else {
                appLogger.logClass(RxErrorHandlingUtil::class.java, "UnexpectedError", originalError)
            }
        }
    }

    fun setRxErrorHandling(appLogger: AppLogger) = RxJavaPlugins.setErrorHandler(AppConsumer(appLogger))
}