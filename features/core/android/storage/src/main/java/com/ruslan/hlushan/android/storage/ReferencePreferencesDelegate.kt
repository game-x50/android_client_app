package com.ruslan.hlushan.android.storage

import android.content.SharedPreferences
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ReferencePreferencesDelegate<T : Any?>(
        private val preferences: SharedPreferences,
        private val writer: (SharedPreferences.Editor, newValue: T) -> SharedPreferences.Editor,
        reader: (SharedPreferences) -> T,
        private val onValueSaved: ((T) -> Unit)? = null
) : ReadWriteProperty<Any?, T> {

    private val atomicValue = AtomicReference<T>(reader(preferences))

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        atomicValue.set(value)
        @Suppress("CommitPrefEdits")
        writer(preferences.edit(), value).apply()
        onValueSaved?.invoke(value)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = atomicValue.get()
}