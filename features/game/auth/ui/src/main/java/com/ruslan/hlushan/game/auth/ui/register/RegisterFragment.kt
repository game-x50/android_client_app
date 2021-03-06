package com.ruslan.hlushan.game.auth.ui.register

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
import com.ruslan.hlushan.game.auth.ui.databinding.GameAuthUiRegisterScreenBinding
import com.ruslan.hlushan.game.auth.ui.di.getAuthUiComponent
import com.ruslan.hlushan.game.auth.ui.observeConfirmPasswordInput
import com.ruslan.hlushan.game.auth.ui.showAuthError
import com.ruslan.hlushan.game.auth.ui.showEmailInputError
import com.ruslan.hlushan.game.auth.ui.showNickNameInputError
import com.ruslan.hlushan.game.auth.ui.showPasswordInputError
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding
import com.ruslan.hlushan.third_party.androidx.material.extensions.clearErrorOnAnyInput
import com.ruslan.hlushan.third_party.androidx.material.extensions.getTrimmedText

internal class RegisterFragment : BaseFragment(
        layoutResId = R.layout.game_auth_ui_register_screen
) {

    private val binding by bindViewBinding(GameAuthUiRegisterScreenBinding::bind)

    private val viewModel: RegisterViewModel by bindBaseViewModel {
        getAuthUiComponent().registerViewModelFactory().create(parentRouter)
    }

    @UiMainThread
    override fun injectDagger2() = getAuthUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addSystemPadding(top = true)

        binding?.registerScreenNickInput?.clearErrorOnAnyInput()
        binding?.registerScreenEmailInput?.clearErrorOnAnyInput()
        binding?.registerScreenPasswordInput?.clearErrorOnAnyInput()

        binding?.registerScreenConfirmPasswordInput?.observeConfirmPasswordInput(this::getPasswordTrimmedString)

        binding?.registerScreenRegisterBtn?.setThrottledOnClickListener {
            viewModel.register(
                    nickname = binding?.registerScreenNickInput.getTrimmedText(),
                    email = binding?.registerScreenEmailInput.getTrimmedText(),
                    password = getPasswordTrimmedString()
            )
        }

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    private fun handleCommand(command: RegisterViewModel.Command) =
            when (command) {
                is RegisterViewModel.Command.ShowSimpleProgress     -> showSimpleProgress(command.show)
                is RegisterViewModel.Command.ShowNickNameInputError -> showNickNameInputError()
                is RegisterViewModel.Command.ShowEmailInputError    -> showEmailInputError()
                is RegisterViewModel.Command.ShowPasswordInputError -> showPasswordInputError()
                is RegisterViewModel.Command.ShowLoginError         -> showAuthError(command.error)
                is RegisterViewModel.Command.ShowError              -> showError(command.error)
            }

    @UiMainThread
    private fun showNickNameInputError() {
        binding?.registerScreenNickInput?.showNickNameInputError()
    }

    @UiMainThread
    private fun showEmailInputError() {
        binding?.registerScreenEmailInput?.showEmailInputError()
    }

    @UiMainThread
    private fun showPasswordInputError() {
        binding?.registerScreenPasswordInput?.showPasswordInputError()
    }

    @UiMainThread
    private fun getPasswordTrimmedString(): String =
            binding?.registerScreenPasswordInput.getTrimmedText()
}

internal class RegisterScreen : FragmentScreen {

    override val screenKey: String get() = "RegisterScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = RegisterFragment()
}