package com.ruslan.hlushan.core.di

import kotlin.reflect.KClass

interface InjectorHolder {

    @Suppress("VARIABLE_HAS_PREFIX")
    val iBaseInjector: IBaseInjector

    val components: ClassInstanceMap
}

class ClassInstanceMap {

    private val holderMap: MutableMap<KClass<*>, Any> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    operator fun <V : Any> get(clazz: KClass<V>): V? = (holderMap[clazz] as? V)

    @Suppress("UNCHECKED_CAST")
    fun <V : Any> getOrPut(clazz: KClass<V>, defaultValue: () -> V): V =
            (holderMap.getOrPut(clazz, defaultValue) as V)

    fun <V : Any> set(value: V) {
        holderMap[value::class] = value
    }

    fun <V : Any> clear(clazz: KClass<V>) {
        holderMap.remove(clazz)
    }
}