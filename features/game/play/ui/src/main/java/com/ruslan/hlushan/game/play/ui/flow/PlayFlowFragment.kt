package com.ruslan.hlushan.game.play.ui.flow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.R
import com.ruslan.hlushan.core.ui.fragment.BaseFlowFragment
import com.ruslan.hlushan.core.ui.routing.SupportNestedNavigator
import com.ruslan.hlushan.game.play.ui.di.getGamePlayUiComponent
import com.ruslan.hlushan.game.play.ui.records.GameRecordsListScreen

/**
 * @author Ruslan Hlushan on 2019-06-26
 */

private const val GAMES_FLOW_NAME = "GAMES_FLOW"

internal class PlayFlowFragment : com.ruslan.hlushan.core.ui.fragment.BaseFlowFragment() {

    override val flowName: String get() = GAMES_FLOW_NAME

    @UiMainThread
    override fun injectDagger2() = getGamePlayUiComponent().inject(this)

    @UiMainThread
    override fun openFirstFlowScreen() = flowCicerone.router.newRootScreen(GameRecordsListScreen())

    override fun createFlowNavigator(): Navigator =
            com.ruslan.hlushan.core.ui.routing.SupportNestedNavigator(
                    parentRouter = this.parentRouter,
                    activity = requireActivity(),
                    childFragmentManager = this.childFragmentManager,
                    containerId = R.id.app_container
            )
}

class PlayFlowScreen : FragmentScreen {

    override val screenKey: String get() = "PlayFlowScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = PlayFlowFragment()
}