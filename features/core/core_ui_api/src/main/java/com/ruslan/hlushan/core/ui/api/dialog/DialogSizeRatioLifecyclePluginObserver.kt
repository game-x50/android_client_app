package com.ruslan.hlushan.core.ui.api.dialog

import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ruslan.hlushan.android.extensions.applicationScreenDimension
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.extensions.ifNotNull
import java.lang.ref.WeakReference

class DialogSizeRatioLifecyclePluginObserver(
        owner: DialogFragment,
        private val widthRatio: Double? = null,
        private val heightRatio: Double? = null
) : LifecyclePluginObserver {

    private val ownerDialogReference = WeakReference(owner)

    @UiMainThread
    override fun onAfterSuperStart() {
        ifNotNull(ownerDialogReference.get()?.context?.applicationScreenDimension()) { displayMetrics ->

            val width = if (widthRatio != null) {
                (displayMetrics.widthPixels * widthRatio).toInt()
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }

            val height = if (heightRatio != null) {
                (displayMetrics.heightPixels * heightRatio).toInt()
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }

            ownerDialogReference.get()?.dialog?.window?.setLayout(width, height)
        }
    }
}