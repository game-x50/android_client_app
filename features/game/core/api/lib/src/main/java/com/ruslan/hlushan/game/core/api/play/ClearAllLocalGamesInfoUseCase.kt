package com.ruslan.hlushan.game.core.api.play

import io.reactivex.Completable

//TODO: redo to do on logo out use case with list of usecases provided in final logout
interface ClearAllLocalGamesInfoUseCase {

    fun clearAllLocalGamesInfo(): Completable
}