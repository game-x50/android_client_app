package com.ruslan.hlushan.game.auth.ui.register

import com.github.terrakok.cicerone.Router
import com.ruslan.hlushan.core.api.dto.OperationResult
import com.ruslan.hlushan.core.api.dto.VoidOperationResult
import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.command.CommandQueue
import com.ruslan.hlushan.core.command.MutableCommandQueue
import com.ruslan.hlushan.core.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.command.strategy.SkipStrategy
import com.ruslan.hlushan.core.command.strategy.StrategyCommand
import com.ruslan.hlushan.core.ui.viewmodel.BaseViewModel
import com.ruslan.hlushan.game.api.auth.AuthInteractor
import com.ruslan.hlushan.game.api.auth.dto.AuthError
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.auth.ui.profile.UserProfileScreen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class RegisterViewModel
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
    fun register(nickname: String, email: String, password: String) {

        val validatedNickname = User.Nickname.createIfValid(nickname)
        if (validatedNickname == null) {
            mutableCommandsQueue.add(Command.ShowNickNameInputError())
        }

        val validatedEmail = User.Email.createIfValid(email)
        if (validatedEmail == null) {
            mutableCommandsQueue.add(Command.ShowEmailInputError())
        }

        val validatedPassword = User.Password.createIfValid(password)
        if (validatedPassword == null) {
            mutableCommandsQueue.add(Command.ShowPasswordInputError())
        }

        if ((validatedNickname != null)
            && (validatedEmail != null)
            && (validatedPassword != null)) {
            executeRegisterRequest(validatedNickname, validatedEmail, validatedPassword)
        }
    }

    @UiMainThread
    private fun executeRegisterRequest(
            nickname: User.Nickname,
            email: User.Email,
            password: User.Password
    ) {
        authInteractor.createNewUser(nickname, email, password)
                .observeOn(schedulersManager.ui)
                .doOnSubscribe { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = true)) }
                .doOnSuccess { result -> handleRegisterResult(result) }
                .doFinally { mutableCommandsQueue.add(Command.ShowSimpleProgress(show = false)) }
                .subscribe(
                        { appLogger.log(this, "user create success") },
                        { error -> mutableCommandsQueue.add(Command.ShowError(error)) }
                )
                .joinUntilDestroy()
    }

    @UiMainThread
    private fun handleRegisterResult(result: VoidOperationResult<AuthError.UserWithSuchCredentialsExists>) =
            when (result) {
                is OperationResult.Success -> router.replaceScreen(UserProfileScreen())
                is OperationResult.Error   -> mutableCommandsQueue.add(Command.ShowAuthError(result.result))
            }

    sealed class Command : StrategyCommand {

        class ShowSimpleProgress(val show: Boolean) : Command() {
            override fun produceStrategy(): HandleStrategy = AddToEndSingleStrategy()
        }

        class ShowNickNameInputError : Command() {
            override fun produceStrategy(): HandleStrategy = SkipStrategy()
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
        fun create(router: Router): RegisterViewModel
    }
}