package com.ruslan.hlushan.android.extensions

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

/**
 * Created by User on 01.02.2018.
 */

var Configuration.currentLocale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)
    } else {
        @Suppress("Deprecation")
        locale
    }
    set(newValue) {
        @Suppress("ObsoleteSdkInt")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setLocale(newValue)
        } else {
            @Suppress("Deprecation")
            locale = newValue
        }
    }

fun Context.wrapContextWithNewLanguage(currentAppLangNotFullCode: String): Context {
    val neededAppLocale = Locale(currentAppLangNotFullCode)
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

fun Resources.updateResourcesWithNewLanguage(currentAppLangNotFullCode: String) {
    val conf = configuration
    val neededAppLocale = Locale(currentAppLangNotFullCode)
    conf.currentLocale = neededAppLocale
    @Suppress("Deprecation")
    updateConfiguration(conf, displayMetrics)
}