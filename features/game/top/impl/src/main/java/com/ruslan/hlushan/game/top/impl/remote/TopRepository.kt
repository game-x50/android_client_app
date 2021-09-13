package com.ruslan.hlushan.game.top.impl.remote

import com.ruslan.hlushan.core.api.managers.SchedulersManager
import com.ruslan.hlushan.core.logger.api.AppLogger
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.top.dto.GamePreviewWithUserDetails
import com.ruslan.hlushan.game.top.impl.remote.dto.RemoteApiGamePreviewWithUserDetails
import com.ruslan.hlushan.game.top.impl.remote.dto.toEntity
import io.reactivex.Single
import javax.inject.Inject

internal class TopRepository @Inject constructor(
        private val topRemoteHttpApi: TopRemoteHttpApi,
        private val schedulersManager: SchedulersManager,
        private val appLogger: AppLogger
) {

    fun getTopGamesFor(size: GameSize): Single<List<GamePreviewWithUserDetails>> =
            topRemoteHttpApi.getBestGames(countRowsAndColumns = size.countRowsAndColumns)
                    .map { list -> list.map(RemoteApiGamePreviewWithUserDetails::toEntity) }
                    .doOnError { error -> appLogger.log(this@TopRepository, "getTopGamesFor: ERROR", error) }
                    .subscribeOn(schedulersManager.io)
}