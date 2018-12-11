package com.ruslan.hlushan.game.screens.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFlowFragment
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.api.utils.BottomMenuHolder
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.game.R
import com.ruslan.hlushan.game.databinding.GameAppMainScreenBinding
import com.ruslan.hlushan.game.di.getGameMainUiComponent
import com.ruslan.hlushan.game.play.ui.flow.PlayFlowScreen
import com.ruslan.hlushan.game.settings.ui.flow.SettingsFlowScreen

private const val MAIN_FLOW_NAME = "MAIN_FLOW"

internal class MainScreenFragment : BaseFlowFragment(
        layoutResId = R.layout.game_app_main_screen
), BottomMenuHolder {

    override val flowName: String get() = MAIN_FLOW_NAME

    private val binding by bindViewBinding(GameAppMainScreenBinding::bind)

    private val playFlowTab = PlayFlowScreen()
    private val settingsTab = SettingsFlowScreen()

    private val currentTabFragment: BaseFragment?
        get() = (childFragmentManager.fragments
                .firstOrNull { fragment -> !fragment.isHidden } as? BaseFragment)

    @UiMainThread
    override fun injectDagger2() = requireActivity().getGameMainUiComponent().inject(this)

    override fun createFlowNavigator(): Navigator = object : AppNavigator(
            activity = requireActivity(),
            fragmentManager = childFragmentManager,
            containerId = -1
    ) {

        override fun activityBack() {
            if (binding?.mainScreenBottomNavigationBar?.selectedItemId != R.id.tab_play) {
                binding?.mainScreenBottomNavigationBar?.selectedItemId = R.id.tab_play
            } else {
                activity.finish()
            }
        }
    }

    @UiMainThread
    override fun openFirstFlowScreen() = initTabs(playFlowTab, settingsTab)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ifNotNull(binding?.mainScreenBottomNavigationBar) { bottomNavigationView ->
            initBottomNavigationBar(bottomNavigationView)
        }
    }

    @UiMainThread
    override fun showBottomMenu(show: Boolean) {
        binding?.mainScreenBottomNavigationBar?.isVisible = show
    }

    @UiMainThread
    private fun initBottomNavigationBar(bottomNavigationBar: BottomNavigationView) {
        bottomNavigationBar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tab_play     -> {
                    selectTab(playFlowTab)
                    true
                }
                R.id.tab_settings -> {
                    selectTab(settingsTab)
                    true
                }
                else              -> {
                    false
                }
            }
        }
    }

    @UiMainThread
    private fun selectTab(tab: FragmentScreen) {
        val currentFragment = currentTabFragment
        val newFragmentInStack = childFragmentManager.findFragmentByTag(tab.screenKey)

        val isSelectedAlreadyShown = ((currentFragment != null)
                                      && (newFragmentInStack != null)
                                      && (currentFragment == newFragmentInStack))

        if (!isSelectedAlreadyShown) {

            val transaction = childFragmentManager.beginTransaction()

            if (currentFragment != null) {
                transaction.hide(currentFragment)
            }

            if (newFragmentInStack != null) {
                transaction.show(newFragmentInStack)
            } else {
                transaction.add(R.id.main_screen_container, createTabFragment(tab), tab.screenKey)
            }

            transaction.commitNow()
        }
    }

    @UiMainThread
    override fun onBackPressed() {
        currentTabFragment?.onBackPressed()
    }

    private fun initTabs(firstShownTab: FragmentScreen, vararg other: FragmentScreen) {

        val transaction = childFragmentManager.beginTransaction()

        for (otherTab in other) {
            val fragment = createTabFragment(otherTab)
            transaction
                    .add(R.id.main_screen_container, fragment, otherTab.screenKey)
                    .hide(fragment)
        }

        transaction.add(R.id.main_screen_container, createTabFragment(firstShownTab), firstShownTab.screenKey)

        transaction.commit()
    }

    private fun createTabFragment(screen: FragmentScreen): Fragment = screen.createFragment(this.childFragmentManager.fragmentFactory)
}

internal class MainScreen : FragmentScreen {

    override val screenKey: String get() = "MainScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = MainScreenFragment()
}