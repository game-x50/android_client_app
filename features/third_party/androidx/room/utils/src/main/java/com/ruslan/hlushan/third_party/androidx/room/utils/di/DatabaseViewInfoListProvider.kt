@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.room.utils.di

import com.ruslan.hlushan.third_party.androidx.room.utils.DatabaseViewInfo

interface DatabaseViewInfoListProvider {

    fun provideDatabases(): List<DatabaseViewInfo>
}