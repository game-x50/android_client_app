package com.ruslan.hlushan.work.manager.extensions

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ruslan.hlushan.work.manager.extensions.di.WorkerFactoryProvider

class CompositeWorkerFactory(
        private val factoryProviders: List<WorkerFactoryProvider>
) : WorkerFactory() {

    override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
    ): ListenableWorker? =
            factoryProviders
                    .asSequence()
                    .map { singleFactoryProvider ->
                        singleFactoryProvider.provideWorkerFactory()
                                .createWorker(appContext, workerClassName, workerParameters)
                    }
                    .filterNotNull()
                    .firstOrNull()
}