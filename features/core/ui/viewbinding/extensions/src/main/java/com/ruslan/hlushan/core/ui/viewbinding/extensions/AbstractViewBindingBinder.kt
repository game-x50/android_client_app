package com.ruslan.hlushan.core.ui.viewbinding.extensions

import android.view.View
import androidx.viewbinding.ViewBinding
import com.ruslan.hlushan.core.thread.SingleThreadSafety
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.utils.UiMainThreadChecker
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver

@SingleThreadSafety
internal abstract class AbstractViewBindingBinder<in Owner : LifecyclePluginObserver.Owner, out V : ViewBinding>(
        private val viewBind: (View) -> V
) : AbstractViewLifecycleBinder<Owner, V>() {

    override val threadChecker: ThreadChecker = UiMainThreadChecker

    protected abstract fun getOwnerView(owner: Owner): View?

    override fun tryCreateValue(thisRef: Owner): V? {
        val ownerView = getOwnerView(thisRef)

        return if (ownerView != null) {
            viewBind(ownerView)
        } else {
            null
        }
    }
}