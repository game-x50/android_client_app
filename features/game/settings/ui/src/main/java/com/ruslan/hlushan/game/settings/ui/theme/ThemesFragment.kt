package com.ruslan.hlushan.game.settings.ui.theme

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.radiobutton.MaterialRadioButton
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.utils.ThemeMode
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.game.settings.ui.R
import com.ruslan.hlushan.game.settings.ui.databinding.GameSettingsUiThemesScreenBinding
import com.ruslan.hlushan.game.settings.ui.di.getGameSettingsUiComponent
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding
import javax.inject.Inject

internal class ThemesFragment : BaseFragment(
        layoutResId = R.layout.game_settings_ui_themes_screen
) {

    private val binding by bindViewBinding(GameSettingsUiThemesScreenBinding::bind)

    @Inject
    lateinit var settings: Settings

    @UiMainThread
    override fun injectDagger2() = getGameSettingsUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addSystemPadding(top = true)

        binding?.themesScreenGroup?.setOnCheckedChangeListener { group, checkedId ->
            val themeMode: ThemeMode? = group.getThemeModeById(checkedId)
            if (themeMode != null) {
                setApplicationTheme(newThemeMode = themeMode)
            }
        }

        showAvailableThemes()
    }

    @UiMainThread
    private fun showAvailableThemes() {
        binding?.themesScreenGroup?.removeAllViews()

        settings.availableThemeModes.forEach { themeMode ->
            binding?.themesScreenGroup?.addOptionFor(
                    themeMode = themeMode,
                    currentSelected = settings.themeMode
            )
        }
    }

    @UiMainThread
    private fun setApplicationTheme(newThemeMode: ThemeMode) {
        settings.themeMode = newThemeMode
        showAvailableThemes()
    }
}

internal class ThemesScreen : FragmentScreen {

    override val screenKey: String get() = "ThemesScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = ThemesFragment()
}

private fun RadioGroup.addOptionFor(themeMode: ThemeMode, currentSelected: ThemeMode) {
    val option = MaterialRadioButton(this.context)
    option.id = View.generateViewId()
    option.tag = themeMode
    option.isChecked = (themeMode == currentSelected)
    option.setText(themeMode.nameResId)

    val verticalPadding = this.resources.getDimensionPixelOffset(
            com.ruslan.hlushan.core.ui.api.R.dimen.default_1_2_padding
    )
    option.setPadding(verticalPadding, verticalPadding, 0, verticalPadding)

    this.addView(option)
}

private fun RadioGroup.getThemeModeById(@IdRes childId: Int): ThemeMode? {
    val checkedView = this.findViewById<View?>(childId)
    return (checkedView?.tag as? ThemeMode)
}

@get:StringRes
private val ThemeMode.nameResId: Int
    get() = when (this) {
        ThemeMode.NIGHT          -> R.string.game_settings_ui_theme_dark
        ThemeMode.LIGHT          -> R.string.game_settings_ui_theme_light
        ThemeMode.SAVE_BATTERY   -> R.string.game_settings_ui_theme_save_battery
        ThemeMode.SYSTEM_DEFAULT -> R.string.game_settings_ui_theme_system_default
    }