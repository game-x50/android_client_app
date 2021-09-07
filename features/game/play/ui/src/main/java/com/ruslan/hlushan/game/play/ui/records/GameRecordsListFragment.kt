package com.ruslan.hlushan.game.play.ui.records

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.recyclerview.widget.RecyclerView
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.api.extensions.bindBaseViewModel
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.api.presentation.viewmodel.pagination.PaginationState
import com.ruslan.hlushan.core.ui.api.recycler.DelegatesRecyclerAdapter
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerViewLifecyclePluginObserver
import com.ruslan.hlushan.extensions.exhaustive
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.extensions.lazyUnsafe
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.play.ui.R
import com.ruslan.hlushan.game.play.ui.databinding.GamePlayUiGameRecordsListScreenBinding
import com.ruslan.hlushan.game.play.ui.di.getGamePlayUiComponent
import com.ruslan.hlushan.game.play.ui.game.new_game.NewGameScreen
import com.ruslan.hlushan.game.play.ui.records.select.level.SelectGameLevelDialog
import com.ruslan.hlushan.game.play.ui.records.select.level.showSelectGameLevelDialog
import com.ruslan.hlushan.game.play.ui.records.select.order.SelectOrderGameRecordsDialog
import com.ruslan.hlushan.game.play.ui.records.select.order.showSelectOrderGameRecordsDialog

private const val RECYCLER_DY_MINIMAL_SCROLL = 20

