package com.ruslan.hlushan.game.storage.impl

import android.content.Context
import com.ruslan.hlushan.game.api.sync.StartSyncUseCase
import com.ruslan.hlushan.game.storage.impl.local.LocalRecordsRepository
import com.ruslan.hlushan.game.storage.impl.workers.SyncWorker
import io.reactivex.Observable
import javax.inject.Inject

internal class StartSyncUseCaseImpl
@Inject
constructor(
        private val appContext: Context,
        private val localRecordsRepository: LocalRecordsRepository
) : StartSyncUseCase {

    override fun observeIsSynchronizing(): Observable<Boolean> =
            localRecordsRepository.observeIsSynchronizing()

    override fun start() = SyncWorker.start(appContext)

    override fun cancel() = SyncWorker.cancel(appContext)
}