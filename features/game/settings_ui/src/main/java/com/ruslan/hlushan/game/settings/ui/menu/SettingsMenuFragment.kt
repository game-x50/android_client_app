package com.ruslan.hlushan.game.settings.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.android.extensions.addSystemPadding
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.api.extensions.bindBaseViewModel
import com.ruslan.hlushan.core.ui.api.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.api.presentation.command.handleCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.game.settings.ui.R
import com.ruslan.hlushan.game.settings.ui.about.AboutAppScreen
import com.ruslan.hlushan.game.settings.ui.databinding.GameSettingsUiMenuScreenBinding
import com.ruslan.hlushan.game.settings.ui.di.getGameSettingsUiComponent
import com.ruslan.hlushan.game.settings.ui.instruction.GameInstructionScreen
import com.ruslan.hlushan.game.settings.ui.languages.LanguagesScreen
import com.ruslan.hlushan.game.settings.ui.theme.ThemesScreen

internal class SettingsMenuFragment : BaseFragment(
        layoutResId = R.layout.game_settings_ui_menu_screen
) {

    private val binding by bindViewBinding(GameSettingsUiMenuScreenBinding::bind)

    private val viewModel: SettingsMenuViewModel by bindBaseViewModel {
        getGameSettingsUiComponent().settingMenuViewModelFactory().create(parentRouter)
    }

    @UiMainThread
    override fun injectDagger2() = getGameSettingsUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addSystemPadding(top = true)

        binding?.settingsMenuScreenAppLang?.setThrottledOnClickListener { parentRouter.navigateTo(LanguagesScreen()) }
        binding?.settingsMenuScreenAppTheme?.setThrottledOnClickListener { parentRouter.navigateTo(ThemesScreen()) }
        binding?.settingsMenuScreenUserProfile?.setThrottledOnClickListener { viewModel.openUserProfile() }
        binding?.settingsMenuScreenGameInstruction?.setThrottledOnClickListener { parentRouter.navigateTo(GameInstructionScreen()) }
        binding?.settingsMenuScreenGameTop?.setThrottledOnClickListener { viewModel.openTop() }
        binding?.settingsMenuScreenAboutApp?.setThrottledOnClickListener { parentRouter.navigateTo(AboutAppScreen()) }

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    private fun handleCommand(command: SettingsMenuViewModel.Command) =
            when (command) {
                is SettingsMenuViewModel.Command.ShowSimpleProgress -> showSimpleProgress(command.show)
                is SettingsMenuViewModel.Command.ShowAppLanguage    -> showAppLanguage(command)
                is SettingsMenuViewModel.Command.ShowError          -> showError(command.error)
            }

    @UiMainThread
    private fun showAppLanguage(command: SettingsMenuViewModel.Command.ShowAppLanguage) {
        binding?.settingsMenuScreenAppLang?.setHint(command.appLanguage)
        binding?.settingsMenuScreenAppLang?.setRightImageIcon(command.imageResName?.let { res -> resourceManager.getDrawableResourceIdByName(res) })
    }
}

internal class SettingsMenuScreen : FragmentScreen {

    override val screenKey: String get() = "SettingsMenuScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = SettingsMenuFragment()
}