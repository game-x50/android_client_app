package com.ruslan.hlushan.game.settings.ui.flow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.fragment.BaseFlowFragment
import com.ruslan.hlushan.core.ui.routing.SupportNestedNavigator
import com.ruslan.hlushan.game.settings.ui.di.getSettingsUiComponent
import com.ruslan.hlushan.game.settings.ui.menu.SettingsMenuScreen

private const val SETTINGS_FLOW_NAME = "SETTINGS_FLOW"

internal class SettingsFlowFragment : BaseFlowFragment() {

    override val flowName: String get() = SETTINGS_FLOW_NAME

    @UiMainThread
    override fun injectDagger2() = getSettingsUiComponent().inject(this)

    @UiMainThread
    override fun openFirstFlowScreen() = cicerone.router.newRootScreen(SettingsMenuScreen())

    @UiMainThread
    override fun createFlowNavigator(): Navigator =
            SupportNestedNavigator(
                    parentRouter = this.parentRouter,
                    activity = requireActivity(),
                    childFragmentManager = this.childFragmentManager,
                    containerId = com.ruslan.hlushan.core.ui.layout.container.R.id.app_container
            )
}

class SettingsFlowScreen : FragmentScreen {

    override val screenKey: String get() = "SettingsFlowScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = SettingsFlowFragment()
}