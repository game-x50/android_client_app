package com.ruslan.hlushan.core.ui.fragment

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.extensions.ifNotNull
import java.lang.ref.WeakReference

internal class OnBackPressedCallbackLifecyclePluginObserver(
        owner: Fragment,
        private val onBackPressed: () -> Unit
) : LifecyclePluginObserver {

    private val ownerFragmentReference = WeakReference(owner)

    override fun onAfterSuperAttach() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = onBackPressed()
        }
        ifNotNull(ownerFragmentReference.get()) { nonNullFragment ->
            nonNullFragment.activity?.onBackPressedDispatcher?.addCallback(nonNullFragment, callback)
        }
    }
}