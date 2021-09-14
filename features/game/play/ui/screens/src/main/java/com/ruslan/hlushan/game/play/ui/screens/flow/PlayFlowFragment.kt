package com.ruslan.hlushan.game.play.ui.screens.flow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.fragment.BaseFlowFragment
import com.ruslan.hlushan.core.ui.routing.SupportNestedNavigator
import com.ruslan.hlushan.game.play.ui.screens.di.gamePlayUiComponent
import com.ruslan.hlushan.game.play.ui.screens.records.GameRecordsListScreen

private const val GAMES_FLOW_NAME = "GAMES_FLOW"

internal class PlayFlowFragment : BaseFlowFragment() {

    override val flowName: String get() = GAMES_FLOW_NAME

    @UiMainThread
    override fun injectDagger2() = gamePlayUiComponent().inject(this)

    @UiMainThread
    override fun openFirstFlowScreen() = cicerone.router.newRootScreen(GameRecordsListScreen())

    override fun createFlowNavigator(): Navigator =
            SupportNestedNavigator(
                    parentRouter = this.parentRouter,
                    activity = requireActivity(),
                    childFragmentManager = this.childFragmentManager,
                    containerId = com.ruslan.hlushan.core.ui.layout.container.R.id.app_container
            )
}

class PlayFlowScreen : FragmentScreen {

    override val screenKey: String get() = "PlayFlowScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = PlayFlowFragment()
}