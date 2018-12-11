package com.ruslan.hlushan.game.core.api.top

import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.top.dto.GamePreviewWithUserDetails
import io.reactivex.Single

interface TopInteractor {

    fun getTopGamesFor(size: GameSize): Single<List<GamePreviewWithUserDetails>>
}