package com.ruslan.hlushan.game.play.ui.game

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.command.ShowDialogCommand
import com.ruslan.hlushan.game.play.ui.view.COMBO_SUMS

internal class ShowComboSumsDialog : AbstractPlayNumbersDialog() {

    override val numbers: IntArray
        get() = COMBO_SUMS

    class ShowCommand : ShowDialogCommand() {

        override val tag: String get() = "TAG_SHOW_COMBO_SUMS_DIALOG"

        @UiMainThread
        override fun getOrCreate(fragmentManager: FragmentManager): DialogFragment =
                ((fragmentManager.findFragmentByTag(tag) as? ShowComboSumsDialog)
                 ?: ShowComboSumsDialog())
    }
}