package com.ruslan.hlushan.android.extensions

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.ViewGroup

val ViewGroup.childs: Iterable<View>
    get() = (0 until childCount).map { index -> getChildAt(index) }

val View.horizontalPaddingsSum: Int
    @SuppressLint("ObsoleteSdkInt")
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        (paddingEnd + paddingStart)
    } else {
        (paddingLeft + paddingRight)
    }

val View.startLeftPadding: Int
    @SuppressLint("ObsoleteSdkInt")
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        paddingStart
    } else {
        paddingLeft
    }

val View.endRightPadding: Int
    @SuppressLint("ObsoleteSdkInt")
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        paddingEnd
    } else {
        paddingRight
    }

fun View.setThrottledOnClickListener(
        intervalMillis: Long = ThrottledOnClickListener.DEFAULT_INTERVAL_MILLIS,
        onClickListener: ((View) -> Unit)?
) =
        setOnClickListener(if (onClickListener != null) {
            ThrottledOnClickListener(intervalMillis, onClickListener)
        } else {
            null
        })