package com.ruslan.hlushan.core.ui.dialog

import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ruslan.hlushan.android.extensions.applicationScreenDimension
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import java.lang.ref.WeakReference

class DialogSizeRatioLifecyclePluginObserver(
        owner: DialogFragment,
        private val widthRatio: Double? = null,
        private val heightRatio: Double? = null
) : LifecyclePluginObserver {

    private val ownerDialogReference = WeakReference(owner)

    @UiMainThread
    override fun onAfterSuperStart() {
        ifNotNull(ownerDialogReference.get()?.activity?.applicationScreenDimension()) { screenSizePx ->

            val width = if (widthRatio != null) {
                (screenSizePx.width * widthRatio).toInt()
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }

            val height = if (heightRatio != null) {
                (screenSizePx.height * heightRatio).toInt()
            } else {
                ViewGroup.LayoutParams.WRAP_CONTENT
            }

            ownerDialogReference.get()?.dialog?.window?.setLayout(width, height)
        }
    }
}