package com.ruslan.hlushan.core.ui.impl.manager

import com.ruslan.hlushan.core.error.BaseAppException
import com.ruslan.hlushan.core.error.NetworkException
import com.ruslan.hlushan.core.error.SimpleUserErrorMapper
import com.ruslan.hlushan.core.manager.api.ResourceManager
import javax.inject.Inject

internal class DefaultUiUserErrorProducer
@Inject
constructor(
        private val resourceManager: ResourceManager
) : SimpleUserErrorMapper {

    override fun produceUserMessage(error: Throwable): String? =
            when (error) {
                is BaseAppException -> {
                    resourceManager.getUserMessage(error)
                }
                is NetworkException -> {
                    resourceManager.getString(com.ruslan.hlushan.core.ui.api.R.string.error_internet_connection)
                }
                else                -> null
            }
}

private fun ResourceManager.getUserMessage(error: BaseAppException): String =
        (error.messageResId?.let { errorResId -> this.getString(errorResId) } ?: error.message)