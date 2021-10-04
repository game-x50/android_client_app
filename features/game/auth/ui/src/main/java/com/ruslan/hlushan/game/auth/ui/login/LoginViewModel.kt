package com.ruslan.hlushan.game.auth.ui.login

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.dto.OperationResult
import com.ruslan.hlushan.core.api.dto.VoidOperationResult
import com.ruslan.hlushan.core.command.CommandQueue
import com.ruslan.hlushan.core.command.MutableCommandQueue
import com.ruslan.hlushan.core.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.command.strategy.SkipStrategy
import com.ruslan.hlushan.core.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.viewmodel.BaseViewModel
import com.ruslan.hlushan.game.api.auth.AuthInteractor
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileScreen
import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

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
        val validatedEmail = User.Email.createIfValid(email)
        if (validatedEmail == null) {
            mutableCommandsQueue.add(Command.ShowEmailInputError())
        }

        val validatedPassword = User.Password.createIfValid(password)
        if (validatedPassword == null) {
            mutableCommandsQueue.add(Command.ShowPasswordInputError())
        }

        if ((validatedEmail != null) && (validatedPassword != null)) {
            executeLoginRequest(validatedEmail, validatedPassword)
        }
    }

    @UiMainThread
    private fun executeLoginRequest(email: User.Email, password: User.Password) {
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
    private fun handleLoginResult(result: VoidOperationResult<AuthError.Login>) =
            when (result) {
                is OperationResult.Success -> router.replaceScreen(UserProfileScreen())
                is OperationResult.Error -> mutableCommandsQueue.add(Command.ShowLoginError(result.result))
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

        class ShowLoginError(val error: AuthError.Login) : Command() {
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