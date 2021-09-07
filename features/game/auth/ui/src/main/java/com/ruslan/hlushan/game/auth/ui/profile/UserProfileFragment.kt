package com.ruslan.hlushan.game.auth.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.dialog.showSimpleProgress
import com.ruslan.hlushan.core.ui.api.extensions.bindBaseViewModel
import com.ruslan.hlushan.core.ui.fragment.BaseFragment
import com.ruslan.hlushan.game.api.auth.dto.User
import com.ruslan.hlushan.game.auth.ui.R
import com.ruslan.hlushan.game.auth.ui.databinding.GameAuthUiUserProfileScreenBinding
import com.ruslan.hlushan.game.auth.ui.di.getGameAuthUiComponent
import com.ruslan.hlushan.game.auth.ui.showAuthError
import com.ruslan.hlushan.third_party.androidx.insets.addSystemPadding

internal class UserProfileFragment : com.ruslan.hlushan.core.ui.fragment.BaseFragment(
        layoutResId = R.layout.game_auth_ui_user_profile_screen
), ConfirmLogOutDialog.LogOutConfirmedListener {

    private val binding by bindViewBinding(GameAuthUiUserProfileScreenBinding::bind)

    private val viewModel: UserProfileViewModel by bindBaseViewModel {
        getGameAuthUiComponent().userProfileViewModelFactory().create(parentRouter)
    }

    @UiMainThread
    override fun injectDagger2() = getGameAuthUiComponent().inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.addSystemPadding(top = true)

        binding?.userProfileScreenConfirmNewPasswordInput?.observeConfirmPasswordInput(
                this::getNewPasswordTrimmedString
        )

        binding?.userProfileScreenUpdateUserProfileBtn?.setThrottledOnClickListener {
            viewModel.updateUserProfileWith(
                    newNickname = binding?.userProfileScreenNickNameInput.getTrimmedText(),
                    oldPassword = binding?.userProfileScreenOldPasswordInput.getTrimmedText(),
                    newPassword = getNewPasswordTrimmedString(),
            )
        }

        binding?.userProfileScreenLogoutBtn?.setThrottledOnClickListener { viewModel.checkForLogOut() }

        this.handleCommandQueue(commandQueue = viewModel.commandsQueue, handler = this::handleCommand)
    }

    @UiMainThread
    override fun oLogOutConfirmed() = viewModel.logOutConfirmed()

    @Suppress("MaxLineLength")
    @UiMainThread
    private fun handleCommand(command: UserProfileViewModel.Command) =
            when (command) {
                is UserProfileViewModel.Command.ShowSimpleProgress        -> showSimpleProgress(command.show)
                is UserProfileViewModel.Command.ShowCurrentUser           -> showCurrentUser(command.currentUser)
                is UserProfileViewModel.Command.ShowNickNameInputError    -> showNickNameInputError()
                is UserProfileViewModel.Command.ShowNewPasswordInputError -> showNewPasswordInputError()
                is UserProfileViewModel.Command.ShowOldPasswordInputError -> showOldPasswordInputError()
                is UserProfileViewModel.Command.ShowConfirmLogOutDialog   -> showConfirmLogOutDialog(command.countNotSynchedRecords)
                is UserProfileViewModel.Command.ShowAuthError             -> showAuthError(command.error)
                is UserProfileViewModel.Command.ShowError                 -> showError(command.error)
            }

    @UiMainThread
    private fun showCurrentUser(currentUser: User) {
        binding?.userProfileScreenEmail?.text = currentUser.email.value
        binding?.userProfileScreenNickNameInput?.editText?.setText(currentUser.nickname.value)
    }

    @UiMainThread
    private fun showNickNameInputError() {
        binding?.userProfileScreenNickNameInput?.showNickNameInputError()
    }

    @UiMainThread
    private fun showNewPasswordInputError() {
        binding?.userProfileScreenNewPasswordInput?.showPasswordInputError()
    }

    @UiMainThread
    private fun showOldPasswordInputError() {
        binding?.userProfileScreenOldPasswordInput?.showPasswordInputError()
    }

    @UiMainThread
    private fun getNewPasswordTrimmedString(): String =
            binding?.userProfileScreenNewPasswordInput.getTrimmedText()
}

internal class UserProfileScreen : FragmentScreen {

    override val screenKey: String get() = "UserProfileScreen"

    override fun createFragment(factory: FragmentFactory): Fragment = UserProfileFragment()
}