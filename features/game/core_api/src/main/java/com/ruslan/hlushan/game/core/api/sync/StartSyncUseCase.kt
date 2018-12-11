package com.ruslan.hlushan.game.core.api.sync

import io.reactivex.Observable

/**
 * @author Ruslan Hlushan on 2019-06-21
 */
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