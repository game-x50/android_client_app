package com.ruslan.hlushan.game.play.ui.game

import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.extensions.showNowSafety
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.command.DialogCommand
import com.ruslan.hlushan.game.play.ui.view.AVAILABLE_NUMBERS

internal class ShowAvailableNumbersDialog : AbstractPlayNumbersDialog() {

    override val numbers: IntArray
        get() = AVAILABLE_NUMBERS

    class ShowCommand : DialogCommand() {

        override val tag: String get() = "TAG_SHOW_AVAILABLE_NUMBERS_DIALOG"

        @UiMainThread
        override fun execute(fragmentManager: FragmentManager) =
                ((fragmentManager.findFragmentByTag(tag) as? ShowAvailableNumbersDialog)
                 ?: ShowAvailableNumbersDialog())
                        .showNowSafety(fragmentManager, tag)
    }
}