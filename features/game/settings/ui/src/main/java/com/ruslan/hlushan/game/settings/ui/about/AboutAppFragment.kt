package com.ruslan.hlushan.game.settings.ui.about

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.game.settings.ui.R
import com.ruslan.hlushan.game.settings.ui.di.getSettingsUiComponent

internal class AboutAppFragment : BaseFragment(
        layoutResId = R.layout.game_settings_ui_about_app_screen
) {

    @UiMainThread
    override fun injectDagger2() = getSettingsUiComponent().inject(this)
}

internal class AboutAppScreen : FragmentScreen {

    override val screenKey: String get() = "AboutAppScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = AboutAppFragment()
}