package com.ruslan.hlushan.android.extensions

import android.os.SystemClock
import android.view.View

class ThrottledOnClickListener(
        private val intervalMillis: Long = DEFAULT_INTERVAL_MILLIS,
        private val onClickListener: (View) -> Unit
) : View.OnClickListener {

    private var lastClickedTimestamp: Long = 0L

    override fun onClick(v: View) {
        val currentTimestamp = SystemClock.elapsedRealtime()

        val delta = (currentTimestamp - lastClickedTimestamp)

        if (delta > intervalMillis) {
            lastClickedTimestamp = currentTimestamp
            onClickListener(v)
        }
    }

    companion object {
        const val DEFAULT_INTERVAL_MILLIS = 500L
    }
}