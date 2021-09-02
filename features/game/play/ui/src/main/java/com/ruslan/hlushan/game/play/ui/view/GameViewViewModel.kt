package com.ruslan.hlushan.game.play.ui.view

import com.ruslan.hlushan.core.api.utils.thread.UiMainThread
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.extensions.indexOfFirstOrNull
import com.ruslan.hlushan.game.core.api.play.dto.GameSize
import com.ruslan.hlushan.game.core.api.play.dto.GameState
import com.ruslan.hlushan.game.core.api.play.dto.ImmutableNumbersMatrix
import com.ruslan.hlushan.game.core.api.play.dto.MatrixAndNewItemsState
import com.ruslan.hlushan.game.play.ui.view.calculations.GameViewDrawingCalculator
import com.ruslan.hlushan.game.play.ui.view.calculations.GameViewParams
import com.ruslan.hlushan.game.play.ui.view.calculations.ItemsMatrix
import com.ruslan.hlushan.game.play.ui.view.calculations.countRowsAndColumns
import com.ruslan.hlushan.game.play.ui.view.calculations.isAllItemsFilled
import com.ruslan.hlushan.game.play.ui.view.calculations.toImmutableNumbersMatrix
import com.ruslan.hlushan.game.play.ui.view.calculations.updateFrom
import com.ruslan.hlushan.game.play.ui.view.listeners.GameFinishedListener
import com.ruslan.hlushan.game.play.ui.view.listeners.TotalSumChangedListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @author Ruslan Hlushan on 8/31/18.
 */
