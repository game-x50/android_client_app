package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.dto.InitAppConfig
import kotlin.reflect.KClass

//todo: move to separate module with IBaseInjector
interface InjectorHolder {

    val initAppConfig: InitAppConfig

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