package com.ruslan.hlushan.game.settings.ui.languages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.recyclerview.widget.GridLayoutManager
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.android.extensions.setUpDefaults
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.extensions.bindBaseViewModel
import com.ruslan.hlushan.core.ui.api.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.api.presentation.command.handleCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.api.recycler.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.extensions.lazyUnsafe
import com.ruslan.hlushan.game.settings.ui.R
import com.ruslan.hlushan.game.settings.ui.databinding.GameSettingsUiLanguagesScreenBinding
import com.ruslan.hlushan.game.settings.ui.di.getGameSettingsUiComponent

/**
 * @author Ruslan Hlushan on 2019-07-10
 */

private const val SPAN_WIDTH_DP = 160

internal class LanguagesFragment : BaseFragment(
        layoutResId = R.layout.game_settings_ui_languages_screen
) {

    private val binding by bindViewBinding(GameSettingsUiLanguagesScreenBinding::bind)

    private val viewModel: LanguagesViewModel by bindBaseViewModel {
        getGameSettingsUiComponent().languagesViewModelFactory().create(parentRouter)
    }

    private val languagesRecyclerAdapter by lazyUnsafe {
        DelegatesRecyclerAdapter(
                LanguagesAdapterDelegate(resourceManager) { wrappedLanguage ->
                    viewModel.setApplicationLanguage(wrappedLanguage)
                }
        )
    }

    @UiMainThread
    override fun injectDagger2() = getGameSettingsUiComponent().inject(this)

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(RecyclerViewLifecyclePluginObserver { binding?.languagesScreenRecycler })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { nonNullActivity ->
            val screenWidthDp = resources.configuration.screenWidthDp
            binding?.languagesScreenRecycler?.setUpDefaults(
                    adapter = languagesRecyclerAdapter,
                    layoutManager = GridLayoutManager(nonNullActivity, screenWidthDp / SPAN_WIDTH_DP)
            )
        }

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    private fun handleCommand(command: LanguagesViewModel.Command) =
            when (command) {
                is LanguagesViewModel.Command.ShowAvailableLanguages -> showAvailableLanguages(command.languages)
                is LanguagesViewModel.Command.ShowError              -> showError(command.error)
            }

    @UiMainThread
    private fun showAvailableLanguages(languages: List<LanguageRecyclerItem>) =
            languagesRecyclerAdapter.submitList(languages)
}

internal class LanguagesScreen : FragmentScreen {

    override val screenKey: String get() = "LanguagesScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = LanguagesFragment()
}