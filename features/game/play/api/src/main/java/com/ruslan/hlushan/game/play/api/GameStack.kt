package com.ruslan.hlushan.game.play.api

import androidx.annotation.IntRange
import androidx.annotation.VisibleForTesting
import com.ruslan.hlushan.core.extensions.clearAndAddAll
import com.ruslan.hlushan.core.extensions.removeLast
import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState

class GameStack(@IntRange(from = 1) limit: Int = DEFAULT_LIMIT) {

    @IntRange(from = 1)
    @VisibleForTesting
    val limit: Int = maxOf(1, limit)

    private val stack = ArrayList<MatrixAndNewItemsState>()

    val canUndo: Boolean get() = stack.isNotEmpty()

    fun undo(): MatrixAndNewItemsState? = stack.removeLast()

    fun setLast(state: MatrixAndNewItemsState) {
        if (stack.size >= limit) {
            stack.removeAt(0)
        }

        stack.add(state)
    }

    fun copyFrom(undoStates: List<MatrixAndNewItemsState>) {
        stack.clearAndAddAll(undoStates.takeLast(limit))
    }

    fun copyAsList(): List<MatrixAndNewItemsState> = stack.toList()

    companion object {
        const val DEFAULT_LIMIT = 10
        fun empty() = GameStack()
    }
}