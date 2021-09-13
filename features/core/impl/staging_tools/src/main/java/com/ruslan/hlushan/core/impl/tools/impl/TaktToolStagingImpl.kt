package com.ruslan.hlushan.core.impl.tools.impl

import android.app.Application
import android.content.Context
import android.graphics.Color
import com.ruslan.hlushan.core.api.tools.TaktTool
import com.ruslan.hlushan.core.logger.api.AppLogger
import jp.wasabeef.takt.Seat
import jp.wasabeef.takt.Takt
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

internal class TaktToolStagingImpl
@Inject
constructor(
        private val appContext: Context,
        private val appLogger: AppLogger
) : TaktTool {

    private val initialized = AtomicBoolean(false)
    private val atomicBoolean = AtomicBoolean(false)

    override var show: Boolean
        get() = atomicBoolean.get()
        set(newValue) {
            if (atomicBoolean.compareAndSet(!newValue, newValue)) {
                if (newValue) {
                    show()
                } else {
                    hide()
                }
            }
        }

    @SuppressWarnings("MagicNumber", "UnsafeCast")
    private fun show() {
        if (initialized.compareAndSet(false, true)) {
            Takt.stock(appContext.applicationContext as Application)
                    .seat(Seat.BOTTOM_RIGHT)
                    .interval(250)
                    .color(Color.GREEN)
                    .size(24f)
                    .alpha(.5f)
//                    .useCustomControl()
                    .listener { fps -> appLogger.logClass(this::class.java, "$fps fps") }
        }

        Takt.play()
    }

    private fun hide() =
            Takt.finish()
}