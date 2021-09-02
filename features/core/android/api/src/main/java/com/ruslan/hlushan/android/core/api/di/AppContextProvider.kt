package com.ruslan.hlushan.android.core.api.di

import android.content.Context

interface AppContextProvider {

    fun provideAppContext(): Context
}