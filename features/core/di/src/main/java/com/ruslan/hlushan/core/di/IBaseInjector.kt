package com.ruslan.hlushan.core.di

interface IBaseInjector

inline fun <reified T : Any> IBaseInjector.asType() = (this as T)