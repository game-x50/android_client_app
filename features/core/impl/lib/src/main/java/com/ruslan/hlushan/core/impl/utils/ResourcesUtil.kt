@file:SuppressWarnings("MatchingDeclarationName")

package com.ruslan.hlushan.core.impl.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringDef
import androidx.core.content.ContextCompat
import com.ruslan.hlushan.android.extensions.currentLocale
import com.ruslan.hlushan.android.extensions.wrapContextWithNewLanguage
import com.ruslan.hlushan.core.language.code.LangNonFullCode

private const val STRING_DEF_TYPE: String = "string"
private const val DRAWABLE_DEF_TYPE: String = "drawable"

@SuppressWarnings("UnusedPrivateClass")
@StringDef(STRING_DEF_TYPE, DRAWABLE_DEF_TYPE)
private annotation class ResDefType

internal fun getWrappedOrUpdateContext(context: Context, currentAppLangCode: LangNonFullCode): Context {
    val currentResourcesLang = LangNonFullCode.fromLocale(context.resources.configuration.currentLocale)
    return if (currentResourcesLang == currentAppLangCode) {
        context
    } else {
        context.wrapContextWithNewLanguage(currentAppLangCode)
    }
}

internal fun Context.getStringResourceByName(stringResName: String): String {
    var value: String? = null
    try {
        val resId = getResIdByName(stringResName, STRING_DEF_TYPE)
        value = resId?.let { getString(resId) }
    } catch (ignore: Exception) {
    }

    return value.orEmpty()
}

internal fun Context.getDrawableResourceByName(drawableResName: String): Drawable? =
        try {
            val resId = getDrawableResourceIdByName(drawableResName)
            resId?.let { ContextCompat.getDrawable(this, resId) }
        } catch (ignore: Exception) {
            null
        }

@DrawableRes
internal fun Context.getDrawableResourceIdByName(drawableResName: String): Int? =
        try {
            getResIdByName(drawableResName, DRAWABLE_DEF_TYPE)
        } catch (ignore: Exception) {
            null
        }

private fun Context.getResIdByName(resName: String, @ResDefType defType: String): Int? =
        try {
            val packageName = packageName
            resources.getIdentifier(resName, defType, packageName)
        } catch (ignore: Exception) {
            null
        }