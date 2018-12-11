package com.ruslan.hlushan.core.api.di

import com.ruslan.hlushan.core.api.dto.DatabaseViewInfo

interface DatabaseViewInfoListProvider {
    fun provideDatabases(): List<DatabaseViewInfo>
}