//TODO: #write_unit_tests
@SuppressWarnings("TooManyFunctions")
@UiMainThread
internal class GameViewViewModel(
        initParams: GameViewParams
) : IGetGameViewState {

    var gameViewParams: GameViewParams = initParams
        private set

    private val gameNumbersGenerator = GameNumbersGenerator(gameSize = initParams.gameSize, sum = 0)

    private val gameStack = GameStack.empty()

    private var needsRecalculateViewSizes = false

    private val commandSubject = PublishSubject.create<Command>()

    private val allItemsFilledListener: (matrix: ImmutableNumbersMatrix) -> Unit = {
        parentGameFinishedListener?.onGameFinished(copyGameState())
    }

    private val viewModelTotalSumChangedListener = TotalSumChangedListener { totalSum ->
        gameNumbersGenerator.updateState(size = gameViewParams.gameSize, totalSum = totalSum)
        parentTotalSumChangedListener?.onTotalSumChanged(totalSum)
    }

    var itemsMatrix: ItemsMatrix = createViewModelItemsMatrix()
        private set

    private var movedItemPair: MovedItemPair? = null

    private val newItems: MutableList<RectangleArea> =
            (0 until gameViewParams.countNewElements)
                    .map { position ->
                        RectangleArea.createDefault(
                                position = position,
                                number = gameNumbersGenerator.generateSingleNew(),
                                isFake = false,
                                drawBackground = true
                        )
                    }
                    .toMutableList()

    private var parentGameFinishedListener: GameFinishedListener? = null
    private var parentTotalSumChangedListener: TotalSumChangedListener? = null

    val movedItem: RectangleArea?
        get() = movedItemPair?.moved

    val newItemsSet: List<RectangleArea>
        get() = newItems

    val isGameFinished: Boolean
        get() = itemsMatrix.isAllItemsFilled

    val canUndo: Boolean
        get() = gameStack.canUndo

    fun observeCommands(): Observable<Command> = commandSubject

    fun initWithNewSize(gameSize: GameSize) {
        gameNumbersGenerator.updateState(size = gameSize)

        val newItems: List<Int> = (1..gameSize.defaultNewItemsCount)
                .map { gameNumbersGenerator.generateSingleNew() }

        val current = MatrixAndNewItemsState(
                immutableNumbersMatrix = ImmutableNumbersMatrix.emptyForSize(gameSize),
                newItems = newItems
        )
        updateGameState(gameState = GameState(current, stack = emptyList()))
    }

    fun initWithParams(gameState: GameState) = updateGameState(gameState)

    fun onViewDetached() {
        needsRecalculateViewSizes = false
    }

    override fun copyGameState(): GameState = GameState(
            current = createCurrentMatrixAndNewItemsState(),
            stack = gameStack.copyAsList()
    )

    fun setGameFinishedListener(gameFinishedListener: GameFinishedListener?) {
        this.parentGameFinishedListener = gameFinishedListener
    }

    fun setTotalSumChangedListener(totalSumChangedListener: TotalSumChangedListener?) {
        this.parentTotalSumChangedListener = totalSumChangedListener
    }

    @Suppress("MaxLineLength")
    fun onActionDown(xTouch: Float, yTouch: Float) {
        insertNewItemIntoMatrixOrReturnItToNewItems()
        ifNotNull(removeNearestNewItemForTouch(xTouch, yTouch)) { area ->
            movedItemPair = MovedItemPair(area.copy(), area.copy())
            movedItemPair?.moved?.leftX = (xTouch - (GameViewDrawingCalculator.MOVED_ITEM_COEFFICIENT_PADDING_X * area.size))
            movedItemPair?.moved?.topY = (yTouch - (GameViewDrawingCalculator.MOVED_ITEM_COEFFICIENT_PADDING_Y * area.size))
        }
        redrawAndRecalculateViewSizesIfNeeded()
    }

    fun onActionMove(xTouch: Float, yTouch: Float) {
        ifNotNull(movedItemPair?.moved) { movedArea ->
            movedArea.leftX = xTouch - (GameViewDrawingCalculator.MOVED_ITEM_COEFFICIENT_PADDING_X * movedArea.size)
            movedArea.topY = yTouch - (GameViewDrawingCalculator.MOVED_ITEM_COEFFICIENT_PADDING_Y * movedArea.size)
        }

        redrawAndRecalculateViewSizesIfNeeded()
    }

    fun onActionUp() {
        insertNewItemIntoMatrixOrReturnItToNewItems()
        redrawAndRecalculateViewSizesIfNeeded()
    }

    fun undo() {
        val undoState = gameStack.undo()
        if (undoState != null) {
            updateMatrixAndNewItems(undoState)
        }
    }

    fun onRestoreInstanceState(gameState: GameState) = updateGameState(gameState)

    private fun insertNewItemIntoMatrixOrReturnItToNewItems() {
        ifNotNull(movedItemPair) { pair ->
            val previousState = createCurrentMatrixAndNewItemsState()
            val wasInserted = itemsMatrix.replaceFakeWith(pair.moved)
            if (wasInserted) {
                gameStack.setLast(previousState)
                resortNewItemsAndAddNewGenerated(pair)
            } else {
                newItems.add(pair.original.position, pair.original)
            }
        }
        movedItemPair = null
    }

    private fun removeNearestNewItemForTouch(xTouch: Float, yTouch: Float): RectangleArea? {
        val index = newItems.indexOfFirstOrNull { newItemArea ->
            newItemArea.containsInclusive(x = xTouch, y = yTouch)
        }

        return if (index != null) {
            newItems.removeAt(index)
        } else {
            null
        }
    }

    private fun resortNewItemsAndAddNewGenerated(movedItemPair: MovedItemPair) {
        val generatedNewItems = gameNumbersGenerator.generateNew()

        newItems.add(
                movedItemPair.original.position,
                movedItemPair.original.copy(number = generatedNewItems.firstNumber)
        )

        if (generatedNewItems is GeneratedNumbers.Double) {
            gameViewParams = GameViewParams(gameViewParams.gameSize, countNewElements = (newItems.size + 1))
            needsRecalculateViewSizes = true
            newItems.add(RectangleArea.createDefault(
                    position = newItems.size,
                    number = generatedNewItems.secondNumber,
                    isFake = false,
                    drawBackground = true
            ))
        }
    }

    private fun createCurrentMatrixAndNewItemsState(): MatrixAndNewItemsState {
        val allNewItemsWithMoving = newItemsSet.toMutableList()
        ifNotNull(movedItemPair?.original) { nonNullMovedPairOriginal ->
            allNewItemsWithMoving.add(nonNullMovedPairOriginal.position, nonNullMovedPairOriginal.copy())
        }
        val matrix: ImmutableNumbersMatrix = itemsMatrix.toImmutableNumbersMatrix()
        return MatrixAndNewItemsState(matrix, allNewItemsWithMoving.map(RectangleArea::number))
    }

    private fun updateGameState(gameState: GameState) {
        gameStack.copyFrom(gameState.stack)
        updateMatrixAndNewItems(gameState.current)
    }

    private fun updateMatrixAndNewItems(matrixAndNewItemsState: MatrixAndNewItemsState) {
        gameViewParams = GameViewParams(
                matrixAndNewItemsState.immutableNumbersMatrix.gameSize,
                matrixAndNewItemsState.newItems.size
        )
        itemsMatrix = createViewModelItemsMatrix()

        itemsMatrix.updateFrom(matrixAndNewItemsState.immutableNumbersMatrix)

        newItems.clear()
        matrixAndNewItemsState.newItems.forEachIndexed { index, newItemNumber ->
            newItems.add(RectangleArea.createDefault(
                    position = index,
                    number = newItemNumber,
                    isFake = false,
                    drawBackground = true
            ))
        }

        sendCommand(command = Command.RecalculateViewSizes())
        sendCommand(command = Command.RedrawView())
        viewModelTotalSumChangedListener.onTotalSumChanged(matrixAndNewItemsState.immutableNumbersMatrix.totalSum)
    }

    private fun createViewModelItemsMatrix(): ItemsMatrix = ItemsMatrix(
            countRowsAndColumns = gameViewParams.countRowsAndColumns,
            allItemsFilledListener = allItemsFilledListener,
            totalSumChangedListener = viewModelTotalSumChangedListener
    )

    private fun redrawAndRecalculateViewSizesIfNeeded() {
        if (needsRecalculateViewSizes) {
            sendCommand(command = Command.RecalculateViewSizes())
            needsRecalculateViewSizes = false
        }

        sendCommand(command = Command.RedrawView())
    }

    private fun sendCommand(command: Command) =
            commandSubject.onNext(command)

    sealed class Command {
        class RecalculateViewSizes : Command()
        class RedrawView : Command()
    }
}

private data class MovedItemPair(
        val original: RectangleArea,
        val moved: RectangleArea
)