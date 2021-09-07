@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.work.manager.utils.di

import androidx.work.WorkerFactory

interface WorkerFactoryProvider {

    fun provideWorkerFactory(): WorkerFactory
}