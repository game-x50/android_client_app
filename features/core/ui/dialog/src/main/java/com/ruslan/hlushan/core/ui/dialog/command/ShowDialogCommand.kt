package com.ruslan.hlushan.core.ui.dialog.command

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.third_party.androidx.fragment.extensions.showNowSafety

abstract class ShowDialogCommand : DialogCommand() {

    @UiMainThread
    protected abstract fun getOrCreate(fragmentManager: FragmentManager): DialogFragment

    @UiMainThread
    final override fun execute(fragmentManager: FragmentManager) =
            getOrCreate(fragmentManager)
                    .showNowSafety(fragmentManager, tag)
}