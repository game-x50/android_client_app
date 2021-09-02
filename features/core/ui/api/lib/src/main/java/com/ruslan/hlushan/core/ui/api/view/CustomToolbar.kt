package com.ruslan.hlushan.core.ui.api.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.view.isVisible
import com.ruslan.hlushan.core.ui.api.R

/**
 * Created by Ruslan on 04.04.2017.
 */

@SuppressWarnings("TooManyFunctions")
class CustomToolbar
@JvmOverloads
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var linearLayoutMain: LinearLayout? = null
    private var rightImage: ImageView? = null
    private var leftImage: ImageView? = null
    private var tvLabel: TextView? = null

    private var labelPosition: Int = Gravity.CENTER_VERTICAL
    private var labelText: CharSequence? = null
    private var colorLabelText: Int = 0
    private var labelTextSize: Int = 0
    private var labelTextStyle: Int = 0
    private var labelTextAllCaps: Boolean = false

    private var leftDrawable: Drawable? = null
    private var rightDrawable: Drawable? = null
    private var leftImagePadding: Int = 0
    private var rightImagePadding: Int = 0

    private var backgroundDrawable: Drawable? = null

    private var onRightImageClickListener: (() -> Unit)? = null
    private var onLeftImageClickListener: (() -> Unit)? = null
    private var onLabelClickListener: (() -> Unit)? = null

    init {
        View.inflate(getContext(), R.layout.custom_toolbar, this)

        initViews()
        setToolbarParams(context, attrs)
        setToolbarViewByParams()
        setOnClickListeners()
    }

    private fun initViews() {
        linearLayoutMain = findViewById<LinearLayout>(R.id.customToolbar_mainLinear)
        tvLabel = findViewById<TextView>(R.id.customToolbar_label)
        rightImage = findViewById<ImageView>(R.id.customToolbar_rightImage)
        leftImage = findViewById<ImageView>(R.id.customToolbar_leftImage)
    }

    @SuppressWarnings("NestedBlockDepth", "TooGenericExceptionCaught")
    private fun setToolbarParams(context: Context, attrs: AttributeSet?) {
        attrs?.let { itAttrs ->
            var typedArray: TypedArray? = null
            try {
                typedArray = context.obtainStyledAttributes(itAttrs, R.styleable.CustomToolbar)

                labelPosition = typedArray.getInt(R.styleable.CustomToolbar_labelPosition, Gravity.CENTER_VERTICAL)
                labelText = typedArray.getString(R.styleable.CustomToolbar_labelText)
                @SuppressWarnings("MagicNumber")
                labelTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomToolbar_labelTextSize, 12)
                labelTextStyle = typedArray.getInt(R.styleable.CustomToolbar_labelTextStyle, 0)
                labelTextAllCaps = typedArray.getBoolean(R.styleable.CustomToolbar_labelTextAllCaps, false)
                colorLabelText = typedArray.getColor(R.styleable.CustomToolbar_labelTextColor, Color.WHITE)

                leftDrawable = typedArray.getDrawable(R.styleable.CustomToolbar_leftDrawRes)
                rightDrawable = typedArray.getDrawable(R.styleable.CustomToolbar_rightDrawRes)
                leftImagePadding = typedArray.getDimensionPixelSize(R.styleable.CustomToolbar_leftImagePadding, 0)
                rightImagePadding = typedArray.getDimensionPixelSize(R.styleable.CustomToolbar_rightImagePadding, 0)
                val imagesPadding = typedArray.getDimensionPixelSize(R.styleable.CustomToolbar_imagesPadding, 0)
                if (imagesPadding != 0) {
                    leftImagePadding = imagesPadding
                    rightImagePadding = imagesPadding
                }

                backgroundDrawable = typedArray.getDrawable(R.styleable.CustomToolbar_toolbarBackgroundDrawRes)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                typedArray?.recycle()
            }
        }
    }

    private fun setToolbarViewByParams() {
        setLabel()
        setImages()
        setBackground()
    }

    private fun setLabel() {
        tvLabel?.let { tv ->
            if (labelText != null) {
                tv.text = labelText
            }
            tv.setTypeface(tv.typeface, labelTextStyle)
            tv.setTextColor(colorLabelText)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize.toFloat())
            tv.gravity = labelPosition
            tv.isAllCaps = labelTextAllCaps
            tv.isSelected = true
        }
    }

    private fun setImages() {
        setupLeftImage()
        setupRightImage()
    }

    private fun setupLeftImage() {
        setupLeftImageDrawable()
        setLeftImagePadding(leftImagePadding)
    }

    private fun setupRightImage() {
        setupRightImageDrawable()
        setRightImagePadding(rightImagePadding)
    }

    private fun setupRightImageDrawable() {
        if (rightDrawable != null) {
            rightImage?.setImageDrawable(rightDrawable)
            rightImage?.isVisible = true
        } else {
            rightImage?.isVisible = false
        }
    }

    private fun setupLeftImageDrawable() {
        if (leftDrawable != null) {
            leftImage?.setImageDrawable(leftDrawable)
            leftImage?.isVisible = true
        } else {
            leftImage?.isVisible = false
        }
    }

    private fun setBackground() {
        linearLayoutMain?.background = backgroundDrawable
    }

    private fun setOnClickListeners() {
        rightImage?.setOnClickListener { v -> onRightImageClickListener?.invoke() }
        leftImage?.setOnClickListener { v -> onLeftImageClickListener?.invoke() }
        tvLabel?.setOnClickListener { v -> onLabelClickListener?.invoke() }
    }

    fun setLabelPosition(labelPosition: Int) {
        this.labelPosition = labelPosition
        setLabel()
    }

    fun setLabelText(labelText: CharSequence?) {
        this.labelText = labelText
        setLabel()
    }

    fun setColorLabelText(colorLabelText: Int) {
        this.colorLabelText = colorLabelText
        setLabel()
    }

    fun setLabelTextSize(labelTextSize: Int) {
        this.labelTextSize = labelTextSize
        setLabel()
    }

    fun setLabelTextStyle(labelTextStyle: Int) {
        this.labelTextStyle = labelTextStyle
        setLabel()
    }

    fun setLabelTextAllCaps(labelTextAllCaps: Boolean) {
        this.labelTextAllCaps = labelTextAllCaps
        setLabel()
    }

    fun setLeftDrawable(leftDrawable: Drawable) {
        this.leftDrawable = leftDrawable
        setupLeftImageDrawable()
    }

    fun setRightDrawable(rightDrawable: Drawable?) {
        this.rightDrawable = rightDrawable
        setupRightImageDrawable()
    }

    fun setLeftImagePadding(leftImagePadding: Int) {
        this.leftImagePadding = leftImagePadding
        leftImage?.setPadding(leftImagePadding, leftImagePadding, leftImagePadding, leftImagePadding)
    }

    fun setRightImagePadding(rightImagePadding: Int) {
        this.rightImagePadding = rightImagePadding
        rightImage?.setPadding(rightImagePadding, rightImagePadding, rightImagePadding, rightImagePadding)
    }

    fun setImagesPadding(imagesPadding: Int) {
        setLeftImagePadding(imagesPadding)
        setRightImagePadding(imagesPadding)
    }

    override fun setBackgroundDrawable(backgroundDrawable: Drawable) {
        this.backgroundDrawable = backgroundDrawable
        setBackground()
    }

    fun setOnRightImageClickListener(onRightImageClickListener: (() -> Unit)) {
        this.onRightImageClickListener = onRightImageClickListener
    }

    fun setOnLeftImageClickListener(onLeftImageClickListener: (() -> Unit)) {
        this.onLeftImageClickListener = onLeftImageClickListener
    }

    fun setOnLabelClickListener(onLabelClickListener: (() -> Unit)) {
        this.onLabelClickListener = onLabelClickListener
    }

    fun getLabel(): CharSequence = tvLabel?.text ?: ""
}