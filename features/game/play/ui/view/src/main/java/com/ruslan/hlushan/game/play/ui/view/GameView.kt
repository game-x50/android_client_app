package com.ruslan.hlushan.game.play.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.ruslan.hlushan.android.extensions.colorAttributeValue
import com.ruslan.hlushan.core.extensions.ifNotNull
import com.ruslan.hlushan.core.thread.UiMainThread
import com.ruslan.hlushan.core.value.holder.MutableValueHolder
import com.ruslan.hlushan.game.api.play.dto.GameSize
import com.ruslan.hlushan.game.api.play.dto.GameState
import com.ruslan.hlushan.game.play.api.GameViewViewModel
import com.ruslan.hlushan.game.play.api.IGetGameViewState
import com.ruslan.hlushan.game.play.api.UndoButton
import com.ruslan.hlushan.game.play.api.calculations.GameGrid
import com.ruslan.hlushan.game.play.api.calculations.GameViewDimensions
import com.ruslan.hlushan.game.play.api.calculations.GameViewParams
import com.ruslan.hlushan.game.play.api.calculations.notFakeItems
import com.ruslan.hlushan.game.play.api.listeners.GameFinishedListener
import com.ruslan.hlushan.game.play.api.listeners.TotalSumChangedListener
import com.ruslan.hlushan.game.play.ui.view.dto.toParcelable
import com.ruslan.hlushan.third_party.rxjava2.extensions.safetyDispose
import io.reactivex.disposables.Disposable

private const val MAX_CLICK_DIFF_MILLIS = 200

