package com.ruslan.hlushan.game.auth.ui.forgot.password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.api.extensions.bindBaseViewModel
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.game.auth.ui.R
import com.ruslan.hlushan.game.auth.ui.databinding.GameAuthUiForgotPasswordScreenBinding
import com.ruslan.hlushan.game.auth.ui.di.getGameAuthUiComponent
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding

internal class ForgotPasswordFragment : com.ruslan.hlushan.core.ui.fragment.BaseFragment(
        layoutResId = R.layout.game_auth_ui_forgot_password_screen
), ResetPasswordEmailSentDialog.CancelDialogListener {

    private val binding by bindViewBinding(GameAuthUiForgotPasswordScreenBinding::bind)

    private val viewModel: ForgotPasswordViewModel by bindBaseViewModel {
        getGameAuthUiComponent().forgotPasswordViewModelFactory().create()
    }

    @UiMainThread
    override fun injectDagger2() = getGameAuthUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addSystemPadding(top = true)

        binding?.forgotPasswordScreenSendResetPasswordBtn?.setThrottledOnClickListener {
            viewModel.sendPasswordResetEmail(email = binding?.forgotPasswordScreenEmailInput.getTrimmedText())
        }

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    override fun onCancel() = parentRouter.exit()

    @Suppress("MaxLineLength")
    @UiMainThread
    private fun handleCommand(command: ForgotPasswordViewModel.Command) =
            when (command) {
                is ForgotPasswordViewModel.Command.ShowSimpleProgress     -> showSimpleProgress(command.show)
                is ForgotPasswordViewModel.Command.ShowEmailInputError    -> showEmailInputError()
                is ForgotPasswordViewModel.Command.ShowPasswordSentDialog -> showResetPasswordEmailSentDialog(command.email)
                is ForgotPasswordViewModel.Command.ShowError              -> showError(command.error)
            }

    @UiMainThread
    private fun showEmailInputError() {
        binding?.forgotPasswordScreenEmailInput?.showEmailInputError()
    }
}

internal class ForgotPasswordScreen : FragmentScreen {

    override val screenKey: String get() = "ForgotPasswordScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = ForgotPasswordFragment()
}