internal class GameRecordsListFragment :
        com.ruslan.hlushan.core.ui.fragment.BaseFragment(layoutResId = R.layout.game_play_ui_game_records_list_screen),
        SelectGameLevelDialog.OnGameLevelSelectedListener,
        ConfirmDeleteGameRecordDialog.OnDeleteGameRecordConfirmedListener,
        SelectOrderGameRecordsDialog.SelectOrderGameRecordsParamsListener {

    private val binding by bindViewBinding(GamePlayUiGameRecordsListScreenBinding::bind)

    @SuppressWarnings("MagicNumber")
    private var isFabMenuOpened: Boolean = false
        set(newValue) {
            field = newValue

            val minDistance: Float = if (newValue) {
                -resources.getDimension(com.ruslan.hlushan.core.ui.api.R.dimen.default_4_padding)
            } else {
                0f
            }

            arrayOf(
                    binding?.gameRecordsListScreenNewBtn,
                    binding?.gameRecordsListScreenOpenOrderSelectBtn,
                    binding?.gameRecordsListScreenSyncBtn
            ).forEachIndexed { index, floatingActionButton ->
                val userIndex = (index + 1)
                floatingActionButton?.animate()?.translationY(userIndex * minDistance)
            }
        }

    private val gameRecordsRecyclerAdapter by lazyUnsafe {
        DelegatesRecyclerAdapter(
                GameRecordsAdapterDelegate(
                        onItemClick = { item -> viewModel.selectRecord(item) },
                        onDeleteClick = { gameWithState -> showConfirmDeleteGameRecordDialog(gameWithState.record) },
                        observeUpdates = viewModel::observeUpdates
                )
        )
    }

    private val viewModel: GameRecordsListViewModel by bindBaseViewModel {
        getGamePlayUiComponent().gameRecordsListViewModelFactory().create(parentRouter)
    }

    @UiMainThread
    override fun injectDagger2() = getGamePlayUiComponent().inject(this)

    @UiMainThread
    override fun initLifecyclePluginObservers() {
        super.initLifecyclePluginObservers()
        addLifecyclePluginObserver(RecyclerViewLifecyclePluginObserver { binding?.gameRecordsListScreenList })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.gameRecordsListScreenList?.addSystemPadding(top = true)

        this.setUpPagination(
                recyclerAdapter = gameRecordsRecyclerAdapter,
                paginationViewModel = viewModel,
                recyclerView = binding?.gameRecordsListScreenList,
                swipeRefreshLayout = binding?.gameRecordsListScreenSwipeRefresh
        )
        binding?.gameRecordsListScreenSyncBtn?.isEnabled = false
        initButtonsClicks()
        initRecycler()

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    override fun onStart() {
        super.onStart()

        ifNotNull(binding?.gameRecordsListScreenList) { recycler ->
            GameRecordsAdapterDelegate.subscribeAllActive(recycler)
        }
    }

    override fun onStop() {
        ifNotNull(binding?.gameRecordsListScreenList) { recycler ->
            GameRecordsAdapterDelegate.unsubscribeAll(recycler)
        }

        super.onStop()
    }

    @UiMainThread
    override fun onGameLevelSelected(gameSize: GameSize) = parentRouter.navigateTo(NewGameScreen(gameSize))

    @UiMainThread
    override fun onDeleteGameRecordConfirmed(gameRecord: GameRecord) = viewModel.removeRecord(gameRecord)

    @UiMainThread
    override fun onOrderGameRecordsParamsSelected(orderParams: GameRecordWithSyncState.Order.Params) {
        viewModel.orderParams = orderParams
    }

    @UiMainThread
    private fun handleCommand(command: GameRecordsListViewModel.Command) =
            when (command) {
                is GameRecordsListViewModel.Command.ShowSimpleProgress -> showSimpleProgress(command.show)
                is GameRecordsListViewModel.Command.SetState           -> setState(command)
                is GameRecordsListViewModel.Command.EnableSyncButton   -> enableSyncButton(command.enable)
                is GameRecordsListViewModel.Command.ShowError          -> showError(command.error)
            }

    @UiMainThread
    private fun initButtonsClicks() {
        binding?.gameRecordsListScreenNewBtn?.setThrottledOnClickListener {
            showSelectGameLevelDialog()
        }
        binding?.gameRecordsListScreenOpenOrderSelectBtn?.setThrottledOnClickListener {
            showSelectOrderGameRecordsDialog(viewModel.orderParams)
        }
        binding?.gameRecordsListScreenSyncBtn?.setThrottledOnClickListener { viewModel.startSync() }

        binding?.gameRecordsListScreenMenuBtn?.setThrottledOnClickListener { isFabMenuOpened = !isFabMenuOpened }
    }

    @UiMainThread
    private fun initRecycler() {
        binding?.gameRecordsListScreenList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    (dy > RECYCLER_DY_MINIMAL_SCROLL)  -> showBottomButtons(show = false)
                    (dy < -RECYCLER_DY_MINIMAL_SCROLL) -> showBottomButtons(show = true)
                }
            }
        })
    }

    @UiMainThread
    private fun showBottomButtons(show: Boolean) {
        arrayOf(
                binding?.gameRecordsListScreenMenuBtn,
                binding?.gameRecordsListScreenNewBtn,
                binding?.gameRecordsListScreenOpenOrderSelectBtn,
                binding?.gameRecordsListScreenSyncBtn
        ).forEach { floatingActionButton ->
            floatingActionButton?.show(show = show)
        }
    }

    @UiMainThread
    private fun notifyFakeScroll() {
        viewsHandler.post {
            binding?.gameRecordsListScreenList?.notifyOnScrolledBottom(viewModel::onScrolled)
        }
    }

    @UiMainThread
    private fun setState(command: GameRecordsListViewModel.Command.SetState) {
        gameRecordsRecyclerAdapter.submitList(command.gameRecords) {
            notifyFakeScroll()
        }

        @Suppress("MaxLineLength")
        binding?.gameRecordsListScreenSwipeRefresh?.isRefreshing = (command.additional is PaginationState.Additional.Loading)

        when (command.additional) {
            is PaginationState.Additional.WaitingForLoadMore -> {
                //TODO
            }
            is PaginationState.Additional.Error -> {
                //TODO
            }
            is PaginationState.Additional.Loading,
            null                                -> Unit
        }.exhaustive
    }

    @UiMainThread
    private fun enableSyncButton(enable: Boolean) {
        binding?.gameRecordsListScreenSyncBtn?.isEnabled = enable
    }
}

internal class GameRecordsListScreen : FragmentScreen {

    override val screenKey: String get() = "GameRecordsListScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = GameRecordsListFragment()
}