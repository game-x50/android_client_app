package com.ruslan.hlushan.android.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.ruslan.hlushan.core.language.code.LangNonFullCode
import com.ruslan.hlushan.core.language.code.toLocale
import java.util.Locale

var Configuration.currentLocale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)
    } else {
        @Suppress("Deprecation")
        locale
    }
    set(newValue) {
        @Suppress("ObsoleteSdkInt", "AppBundleLocaleChanges")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setLocale(newValue)
        } else {
            @Suppress("Deprecation")
            locale = newValue
        }
    }

fun Context.wrapContextWithNewLanguage(code: LangNonFullCode): Context {
    val neededAppLocale = code.toLocale()
    Locale.setDefault(neededAppLocale)

    val res = resources
    val newConfig = Configuration(res.configuration)

    newConfig.currentLocale = neededAppLocale

    @Suppress("ObsoleteSdkInt")
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        createConfigurationContext(newConfig)
    } else {
        @Suppress("Deprecation")
        res.updateConfiguration(newConfig, res.displayMetrics)
        this
    }
}

fun Resources.updateResourcesWithNewLanguage(code: LangNonFullCode) {
    val conf = configuration
    conf.currentLocale = code.toLocale()
    @Suppress("Deprecation")
    updateConfiguration(conf, displayMetrics)
}