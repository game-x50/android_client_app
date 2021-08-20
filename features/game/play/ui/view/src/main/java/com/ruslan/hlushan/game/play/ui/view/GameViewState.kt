package com.ruslan.hlushan.game.play.ui.view

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.ruslan.hlushan.android.extensions.parcelableCreator
import com.ruslan.hlushan.android.extensions.readParcelableSafety
import com.ruslan.hlushan.game.play.ui.view.dto.GameStateParcelable

internal class GameViewState : View.BaseSavedState {

    val gameState: GameStateParcelable

    constructor(superState: Parcelable?, gameState: GameStateParcelable) : super(superState) {
        this.gameState = gameState
    }

    constructor(source: Parcel) : super(source) {
        gameState = source.readParcelableSafety()!!
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeParcelable(gameState, flags)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator { GameViewState(it) }
    }
}