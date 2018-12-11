package com.ruslan.hlushan.android.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar

@SuppressWarnings("TooGenericExceptionCaught")
fun View.showSnackBar(
        message: String,
        actionText: String?,
        onActionListener: View.OnClickListener?,
        duration: Int
): Snackbar? {

    var snackbar: Snackbar? = null
    try {
        snackbar = Snackbar.make(this, message, duration)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return snackbar?.apply {
        val actionListenerFinal = (onActionListener ?: View.OnClickListener { snackbar.dismiss() })
        val actionTextFinal = (actionText ?: snackbar.context.getString(android.R.string.cancel))
        setAction(actionTextFinal, actionListenerFinal)
        show()
    }
}