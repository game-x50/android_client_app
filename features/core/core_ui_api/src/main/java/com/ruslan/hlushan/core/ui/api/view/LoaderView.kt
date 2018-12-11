package com.ruslan.hlushan.core.ui.api.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import com.ruslan.hlushan.android.extensions.applyGravity
import com.ruslan.hlushan.android.extensions.containsInclusive
import com.ruslan.hlushan.android.extensions.copy
import com.ruslan.hlushan.android.extensions.diagonal
import com.ruslan.hlushan.android.extensions.endRightPadding
import com.ruslan.hlushan.android.extensions.startLeftPadding
import com.ruslan.hlushan.core.ui.api.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.tan

private const val UNSPECIFIED_VALUE: Int = -1

private const val DEFAULT_CIRCLES_COUNT: Int = 5
private const val DEFAULT_CIRCLES_RADIUS: Float = 50.toFloat()
private const val DEFAULT_CIRCLES_UPDATE_INTERVAL_MILLIS: Int = (1000 / 60)
private const val DEFAULT_STEP_PER_INTERVAL: Float = 50.toFloat()

class LoaderView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var angleDegrees: Double = 0.toDouble()

    private var stepPerInterval: Float = DEFAULT_STEP_PER_INTERVAL

    private var circlesRadius: Float = DEFAULT_CIRCLES_RADIUS
    private var circlesCount: Int = DEFAULT_CIRCLES_COUNT
    private var updateIntervalMillis: Int = DEFAULT_CIRCLES_UPDATE_INTERVAL_MILLIS

    @ColorInt
    private var circlesColor: Int = Color.BLACK
        set(newValue) {
            field = newValue
            circlesPaint.color = newValue
        }

    private var contentGravity: Int = Gravity.CENTER

    private var availableLine: Int = UNSPECIFIED_VALUE

    private var availableArea = RectF(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())

    private val circlesDiameter: Float get() = (2 * circlesRadius)
    private val totalCirclesLine: Float get() = (circlesDiameter * circlesCount)

    private val circles: MutableList<Circle> = mutableListOf()

    private val circlesPaint = Paint().apply {
        isAntiAlias = true
        color = circlesColor
    }

    private var currentMovingIndex = 0
    private var moveForward = false

    private val updateAction = Runnable {
        circles.getOrNull(currentMovingIndex)?.let {
            if (moveForward) {
                moveForward(it)
            } else {
                moveBack(it)
            }

            invalidate()
        }
    }

    private val textView: TextView = TextView(context).apply {
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                (Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL))
        gravity = Gravity.CENTER
    }

    private var textSize = 0.toFloat()
        set(newValue) {
            field = newValue
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newValue)
        }

    @ColorInt
    private var textColor = UNSPECIFIED_VALUE
        set(newValue) {
            field = newValue
            if (newValue != UNSPECIFIED_VALUE) {
                textView.setTextColor(newValue)
            }
        }
    private var text: CharSequence? = ""
        set(newValue) {
            field = newValue
            textView.text = newValue
        }

    init {
        initTextView()
        initParams(context, attrs)
        initDefaults(context)
    }

    private fun initTextView() {
        this@LoaderView.textSize = textView.textSize
        addView(textView)
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun initParams(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            var typedArray: TypedArray? = null
            try {
                typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoaderView)
                contentGravity = typedArray.getInt(R.styleable.LoaderView_content_gravity, contentGravity)
                availableLine = typedArray.getDimensionPixelSize(R.styleable.LoaderView_availableLine, availableLine)
                circlesRadius = typedArray.getDimensionPixelSize(R.styleable.LoaderView_circlesRadius, circlesRadius.toInt()).toFloat()
                circlesCount = typedArray.getInteger(R.styleable.LoaderView_circlesCount, circlesCount)
                circlesColor = typedArray.getColor(R.styleable.LoaderView_circlesColor, circlesColor)
                stepPerInterval = typedArray.getDimensionPixelSize(R.styleable.LoaderView_stepPerInterval, stepPerInterval.toInt()).toFloat()
                updateIntervalMillis = typedArray.getInteger(R.styleable.LoaderView_updateIntervalMillis, updateIntervalMillis)
                angleDegrees = typedArray.getInteger(R.styleable.LoaderView_angleDegrees, angleDegrees.toInt()).toDouble()

                textSize = typedArray.getDimensionPixelSize(R.styleable.LoaderView_textSize, textSize.toInt()).toFloat()
                textColor = typedArray.getColor(R.styleable.LoaderView_textColor, textColor)
                text = typedArray.getString(R.styleable.LoaderView_text) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                typedArray?.recycle()
            }
        }
    }

    private fun initDefaults(context: Context) {
        setWillNotDraw(false)

        @SuppressLint("ObsoleteSdkInt")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = context.resources.getDimension(R.dimen.default_1_2_padding)
        }
        isFocusable = true
        isClickable = true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updateCircles()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        circles.forEach { singleCircle ->
            canvas?.drawCircle(singleCircle.centerX, singleCircle.centerY, circlesRadius, circlesPaint)
        }

        waitForNewStep()
    }

    override fun onDetachedFromWindow() {
        closeNextStep()
        super.onDetachedFromWindow()
    }

    override fun setVisibility(newVisibility: Int) {
        val oldVisibility = visibility
        super.setVisibility(newVisibility)

        val changed = (oldVisibility != visibility)
        when {
            (changed && isVisible)  -> updateCircles()
            (changed && !isVisible) -> closeNextStep()
        }
    }

    private fun Float.toX(abs: Boolean = true): Float {
        val result = (this * cos(Math.toRadians(angleDegrees)))
        return (if (abs) {
            abs(result)
        } else {
            result
        }).toFloat()
    }

    private fun Float.toY(abs: Boolean = true): Float {
        val result = (this * sin(Math.toRadians(angleDegrees)))
        return (if (abs) {
            abs(result)
        } else {
            result
        }).toFloat()
    }

    private fun RectF.firstPoint(): PointF =
            PointF((availableArea.centerX() - (diagonal / 2).toX(abs = false)),
                   (availableArea.centerY() + (diagonal / 2).toY(abs = false)))

    private fun RectF.lastPoint(): PointF =
            PointF((availableArea.centerX() + (diagonal / 2).toX(abs = false)),
                   (availableArea.centerY() - (diagonal / 2).toY(abs = false)))

    private fun updateCircles() {
        recalculateAvailableArea()
        updateStartCirclesPositionsWithAvailableArea()
    }

    private fun recalculateAvailableArea() {
        val fullAvailableForDrawingArea = RectF((startLeftPadding + circlesRadius),
                                                paddingTop.toFloat(),
                                                (width - endRightPadding - circlesRadius),
                                                (height - paddingBottom - circlesRadius))

        val frameHeightWithCirclesRadius: Float = ((if (textView.text?.isNotEmpty() == true) textView.height else 0) + circlesRadius)

        val fullAvailableForCirclesArea = fullAvailableForDrawingArea.copy(topY = (fullAvailableForDrawingArea.top + frameHeightWithCirclesRadius))

        val tangensFloat = tan(Math.toRadians(angleDegrees)).toFloat()

        val yC = fullAvailableForCirclesArea.centerY()
        val xC = fullAvailableForCirclesArea.centerX()

        val y0 = (yC + (xC * tangensFloat))
        val x0 = (xC + (yC / tangensFloat))

        val (resultWidth, resultHeight) = when {
            (availableLine != UNSPECIFIED_VALUE)                                           -> {
                availableLine.toFloat().toX() to availableLine.toFloat().toY()
            }
            (x0 !in (fullAvailableForCirclesArea.left..fullAvailableForCirclesArea.right)) -> {
                val w = fullAvailableForCirclesArea.width()
                w to (w * abs(tangensFloat))
            }
            (y0 !in (fullAvailableForCirclesArea.top..fullAvailableForCirclesArea.bottom)) -> {
                val h = fullAvailableForCirclesArea.height()
                (h / abs(tangensFloat)) to h
            }
            else                                                                           -> {
                fullAvailableForCirclesArea.width() to fullAvailableForCirclesArea.height()
            }
        }

        val availableAreaWithFrame = applyGravity(contentGravity,
                                                  resultWidth,
                                                  (resultHeight + frameHeightWithCirclesRadius),
                                                  fullAvailableForDrawingArea)
        availableArea = availableAreaWithFrame.copy(topY = (availableAreaWithFrame.top + frameHeightWithCirclesRadius))

        val bottomMargin = ((this@LoaderView.height - availableArea.top) + circlesRadius).toInt()
        if (bottomMargin != (textView.layoutParams as? FrameLayout.LayoutParams)?.bottomMargin) {
            textView.layoutParams = (textView.layoutParams as? FrameLayout.LayoutParams)
                    ?.also { p -> p.setMargins(0, 0, 0, bottomMargin) }
        }
    }

    private fun updateStartCirclesPositionsWithAvailableArea() {
        circles.clear()

        if (availableArea.diagonal >= totalCirclesLine + stepPerInterval) {

            val diameterX = circlesDiameter.toX(abs = false)
            val diameterY = circlesDiameter.toY(abs = false)

            var movingCenterX = availableArea.firstPoint().x
            var movingCenterY = availableArea.firstPoint().y

            repeat(circlesCount) {
                circles.add(Circle(movingCenterX, movingCenterY))
                movingCenterX += diameterX
                movingCenterY -= diameterY
            }
        }
    }

    private fun moveBack(old: Circle) {
        val newCenter = PointF((old.centerX - stepPerInterval.toX(abs = false)),
                               (old.centerY + stepPerInterval.toY(abs = false)))

        val firstPoint = availableArea.firstPoint()

        val minimalCenter = circles.getOrNull(currentMovingIndex - 1)
                                    ?.let { singleCircle ->
                                        PointF(
                                                (singleCircle.centerX + circlesDiameter.toX(abs = false)),
                                                (singleCircle.centerY - circlesDiameter.toY(abs = false))
                                        )
                                    }
                            ?: firstPoint

        val unAvailableArea = RectF(
                min(firstPoint.x, minimalCenter.x),
                min(firstPoint.y, minimalCenter.y),
                max(firstPoint.x, minimalCenter.x),
                max(firstPoint.y, minimalCenter.y)
        )

        if (unAvailableArea.containsInclusive(newCenter) || !availableArea.containsInclusive(newCenter)) {
            circles[currentMovingIndex] = Circle(minimalCenter.x, minimalCenter.y)
            if (currentMovingIndex + 1 <= circles.lastIndex) {
                currentMovingIndex++
            } else {
                moveForward = true
            }
        } else {
            circles[currentMovingIndex] = Circle(newCenter.x, newCenter.y)
        }
    }

    private fun moveForward(old: Circle) {
        val newCenter = PointF((old.centerX + stepPerInterval.toX(abs = false)),
                               (old.centerY - stepPerInterval.toY(abs = false)))

        val lastPoint = availableArea.lastPoint()

        val maximalCenter = circles.getOrNull(currentMovingIndex + 1)
                                    ?.let { singleCircle ->
                                        PointF(
                                                (singleCircle.centerX - circlesDiameter.toX(abs = false)),
                                                (singleCircle.centerY + circlesDiameter.toY(abs = false))
                                        )
                                    }
                            ?: availableArea.lastPoint()

        val unAvailableArea = RectF(
                min(lastPoint.x, maximalCenter.x),
                min(lastPoint.y, maximalCenter.y),
                max(lastPoint.x, maximalCenter.x),
                max(lastPoint.y, maximalCenter.y)
        )

        if (unAvailableArea.containsInclusive(newCenter) || !availableArea.containsInclusive(newCenter)) {
            circles[currentMovingIndex] = Circle(maximalCenter.x, maximalCenter.y)
            if (currentMovingIndex - 1 >= 0) {
                currentMovingIndex--
            } else {
                moveForward = false
            }
        } else {
            circles[currentMovingIndex] = Circle(newCenter.x, newCenter.y)
        }
    }

    private fun waitForNewStep() {
        closeNextStep()
        handler?.postDelayed(updateAction, updateIntervalMillis.toLong())
    }

    private fun closeNextStep() {
        handler?.removeCallbacks(updateAction)
    }
}

private data class Circle(val centerX: Float, val centerY: Float)