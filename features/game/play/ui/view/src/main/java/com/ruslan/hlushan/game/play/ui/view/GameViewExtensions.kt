package com.ruslan.hlushan.game.play.ui.view

import android.graphics.RectF
import com.ruslan.hlushan.game.play.api.UndoButton

internal fun UndoButton.contains(x: Float, y: Float): Boolean =
        RectF(this.leftX, this.topY, this.rightX, this.bottomY)
                .contains(x, y)