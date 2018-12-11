package com.ruslan.hlushan.game.auth.ui.login

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
import com.ruslan.hlushan.game.auth.ui.isEmailValid
import com.ruslan.hlushan.game.auth.ui.isPasswordValid
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileScreen
import com.ruslan.hlushan.game.core.api.auth.AuthInteractor
import com.ruslan.hlushan.game.core.api.auth.dto.AuthError
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

/**
 * @author Ruslan Hlushan on 2019-07-10
 */
internal class LoginViewModel
@AssistedInject
constructor(
        appLogger: AppLogger,
        threadChecker: ThreadChecker,
        @Assisted private val router: Router,
        private val schedulersManager: SchedulersManager,
        private val authInteractor: AuthInteractor
) : BaseViewModel(appLogger, threadChecker) {

    @UiMainThread
    private val mutableCommandsQueue = MutableCommandQueue<Command>()

    val commandsQueue: CommandQueue<Command> get() = mutableCommandsQueue

    @UiMainThread
    fun logIn(email: String, password: String) {
        val isEmailValid = email.isEmailValid()
        if (!isEmailValid) {
            mutableCommandsQueue.add(Command.ShowEmailInputError())
        }

        val isPasswordValid = password.isPasswordValid()
        if (!isPasswordValid) {
            mutableCommandsQueue.add(Command.ShowPasswordInputError())
        }

        val allInputsValid = (isEmailValid && isPasswordValid)

        if (allInputsValid) {
            executeLoginRequest(email, password)
        }
    }

    @UiMainThread
    private fun executeLoginRequest(email: String, password: String) {
        authInteractor.logIn(email, password)
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnSuccess { result -> handleLoginResult(result) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe(
                        { appLogger.log(this, "log in success") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    private fun handleLoginResult(result: VoidOperationResult<AuthError.InvalidUserCredentials>) =
            when (result) {
                is OperationResult.Success -> router.replaceScreen(UserProfileScreen())
                is OperationResult.Error -> mutableCommandsQueue.add(Command.ShowAuthError(result.result))
            }

    sealed class Command : StrategyCommand {

        class ShowSimpleProgress(val show: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowEmailInputError : Command() {
            override fun produceStrategy(): HandleStrategy = SkipStrategy()
        }

        class ShowPasswordInputError : Command() {
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
        fun create(router: Router): LoginViewModel
    }
}