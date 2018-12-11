package com.ruslan.hlushan.game.settings.ui.instruction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.game.settings.ui.R
import com.ruslan.hlushan.game.settings.ui.di.getGameSettingsUiComponent

internal class GameInstructionFragment : BaseFragment(
        layoutResId = R.layout.game_settings_ui_instruction_screen
) {

    @UiMainThread
    override fun injectDagger2() = getGameSettingsUiComponent().inject(this)
}

internal class GameInstructionScreen : FragmentScreen {

    override val screenKey: String get() = "GameInstructionScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = GameInstructionFragment()
}