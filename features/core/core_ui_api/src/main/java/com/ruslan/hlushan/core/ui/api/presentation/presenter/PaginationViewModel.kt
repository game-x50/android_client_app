package com.ruslan.hlushan.core.ui.api.presentation.presenter

import com.ruslan.hlushan.core.api.dto.pagination.PaginationLimits
import com.ruslan.hlushan.core.api.dto.pagination.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.pagination.PaginationResponse
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationAction
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationSideEffect
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationState
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.itemsCount
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.reduceState
import com.ruslan.hlushan.core.ui.api.recycler.RecyclerItem
import com.ruslan.hlushan.extensions.exhaustive
import com.ruslan.hlushan.rxjava2.extensions.safetyDispose
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/*
 * https://www.youtube.com/watch?v=g7wwybnXE40
 */

private const val FILTER_UPDATE_DEBOUNCE_MILLIS: Long = 300

//TODO: #write_unit_tests
abstract class PaginationViewModel<F : Any, ItemId : Any, RI : RecyclerItem<ItemId>, Id : Any>(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        initFilter: F,
        private val limits: PaginationLimits = PaginationLimits(),
        protected val schedulersManager: SchedulersManager
) : BaseViewModel(appLogger, threadChecker) {

    private val filterSubject = PublishSubject.create<F>()

    @UiMainThread
    private var requestDisposable: Disposable? = null

    //for overriding in case of not real items that should not be counted(headers,...)
    @UiMainThread
    protected open val totalItemCount: Int
        get() = state.itemsCount()

    @UiMainThread
    protected var state: PaginationState<F, ItemId, RI, Id> = PaginationState.Active.Empty.Default(filter = initFilter)
        /** use [setNewState] as setter*/
        private set

    init {
        filterSubject
                .debounce(FILTER_UPDATE_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(schedulersManager.ui)
                .doOnNext { filter -> handleFilterUpdated(filter) }
                .subscribe(
                        { action -> this.appLogger.log(this@PaginationViewModel, "action = $action") },
                        { error -> this.appLogger.log(this@PaginationViewModel, "filterSubject", error) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    protected abstract fun loadData(
            pagesRequest: PaginationPagesRequest<Id>,
            filter: F
    ): Single<PaginationResponse<RI, Id>>

    @UiMainThread
    protected abstract fun onStateUpdated()

    @UiMainThread
    fun retryIfError() {
        val localState = state
        if (localState is PaginationState.WithError) {
            loadMoreAction(direction = localState.additional.loadDirection)
        }
    }

    @UiMainThread
    fun refresh() = handleAction(action = PaginationAction.UI.Refresh(state.filter))

    @UiMainThread
    fun onScrolled(boundaryVisibleItem: Int, scrolledBottom: Boolean) {
        when {
            (scrolledBottom && ((totalItemCount - limits.itemsOffsetToBorder) <= boundaryVisibleItem)) -> {
                loadMoreAction(direction = PaginationPagesRequest.Direction.NEXT)
            }

            (!scrolledBottom && (boundaryVisibleItem <= limits.itemsOffsetToBorder))                   -> {
                loadMoreAction(direction = PaginationPagesRequest.Direction.PREVIOUS)
            }
        }
    }

    @UiMainThread
    protected fun updateFilter(newFilter: F) = filterSubject.onNext(newFilter)

    @UiMainThread
    protected fun onSingleItemUpdated(updatedItem: RI, notifyStateUpdated: Boolean) =
            handleAction(action = PaginationAction.Change.SingleItemUpdated(updatedItem, notifyStateUpdated))

    @UiMainThread
    protected fun onSingleItemDeleted(deletedItemId: ItemId) =
            handleAction(action = PaginationAction.Change.SingleItemDeleted(deletedItemId))

    @UiMainThread
    private fun handleFilterUpdated(newFilter: F) =
            handleAction(action = PaginationAction.UI.Refresh(newFilter))

    @UiMainThread
    private fun loadMoreAction(direction: PaginationPagesRequest.Direction) =
            handleAction(action = PaginationAction.UI.LoadMore(direction = direction))

    @UiMainThread
    private fun handleAction(action: PaginationAction<F, ItemId, RI, Id>) {
        val result = reduceState(
                state = state,
                action = action,
                limits = limits
        )

        val notifyStateUpdated = !result.sideEffects.any { sideEffect -> (sideEffect is PaginationSideEffect.AvoidNotifyStateUpdated) }

        setNewState(newState = result.newState, notifyStateUpdated = notifyStateUpdated)

        result.sideEffects.forEach { sideEffect ->
            when (sideEffect) {
                is PaginationSideEffect.LoadMore                -> {
                    loadMore(params = sideEffect, filter = result.newState.filter)
                }
                is PaginationSideEffect.AvoidNotifyStateUpdated -> {
                    Unit //NOP
                }
            }.exhaustive
        }
    }

    @UiMainThread
    private fun loadMore(params: PaginationSideEffect.LoadMore<Id>, filter: F) {
        requestDisposable?.safetyDispose()

        requestDisposable = loadData(pagesRequest = params.pagesRequest, filter = filter)
                .map<PaginationAction<F, ItemId, RI, Id>> { response -> PaginationAction.Response.Success(response) }
                .onErrorReturn { error -> PaginationAction.Response.Error(error) }
                .observeOn(schedulersManager.ui)
                .doOnSuccess { action -> handleAction(action) }
                .subscribe(
                        { action -> appLogger.log(this@PaginationViewModel, "request action = $action") },
                        { error -> appLogger.log(this@PaginationViewModel, "request", error) }
                )

        requestDisposable?.joinUntilDestroy()
    }

    @UiMainThread
    private fun setNewState(newState: PaginationState<F, ItemId, RI, Id>, notifyStateUpdated: Boolean) {
        if (state != newState) {
            state = newState

            if (notifyStateUpdated) {
                onStateUpdated()
            }
        }
    }
}