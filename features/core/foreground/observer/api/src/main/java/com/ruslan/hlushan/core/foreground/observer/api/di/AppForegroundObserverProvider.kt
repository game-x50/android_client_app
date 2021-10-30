package com.ruslan.hlushan.core.foreground.observer.api.di

import com.ruslan.hlushan.core.foreground.observer.api.AppForegroundObserver

interface AppForegroundObserverProvider {

    fun provideAppForegroundObserver(): AppForegroundObserver
}