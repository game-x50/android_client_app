package com.ruslan.hlushan.game.auth.ui.profile

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.dto.OperationResult
import com.ruslan.hlushan.core.api.dto.VoidOperationResult
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.presentation.command.CommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.SkipStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.ui.api.presentation.presenter.BaseViewModel
import com.ruslan.hlushan.game.api.auth.AuthInteractor
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.api.play.PlayRecordsInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class UserProfileViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        @Assisted private val router: Router,
        private val schedulersManager: SchedulersManager,
        private val authInteractor: AuthInteractor,
        private val playRecordsInteractor: PlayRecordsInteractor
) : BaseViewModel(appLogger, threadChecker) {

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    init {
        val currentUser = authInteractor.getUser()
        if (currentUser != null) {
            mutableCommandsQueue.add(Command.ShowCurrentUser(currentUser))
        } else {
            this.router.exit()
        }
    }

    @UiMainThread
    fun updateUserProfileWith(newNickname: String, oldPassword: String, newPassword: String) {

        val validatedNewNickname = User.Nickname.createIfValid(newNickname)
        if (validatedNewNickname == null) {
            mutableCommandsQueue.add(Command.ShowNickNameInputError())
        }

        val validatedOldPassword = User.Password.createIfValid(oldPassword)
        if (validatedOldPassword == null) {
            mutableCommandsQueue.add(Command.ShowOldPasswordInputError())
        }

        val validatedNewPassword = User.Password.createIfValid(newPassword)
        if (validatedNewPassword == null) {
            mutableCommandsQueue.add(Command.ShowNewPasswordInputError())
        }

        if ((validatedNewNickname != null)
            && (validatedOldPassword != null)
            && (validatedNewPassword != null)) {
            executeUpdateRequest(
                    newNickname = validatedNewNickname,
                    newPassword = validatedNewPassword,
                    oldPassword = validatedOldPassword
            )
        }
    }

    @UiMainThread
    fun checkForLogOut() {
        playRecordsInteractor.getCountOfNotSynchronizedRecords()
                .observeOn(schedulersManager.ui)
                .doOnSuccess { countNotSynchedRecords ->
                    mutableCommandsQueue.add(Command.ShowConfirmLogOutDialog(countNotSynchedRecords))
                }
                .subscribe(
                        { countNotSynchedRecords ->
                            appLogger.log(this@UserProfileViewModel,
                                          "checkForLogOut: SUCCESS! countNotSynchedRecords = $countNotSynchedRecords")
                        },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    fun logOutConfirmed() {
        authInteractor.logOut()
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnComplete { router.exit() }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe(
                        { appLogger.log(this@UserProfileViewModel, "logOut success") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    private fun executeUpdateRequest(
            newNickname: User.Nickname,
            newPassword: User.Password,
            oldPassword: User.Password
    ) {
        authInteractor.updateUserWith(
                newNickname = newNickname,
                newPassword = newPassword,
                oldPassword = oldPassword
        )
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnSuccess { result -> handleUpdateResult(result) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe(
                        { appLogger.log(this@UserProfileViewModel, "updateUserWith SUCCESS") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    private fun handleUpdateResult(result: VoidOperationResult<AuthError>) =
            when (result) {
                is OperationResult.Success -> router.exit()
                is OperationResult.Error   -> mutableCommandsQueue.add(Command.ShowAuthError(result.result))
            }

    sealed class Command : StrategyCommand {

        class ShowSimpleProgress(val show: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowCurrentUser(val currentUser: User) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }

        class ShowNickNameInputError : Command() {
            override fun produceStrategy(): HandleStrategy = SkipStrategy()
        }

        class ShowOldPasswordInputError : Command() {
            override fun produceStrategy(): HandleStrategy = SkipStrategy()
        }

        class ShowNewPasswordInputError : Command() {
            override fun produceStrategy(): HandleStrategy = SkipStrategy()
        }

        class ShowConfirmLogOutDialog(val countNotSynchedRecords: Int) : Command() {
            override fun produceStrategy(): HandleStrategy = SkipStrategy()
        }

        class ShowAuthError(val error: AuthError) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }

        class ShowError(val error: Throwable) : Command() {
            override fun produceStrategy(): HandleStrategy = OneExecutionStateStrategy()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(router: Router): UserProfileViewModel
    }
}