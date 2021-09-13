package com.ruslan.hlushan.game.top.ui.games

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.command.extensions.handleCommandQueue
import com.ruslan.hlushan.core.pagination.api.PaginationState
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.pagination.view.setUpPagination
import com.ruslan.hlushan.core.ui.recycler.adapter.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.recycler.adapter.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.core.ui.viewbinding.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.viewmodel.extensions.bindBaseViewModel
import com.ruslan.hlushan.extensions.exhaustive
import com.ruslan.hlushan.game.top.ui.R
import com.ruslan.hlushan.game.top.ui.databinding.GameTopUiTopGamesListScreenBinding
import com.ruslan.hlushan.game.top.ui.di.clearTopUiComponent
import com.ruslan.hlushan.game.top.ui.di.getTopUiComponent
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding

internal class TopGamesFragment : BaseFragment(
        layoutResId = R.layout.game_top_ui_top_games_list_screen
) {

    private val topGamesAdapter = DelegatesRecyclerAdapter(TopGamesAdapterDelegate())

    private val binding by bindViewBinding(GameTopUiTopGamesListScreenBinding::bind)

    private val viewModel: TopGamesViewModel by bindBaseViewModel {
        getTopUiComponent().topGamesViewModelFactory().create()
    }

    @UiMainThread
    override fun injectDagger2() = getTopUiComponent().inject(this)

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(RecyclerViewLifecyclePluginObserver { binding?.gameTopUiTopGamesListScreenList })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.gameTopUiTopGamesListScreenList?.addSystemPadding(top = true)

        this.setUpPagination(
                recyclerAdapter = topGamesAdapter,
                paginationViewModel = viewModel,
                recyclerView = binding?.gameTopUiTopGamesListScreenList,
                swipeRefreshLayout = binding?.gameTopUiTopGamesListScreenSwipeRefresh
        )

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    override fun onCloseScope() = clearTopUiComponent()

    @UiMainThread
    private fun handleCommand(command: TopGamesViewModel.Command) =
            when (command) {
                is TopGamesViewModel.Command.SetState -> setState(command)
            }

    @UiMainThread
    private fun setState(command: TopGamesViewModel.Command.SetState) {
        topGamesAdapter.submitList(command.items)

        @Suppress("MaxLineLength")
        binding?.gameTopUiTopGamesListScreenSwipeRefresh?.isRefreshing = (command.additional is PaginationState.Additional.Loading)

        when (command.additional) {
            is PaginationState.Additional.WaitingForLoadMore -> {
                //TODO
            }
            is PaginationState.Additional.Error              -> {
                //TODO
            }
            is PaginationState.Additional.Loading,
            null                                             -> Unit
        }.exhaustive
    }
}

class TopGamesScreen : FragmentScreen {

    override val screenKey: String get() = "TopGamesScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = TopGamesFragment()
}