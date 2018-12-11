package com.ruslan.hlushan.android.extensions

import android.os.SystemClock
import android.view.View

/**
 * @author Ruslan Hlushan on 2019-07-08
 */

class ThrottledOnClickListener(
        private val intervalMillis: Long = DEFAULT_INTERVAL_MILLIS,
        private val onClickListener: (View) -> Unit
) : View.OnClickListener {

    @SuppressWarnings("ClassOrdering")
    companion object {
        const val DEFAULT_INTERVAL_MILLIS = 500L
    }

    private var lastClickedTimestamp: Long = 0L

    override fun onClick(v: View) {
        val currentTimestamp = SystemClock.elapsedRealtime()

        val delta = (currentTimestamp - lastClickedTimestamp)

        if (delta > intervalMillis) {
            lastClickedTimestamp = currentTimestamp
            onClickListener(v)
        }
    }
}