package com.ruslan.hlushan.game.top.impl

import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.top.TopInteractor
import com.ruslan.hlushan.game.core.api.top.dto.GamePreviewWithUserDetails
import com.ruslan.hlushan.game.top.impl.remote.TopRepository
import io.reactivex.Single
import javax.inject.Inject

internal class TopInteractorImpl @Inject constructor(
        private val topRepository: TopRepository
) : TopInteractor {

    override fun getTopGamesFor(size: GameSize): Single<List<GamePreviewWithUserDetails>> =
            topRepository.getTopGamesFor(size)
}