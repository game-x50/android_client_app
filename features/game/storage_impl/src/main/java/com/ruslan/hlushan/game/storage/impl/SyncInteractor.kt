package com.ruslan.hlushan.game.storage.impl

import io.reactivex.Completable

internal interface SyncInteractor {

    fun sync(): Completable
}