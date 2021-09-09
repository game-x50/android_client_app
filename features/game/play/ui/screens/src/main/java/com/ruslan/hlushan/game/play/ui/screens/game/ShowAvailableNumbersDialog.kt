package com.ruslan.hlushan.game.play.ui.screens.game

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.game.play.api.AVAILABLE_NUMBERS

internal class ShowAvailableNumbersDialog : AbstractPlayNumbersDialog() {

    override val numbers: IntArray
        get() = AVAILABLE_NUMBERS

    class ShowCommand : ShowDialogCommand() {

        override val tag: String get() = "TAG_SHOW_AVAILABLE_NUMBERS_DIALOG"

        @UiMainThread
        override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
                ((fragmentManager.findFragmentByTag(tag) as? ShowAvailableNumbersDialog)
                 ?: ShowAvailableNumbersDialog())
    }
}