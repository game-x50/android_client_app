package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import android.view.Gravity.START
import android.view.Gravity.TOP
import com.codemonkeylabs.fpslibrary.TinyDancer
import com.ruslan.hlushan.core.api.tools.TinyDancerTool
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * @author Ruslan Hlushan on 2019-07-18
 */
internal class TinyDancerToolStagingImpl
@Inject
constructor(
        private val appContext: Context
) : TinyDancerTool {

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

    @SuppressWarnings("MagicNumber")
    private fun show() {
        val displayMetrics = appContext.applicationContext.resources.displayMetrics

        TinyDancer.create()
                .redFlagPercentage(0.2f)
                .yellowFlagPercentage(0.05f)
                .startingGravity(TOP or START)
                .startingXPosition(displayMetrics.widthPixels / 10)
                .startingYPosition(displayMetrics.heightPixels / 4)
                .show(appContext)
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun hide() {
        try {
            TinyDancer.hide(appContext)
        } catch (e: Exception) {
            //TODO
        }
    }
}