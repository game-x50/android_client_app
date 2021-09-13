package com.ruslan.hlushan.core.impl.tools.initUtils

import android.view.Choreographer
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.logger.api.AppLogger
import java.lang.reflect.Field
import java.lang.reflect.Modifier

//https://www.techyourchance.com/android-application-skips-frames/
@SuppressWarnings("TooGenericExceptionCaught", "SoonBlockedPrivateApi")
@UiMainThread
internal fun initSkippedFramesWarning(appLogger: AppLogger) =
        try {
            val field: Field = Choreographer::class.java.getDeclaredField("SKIPPED_FRAME_WARNING_LIMIT")
            field.isAccessible = true
            field.setInt(field, field.modifiers and Modifier.FINAL.inv())
            @Suppress("MagicNumber")
            field.set(null, 5)
        } catch (error: Throwable) {
            appLogger.logClass(
                    Choreographer::class.java,
                    message = "failed to change choreographer's skipped frames threshold:",
                    error = error
            )
        }