package com.ruslan.hlushan.core.manager.api

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.ruslan.hlushan.core.value.holder.ValueHolder
import io.reactivex.Single

interface ResourceManager {

    fun getString(@StringRes strResId: Int): String

    fun getString(@StringRes strResId: Int, vararg formatArgs: Any): String

    fun getStringResourceByName(stringResName: String): String

    @DrawableRes
    fun getDrawableResourceIdByName(drawableResName: String): Int?

    fun readRawTextFile(@RawRes rawResId: Int): Single<ValueHolder<String?>>
}