package com.ruslan.hlushan.game.storage.impl.local.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.core.api.play.dto.MatrixAndNewItemsState

private const val EMPTY_VALUE = -1

@Entity(
        tableName = MatrixAndNewItemsStateDb.TABLE_NAME,
        foreignKeys = [
            ForeignKey(
                    entity = GameStateDb::class,
                    parentColumns = [GameStateDb.RECORD_ID],
                    childColumns = [GameStateDb.RECORD_ID],
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ],
        primaryKeys = [
            GameStateDb.RECORD_ID,
            MatrixAndNewItemsStateDb.MATRIX_INDEX
        ],
        indices = [
            Index(
                    name = "MatrixAndNewItemsStateTableRecordIdIndex",
                    unique = false,
                    value = [GameStateDb.RECORD_ID]
            )
        ]
)
internal data class MatrixAndNewItemsStateDb(
        @ColumnInfo(name = GameStateDb.RECORD_ID)
        val recordId: Long,
        @ColumnInfo(name = MATRIX_INDEX)
        val matrixIndex: Int,
        val matrix: List<Int>,
        val newItems: List<Int>,
        val totalSum: Int
) {

    @SuppressWarnings("ClassOrdering")
    companion object {
        const val TABLE_NAME = "matrix_and_new_items_state"
        const val MATRIX_INDEX = "matrix_index"
    }

    fun fromDbModel(gameSize: GameSize): MatrixAndNewItemsState {
        val numbers = this.matrix.map { number ->
            if (number != EMPTY_VALUE) {
                number
            } else {
                null
            }
        }

        val immutableNumbersMatrix = ImmutableNumbersMatrix(
                numbers = numbers,
                gameSize = gameSize,
                totalSum = this.totalSum
        )

        return MatrixAndNewItemsState(
                immutableNumbersMatrix = immutableNumbersMatrix,
                newItems = this.newItems
        )
    }
}

internal fun MatrixAndNewItemsState.toDbModel(recordId: Long, matrixIndex: Int): MatrixAndNewItemsStateDb =
        MatrixAndNewItemsStateDb(
                recordId = recordId,
                matrixIndex = matrixIndex,
                matrix = this.immutableNumbersMatrix.numbers
                        .map { number -> (number ?: EMPTY_VALUE) },
                newItems = this.newItems,
                totalSum = this.immutableNumbersMatrix.totalSum
        )