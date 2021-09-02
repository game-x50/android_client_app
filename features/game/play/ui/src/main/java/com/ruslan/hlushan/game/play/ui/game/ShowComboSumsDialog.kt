package com.ruslan.hlushan.game.play.ui.game

import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.android.extensions.showNowSafety
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.command.DialogCommand
import com.ruslan.hlushan.game.play.ui.view.COMBO_SUMS

internal class ShowComboSumsDialog : AbstractPlayNumbersDialog() {

    override val numbers: IntArray
        get() = COMBO_SUMS

    class ShowCommand : DialogCommand() {

        override val tag: String get() = "TAG_SHOW_COMBO_SUMS_DIALOG"

        @UiMainThread
        override fun execute(fragmentManager: FragmentManager) =
                ((fragmentManager.findFragmentByTag(tag) as? ShowComboSumsDialog)
                 ?: ShowComboSumsDialog())
                        .showNowSafety(fragmentManager, tag)
    }
}