package com.ruslan.hlushan.core.error.di

import com.ruslan.hlushan.core.error.CompositeUserErrorMapper

interface UserErrorMapperProvider {

    fun provideUserErrorMapper(): CompositeUserErrorMapper
}