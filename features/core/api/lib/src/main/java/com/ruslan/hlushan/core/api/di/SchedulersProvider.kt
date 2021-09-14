package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.managers.SchedulersManager

interface SchedulersProvider {

    fun provideSchedulersManager(): SchedulersManager
}