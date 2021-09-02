package com.ruslan.hlushan.core.impl.managers

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.ruslan.hlushan.core.api.dto.ValueHolder
import com.ruslan.hlushan.core.api.managers.ResourceManager
import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.api.managers.Settings
import com.ruslan.hlushan.core.api.managers.appLanguageNotFullCode
import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolSpecification
import com.ruslan.hlushan.core.api.utils.thread.ThreadPoolType
import com.ruslan.hlushan.core.impl.utils.files.readRawTextFile
import com.ruslan.hlushan.core.impl.utils.getDrawableResourceIdByName
import com.ruslan.hlushan.core.impl.utils.getStringResourceByName
import com.ruslan.hlushan.core.impl.utils.getWrappedOrUpdateContext
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author Ruslan Hlushan on 10/18/18.
 */
internal class ResourceManagerImpl
@Inject
constructor(
        private val settings: Settings,
        private val schedulersManager: SchedulersManager,
        private var appContext: Context
) : ResourceManager {

    private val wrappedResources: Resources
        get() {
            wrapContext()
            return appContext.resources
        }

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun getString(@StringRes strResId: Int): String =
            try {
                wrappedResources.getString(strResId)
            } catch (e: Exception) {
                ""
            }

    @SuppressWarnings("TooGenericExceptionCaught", "SpreadOperator")
    override fun getString(@StringRes strResId: Int, vararg formatArgs: Any): String =
            try {
                wrappedResources.getString(strResId, *formatArgs)
            } catch (e: Exception) {
                ""
            }

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun getNonTranslatableString(@StringRes strResId: Int): String =
            try {
                appContext.getString(strResId)
            } catch (e: Exception) {
                ""
            }

    override fun getStringResourceByName(stringResName: String): String {
        wrapContext()
        return appContext.getStringResourceByName(stringResName)
    }

    @DrawableRes
    override fun getDrawableResourceIdByName(drawableResName: String): Int? =
            appContext.getDrawableResourceIdByName(drawableResName)

    @ThreadPoolSpecification(ThreadPoolType.IO)
    override fun readRawTextFile(@RawRes rawResId: Int): Single<ValueHolder<String?>> =
            Single.fromCallable { ValueHolder(readRawTextFile(appContext, rawResId)) }
                    .subscribeOn(schedulersManager.io)

    private fun wrapContext() {
        val nonNullContext = appContext
        appContext = getWrappedOrUpdateContext(appContext, settings.appLanguageNotFullCode)
                     ?: nonNullContext
    }
}