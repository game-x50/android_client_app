package com.ruslan.hlushan.core.api.exceptions

import androidx.annotation.StringRes

class BaseAppException : RuntimeException {

    @get:StringRes
    val messageResId: Int?

    override val message: String get() = super.message.orEmpty()

    constructor(@StringRes messageResId: Int) {
        this.messageResId = messageResId
    }

    constructor(message: String) : super(message) {
        this.messageResId = null
    }

    constructor(message: String, cause: Throwable) : super(message, cause) {
        this.messageResId = null
    }

    constructor(@StringRes messageResId: Int, cause: Throwable) : super(cause) {
        this.messageResId = messageResId
    }
}