@SuppressWarnings("TooManyFunctions", "TooGenericExceptionCaught")
@UiMainThread
class GameView
@JvmOverloads
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IGetGameViewState {

    private var gameViewDimensions: GameViewDimensions = GameViewDimensions.createDefault()
    private val gameGrid = GameGrid(mutableListOf(), mutableListOf())

    private val undoButtonHolder = MutableValueHolder(UndoButton.createDefault())

    private val gameDrawer: GameDrawer

    private val viewModel: GameViewViewModel

    private var commandsDisposable: Disposable? = null

    val isGameFinished: Boolean get() = viewModel.isGameFinished

    init {
        @ColorInt val colorOnBackground = context.colorAttributeValue(
                com.google.android.material.R.attr.colorOnBackground
        )
        val defaultGameSize = GameSize.SMALL

        val (@ColorInt textColorOnTransparent: Int, @ColorInt gridColor: Int, gameSize: GameSize) = try {
            context.obtainStyledAttributes(attrs, R.styleable.GameView).use { ta ->
                Triple(
                        ta.getColor(R.styleable.GameView_textColorOnTransparent, colorOnBackground),
                        ta.getColor(R.styleable.GameView_gridColor, colorOnBackground),
                        GameSize.fromCountRowsAndColumns(
                                ta.getInt(R.styleable.GameView_gameSize, defaultGameSize.countRowsAndColumns)
                        )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Triple(colorOnBackground, colorOnBackground, defaultGameSize)
        }

        gameDrawer = GameDrawer(
                textColorOnTransparent = textColorOnTransparent,
                gridColor = gridColor
        )

        viewModel = GameViewViewModel(
                initParams = GameViewParams(
                        gameSize = gameSize,
                        countNewElements = gameSize.defaultNewItemsCount
                )
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        cancelCommandsDisposable()
        commandsDisposable = viewModel.observeCommands()
                .subscribe { command -> handleCommand(command) }
    }

    override fun onDetachedFromWindow() {
        viewModel.onViewDetached()
        cancelCommandsDisposable()

        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = GameViewDrawingCalculator.calculateGameViewWith(widthMeasureSpec, viewModel.gameViewParams)
        val viewHeight = GameViewDrawingCalculator.calculateGameViewHeight(heightMeasureSpec)

        gameViewDimensions = GameViewDrawingCalculator.measureViewSize(
                viewHeight,
                viewWidth,
                viewModel.gameViewParams,
                gameDrawer
        )

        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        GameViewDrawingCalculator.fillGameViewTable(
                itemsMatrix = viewModel.itemsMatrix,
                gameViewDimensions = gameViewDimensions,
                gameViewParams = viewModel.gameViewParams,
                tableGridLines = gameGrid.tableGridLines
        )
        GameViewDrawingCalculator.fillGameViewNewItems(
                newItemsSet = viewModel.newItemsSet,
                gameViewDimensions = gameViewDimensions,
                gameViewParams = viewModel.gameViewParams,
                newElementsGrid = gameGrid.newElementsGrid
        )
        GameViewDrawingCalculator.fillUndoButton(gameViewDimensions, undoButtonHolder)
        gameDrawer.onLayout(undoButtonHolder.value)
    }

    override fun onDraw(canvas: Canvas) {
        drawSums(canvas)
        drawGrid(canvas)
        drawItems(canvas)
        drawUndoButton(canvas)
    }

    override fun copyGameState(): GameState = viewModel.copyGameState()

    fun setGameFinishedListener(gameFinishedListener: GameFinishedListener?) =
            viewModel.setGameFinishedListener(gameFinishedListener)

    fun setTotalSumChangedListener(totalSumChangedListener: TotalSumChangedListener?) =
            viewModel.setTotalSumChangedListener(totalSumChangedListener)

    fun setNewSize(gameSize: GameSize) = viewModel.initWithNewSize(gameSize)

    fun setGameViewState(gameState: GameState) = viewModel.initWithParams(gameState)

    private fun drawGrid(canvas: Canvas) = gameDrawer.drawGameGrid(gameGrid, canvas)

    private fun drawItems(canvas: Canvas) {
        drawInsertedItems(canvas)
        drawNewItems(canvas)
        drawMovedItem(canvas)
    }

    private fun drawUndoButton(canvas: Canvas) {
        if (viewModel.canUndo) {
            gameDrawer.drawUndoButton(undoButtonHolder.value, canvas)
        }
    }

    private fun drawInsertedItems(canvas: Canvas) =
            viewModel.itemsMatrix.notFakeItems
                    .forEach { area -> gameDrawer.drawRectangleArea(area, canvas) }

    private fun drawSums(canvas: Canvas) {
        viewModel.itemsMatrix.rowSums
                .forEach { area -> gameDrawer.drawRectangleArea(area, canvas) }

        viewModel.itemsMatrix.columnSums
                .forEach { area -> gameDrawer.drawRectangleArea(area, canvas) }

        viewModel.itemsMatrix.rectanglesSums
                .forEach { area -> gameDrawer.drawRectangleArea(area, canvas) }
    }

    private fun drawNewItems(canvas: Canvas) =
            viewModel.newItemsSet.filter { area -> !area.isFake }
                    .forEach { area -> gameDrawer.drawRectangleArea(area, canvas) }

    private fun drawMovedItem(canvas: Canvas) {
        ifNotNull(viewModel.movedItem) { moved ->
            gameDrawer.drawRectangleArea(moved, canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var handled = false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN       -> {
                handleActionDown(event)
                handled = true
            }
            MotionEvent.ACTION_MOVE       -> {
                handleActionMove(event)
                handled = true
            }
            MotionEvent.ACTION_UP         -> {
                handleActionUp(event)
                handled = true
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> {
                handled = true
            }
            else                          -> Unit // NOP
        }

        return (super.onTouchEvent(event) || handled)
    }

    private fun handleActionDown(event: MotionEvent) {
        val xTouch: Float = event.getX(0)
        val yTouch: Float = event.getY(0)
        viewModel.onActionDown(xTouch, yTouch)
    }

    private fun handleActionMove(event: MotionEvent) {
        val actionIndex: Int = event.actionIndex
        val xTouch: Float = event.getX(actionIndex)
        val yTouch: Float = event.getY(actionIndex)
        viewModel.onActionMove(xTouch, yTouch)
    }

    private fun handleActionUp(event: MotionEvent) {
        val actionIndex: Int = event.actionIndex
        val xTouch: Float = event.getX(actionIndex)
        val yTouch: Float = event.getY(actionIndex)
        if (((event.eventTime - event.downTime) < MAX_CLICK_DIFF_MILLIS)
            && undoButtonHolder.value.contains(x = xTouch, y = yTouch)) {
            viewModel.undo()
        } else {
            viewModel.onActionUp()
        }
    }

    private fun cancelCommandsDisposable() {
        commandsDisposable?.safetyDispose()
        commandsDisposable = null
    }

    private fun handleCommand(command: GameViewViewModel.Command) =
            when (command) {
                is GameViewViewModel.Command.RecalculateViewSizes -> requestLayout()
                is GameViewViewModel.Command.RedrawView           -> invalidate()
            }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return GameViewState(superState, copyGameState().toParcelable())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val gameViewState = (state as? GameViewState)
        if (state is BaseSavedState) {
            super.onRestoreInstanceState(state.superState)
        }
        if (gameViewState != null) {
            viewModel.onRestoreInstanceState(gameViewState.gameState.toOriginal())
        }
    }
}