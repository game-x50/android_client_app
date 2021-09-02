package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.managers.CompositeUserErrorMapper

interface UserErrorMapperProvider {

    fun provideUserErrorMapper(): CompositeUserErrorMapper
}