@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.material.extensions

import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout?.getTrimmedText(): String =
        this?.editText?.text?.toString()?.trim().orEmpty()

fun TextInputLayout.showErrorResId(@StringRes stringResId: Int) =
        showErrorWithEnable(errorMessage = this.context.getString(stringResId))

fun TextInputLayout.showErrorWithEnable(errorMessage: String) {
    this.isErrorEnabled = true
    this.error = errorMessage
}

fun TextInputLayout.clearError() {
    this.isErrorEnabled = false
    this.error = null
}

fun TextInputLayout.clearErrorOnAnyInput() =
        this.editText?.doAfterTextChanged {
            this.clearError()
        }