package com.ruslan.hlushan.core.ui.dialog

import android.graphics.drawable.ColorDrawable
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import java.lang.ref.WeakReference

class DialogBackgroundColorLifecyclePluginObserver(
        owner: DialogFragment,
        @ColorInt private val color: Int
) : LifecyclePluginObserver {

    private val ownerDialogReference = WeakReference(owner)

    @UiMainThread
    override fun onAfterSuperStart() {
        ownerDialogReference.get()?.dialog?.window?.setBackgroundDrawable(ColorDrawable(color))
    }
}