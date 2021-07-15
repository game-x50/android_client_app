package com.ruslan.hlushan.game.auth.ui

import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.ruslan.hlushan.android.extensions.applicationLabel
import com.ruslan.hlushan.android.extensions.clearError
import com.ruslan.hlushan.android.extensions.showErrorResId
import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.core.ui.api.dialog.showDialogMessage
import com.ruslan.hlushan.core.ui.api.presentation.view.fragment.BaseFragment
import com.ruslan.hlushan.game.core.api.auth.dto.AuthError

/**
 * @author Ruslan Hlushan on 2019-07-10
 */

internal fun TextInputLayout.showEmailInputError() =
        this.showErrorResId(R.string.game_auth_ui_email_input_error)

internal fun TextInputLayout.showPasswordInputError() =
        this.showErrorResId(R.string.game_auth_ui_password_input_error)

internal fun TextInputLayout.showPasswordConfirmInputError() =
        this.showErrorResId(R.string.game_auth_ui_password_confirm_input_error)

internal fun TextInputLayout.showNickNameInputError() =
        this.showErrorResId(R.string.game_auth_ui_nick_name_input_error)

internal inline fun TextInputLayout.observeConfirmPasswordInput(crossinline getOriginalPasswordText: () -> String) =
        this.editText?.doAfterTextChanged { editable ->
            if (editable.toString().trim() != getOriginalPasswordText()) {
                this.showPasswordConfirmInputError()
            } else {
                this.clearError()
            }
        }

@UiMainThread
internal fun BaseFragment.showAuthError(error: AuthError) =
        showDialogMessage(
                title = context?.applicationLabel.orEmpty(),
                message = getString(error.descriptionStringResId),
                buttonText = getString(com.ruslan.hlushan.core.ui.api.R.string.cancel)
        )