package com.ruslan.hlushan.android.storage

import android.content.SharedPreferences
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BoolPreferencesDelegate(
        private val preferences: SharedPreferences,
        private val key: String,
        defaultValue: Boolean
) : ReadWriteProperty<Any?, Boolean> {

    private val atomicValue = AtomicBoolean(preferences.getBoolean(key, defaultValue))

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        atomicValue.set(value)
        preferences.edit()
                .putBoolean(key, value)
                .apply()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = atomicValue.get()
}