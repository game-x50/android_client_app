package com.ruslan.hlushan.storage.delegate

import android.content.SharedPreferences
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ReferencePreferencesDelegate<T : Any?>(
        private val preferences: SharedPreferences,
        private val writer: (SharedPreferences.Editor, newValue: T) -> SharedPreferences.Editor,
        reader: (SharedPreferences) -> T
) : ReadWriteProperty<Any?, T> {

    private val atomicValue = AtomicReference<T>(reader(preferences))

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        atomicValue.set(value)
        @Suppress("CommitPrefEdits")
        writer(preferences.edit(), value).apply()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = atomicValue.get()
}