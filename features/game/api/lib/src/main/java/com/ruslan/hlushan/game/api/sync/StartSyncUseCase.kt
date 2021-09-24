package com.ruslan.hlushan.game.api.sync

import io.reactivex.Observable

interface StartSyncUseCase {

    fun observeIsSynchronizing(): Observable<Boolean>

    fun start()

    fun cancel()
}

fun StartSyncUseCase.observeSyncFinished(): Observable<Unit> =
        this.observeIsSynchronizing()
                .distinctUntilChanged { prev, current ->
                    val shouldSkip = (!prev || current)
                    shouldSkip
                }
                .skip(1)
                .map { Unit }