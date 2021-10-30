package com.ruslan.hlushan.core.api.di

//todo: move to separate module with InjectorHolder
interface IBaseInjector

inline fun <reified T : Any> IBaseInjector.asType() = (this as T)