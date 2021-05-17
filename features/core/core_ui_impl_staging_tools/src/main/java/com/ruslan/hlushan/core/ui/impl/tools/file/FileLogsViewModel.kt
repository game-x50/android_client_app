package com.ruslan.hlushan.core.ui.impl.tools.file

import com.ruslan.hlushan.core.api.dto.pagination.PaginationPagesRequest
import com.ruslan.hlushan.core.api.dto.pagination.PaginationResponse
import com.ruslan.hlushan.core.api.dto.pagination.map
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.log.FileLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.command.CommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.ui.api.presentation.presenter.PaginationViewModel
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.PaginationState
import com.ruslan.hlushan.core.ui.api.presentation.presenter.pagination.itemsOrEmpty
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.Single
import java.io.File
import java.util.concurrent.atomic.AtomicLong

private const val FILES_COUNT_LIMIT = 10

internal class FileLogsViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        private val fileLogger: FileLogger,
        schedulersManager: SchedulersManager
) : PaginationViewModel<Unit, Long, LogRecyclerItem, String>(
        appLogger = appLogger,
        threadChecker = threadChecker,
        initFilter = Unit,
        schedulersManager = schedulersManager
) {

    private val idGenerator = AtomicLong()

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    init {
        refresh()
    }

    @UiMainThread
    override fun loadData(
            pagesRequest: PaginationPagesRequest<String>,
            filter: Unit
    ): Single<PaginationResponse<LogRecyclerItem, String>> =
            fileLogger.readNextFileLogs(pagesRequest, limitFiles = FILES_COUNT_LIMIT)
                    .map<PaginationResponse<LogRecyclerItem, String>> { response ->
                        response.map { logStringItem ->
                            LogRecyclerItem(
                                    id = idGenerator.incrementAndGet(),
                                    log = logStringItem
                            )
                        }
                    }

    @UiMainThread
    override fun onStateUpdated() =
            mutableCommandsQueue.add(Command.SetState(
                    logs = state.itemsOrEmpty(),
                    additional = (state as? PaginationState.Active)?.additional
            ))

    @UiMainThread
    fun bigLog() {
        Single.just(StringBuilder())
                .observeOn(schedulersManager.computation)
                .map { builder ->
                    @SuppressWarnings("MagicNumber")
                    val count = 1_000
                    repeat(count) { _ ->
                        builder.append("kvnaiunvsbsbnl;fnbl;dfnsl;sdnbldksbndklsbnodfnbiodfnsbiod")
                        builder.append("nsbonsdobfnosdacdssssssssssssssssssssssssssssssssssssssssl")
                    }
                    builder.toString()
                }
                .map { bigMessage -> fileLogger.logToFile(bigMessage) }
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnSuccess { mutableCommandsQueue.add(Command.ShowMessage(message = "bigLog SUCCESS")) }
                .doOnError { mutableCommandsQueue.add(Command.ShowMessage(message = "bigLog FAIL")) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe({ }, { })
                .joinWhileViewAttached()
    }

    @UiMainThread
    fun deleteLogFiles() {
        fileLogger.deleteLogFiles()
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnSuccess { refresh() }
                .doOnSuccess { mutableCommandsQueue.add(Command.ShowMessage(message = "deleteLogFiles SUCCESS")) }
                .doOnError { mutableCommandsQueue.add(Command.ShowMessage(message = "deleteLogFiles FAIL")) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe({ }, { })
                .joinWhileViewAttached()
    }

    @UiMainThread
    fun copyAllExistingLogsToSingleExternalStorageFile(destination: File) {
        fileLogger.copyAllExistingLogsToSingleFile(destination)
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnComplete { mutableCommandsQueue.add(Command.ShowMessage(message = "Saved to : $destination")) }
                .doOnError { mutableCommandsQueue.add(Command.ShowMessage(message = "collectAndSend FAIL")) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe({ }, { })
                .joinWhileViewAttached()
    }

    sealed class Command : StrategyCommand {

        class ShowSimpleProgress(val show: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowMessage(val message: String) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }

        class SetState(val logs: List<LogRecyclerItem>, val additional: PaginationState.Additional?) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(): FileLogsViewModel
    }
}