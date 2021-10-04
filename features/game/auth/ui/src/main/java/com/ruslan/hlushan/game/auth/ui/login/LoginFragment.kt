package com.ruslan.hlushan.game.auth.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.core.command.extensions.handleCommandQueue
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.core.ui.viewbinding.extensions.bindViewBinding
import com.ruslan.hlushan.core.ui.viewmodel.extensions.bindBaseViewModel
import com.ruslan.hlushan.game.auth.ui.R
import com.ruslan.hlushan.game.auth.ui.databinding.GameAuthUiLoginScreenBinding
import com.ruslan.hlushan.game.auth.ui.di.getAuthUiComponent
import com.ruslan.hlushan.game.auth.ui.forgot.password.ForgotPasswordScreen
import com.ruslan.hlushan.game.auth.ui.register.RegisterScreen
import com.ruslan.hlushan.game.auth.ui.showAuthError
import com.ruslan.hlushan.game.auth.ui.showEmailInputError
import com.ruslan.hlushan.game.auth.ui.showPasswordInputError
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding
import com.ruslan.hlushan.third_party.androidx.material.extensions.clearErrorOnAnyInput
import com.ruslan.hlushan.third_party.androidx.material.extensions.getTrimmedText

internal class LoginFragment : BaseFragment(
        layoutResId = R.layout.game_auth_ui_login_screen
) {

    private val binding by bindViewBinding(GameAuthUiLoginScreenBinding::bind)

    private val viewModel: LoginViewModel by bindBaseViewModel {
        getAuthUiComponent().loginViewModelFactory().create(parentRouter)
    }

    @UiMainThread
    override fun injectDagger2() = getAuthUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addSystemPadding(top = true)

        binding?.loginScreenEmailInput?.clearErrorOnAnyInput()
        binding?.loginScreenPasswordInput?.clearErrorOnAnyInput()

        binding?.loginScreenLoginBtn?.setThrottledOnClickListener {
            viewModel.logIn(
                    email = binding?.loginScreenEmailInput.getTrimmedText(),
                    password = binding?.loginScreenPasswordInput.getTrimmedText()
            )
        }

        binding?.loginScreenRegisterBtn?.setThrottledOnClickListener {
            parentRouter.replaceScreen(RegisterScreen())
        }
        binding?.loginScreenForgotPasswordBtn?.setThrottledOnClickListener {
            parentRouter.navigateTo(ForgotPasswordScreen())
        }

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    private fun handleCommand(command: LoginViewModel.Command) =
            when (command) {
                is LoginViewModel.Command.ShowSimpleProgress     -> showSimpleProgress(command.show)
                is LoginViewModel.Command.ShowEmailInputError    -> showEmailInputError()
                is LoginViewModel.Command.ShowPasswordInputError -> showPasswordInputError()
                is LoginViewModel.Command.ShowLoginError         -> showAuthError(command.error)
                is LoginViewModel.Command.ShowError              -> showError(command.error)
            }

    @UiMainThread
    private fun showEmailInputError() {
        binding?.loginScreenEmailInput?.showEmailInputError()
    }

    @UiMainThread
    private fun showPasswordInputError() {
        binding?.loginScreenPasswordInput?.showPasswordInputError()
    }
}

internal class LoginScreen : FragmentScreen {

    override val screenKey: String get() = "LoginScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = LoginFragment()
}