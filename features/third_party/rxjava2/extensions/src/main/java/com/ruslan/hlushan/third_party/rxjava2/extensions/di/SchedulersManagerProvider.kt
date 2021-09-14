@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.rxjava2.extensions.di

import com.ruslan.hlushan.third_party.rxjava2.extensions.SchedulersManager

interface SchedulersManagerProvider {

    fun provideSchedulersManager(): SchedulersManager
}