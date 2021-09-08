package com.ruslan.hlushan.core.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.ruslan.hlushan.android.extensions.colorAttributeValue

private const val INVALID_RESOURCE_ID = 0

class MenuItemLayout
@JvmOverloads
constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mTitleTextView: TextView? = null
    private var mHintTextView: TextView? = null
    private var mIconImageView: ImageView? = null
    private var mRightIconImageView: ImageView? = null
    private var mArrowImageView: ImageView? = null

    private var mTitle: CharSequence? = null
    private var mHint: CharSequence? = null

    private var mEnableTitleAnimation = false

    init {
        init(context, attrs)
    }

    fun setTitle(title: CharSequence?) {
        mTitle = title
        setTitleTextView()
    }

    fun setHint(hint: CharSequence?) {
        mHint = hint
        setHintTextView()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_menu_item, this, true)
        if (isInEditMode) {
            return
        }
        initViews()
        initAttrs(context, attrs)
    }

    private fun initViews() {
        mTitleTextView = findViewById(R.id.tv_title)
        mHintTextView = findViewById(R.id.tv_hint)
        mIconImageView = findViewById(R.id.iv_icon)
        mRightIconImageView = findViewById(R.id.iv_right_icon)
        mArrowImageView = findViewById(R.id.iv_arrow_next)
    }

    @SuppressWarnings("ComplexMethod", "TooGenericExceptionCaught")
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            @StringRes var titleResId: Int = INVALID_RESOURCE_ID
            @StringRes var hintResId: Int = INVALID_RESOURCE_ID
            @DrawableRes var iconResId: Int = INVALID_RESOURCE_ID
            @ColorInt var titleTextColor: Int = INVALID_RESOURCE_ID
            @ColorInt var hintTextColor: Int = INVALID_RESOURCE_ID
            @ColorInt var arrowColor: Int = INVALID_RESOURCE_ID
            @DrawableRes var backgroundImageDrawableResId: Int = INVALID_RESOURCE_ID

            @ColorInt val colorSecondary = context.colorAttributeValue(
                    com.google.android.material.R.attr.colorSecondary
            )
            @ColorInt val textColor = context.colorAttributeValue(android.R.attr.textColor)

            var typedArray: TypedArray? = null
            try {
                typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuItemLayout)
                hintResId = typedArray.getResourceId(R.styleable.MenuItemLayout_item_hint, hintResId)
                titleResId = typedArray.getResourceId(R.styleable.MenuItemLayout_item_title, titleResId)
                iconResId = typedArray.getResourceId(R.styleable.MenuItemLayout_item_icon, iconResId)
                titleTextColor = typedArray.getColor(R.styleable.MenuItemLayout_titleTextColor, textColor)
                hintTextColor = typedArray.getColor(R.styleable.MenuItemLayout_hintTextColor, textColor)
                arrowColor = typedArray.getColor(R.styleable.MenuItemLayout_arrowColor, colorSecondary)
                backgroundImageDrawableResId = typedArray.getResourceId(
                        R.styleable.MenuItemLayout_backgroundImageDrawable,
                        backgroundImageDrawableResId
                )
                mEnableTitleAnimation = typedArray.getBoolean(
                        R.styleable.MenuItemLayout_enableTitleAnimation,
                        false
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                typedArray?.recycle()
            }

            if (titleResId != INVALID_RESOURCE_ID) {
                mTitle = resources.getString(titleResId)
            }
            if (hintResId != INVALID_RESOURCE_ID) {
                mHint = resources.getString(hintResId)
            }

            setTitleTextView()
            setHintTextView()

            if (iconResId != INVALID_RESOURCE_ID) {
                mIconImageView?.setImageDrawable(AppCompatResources.getDrawable(context, iconResId))
            }

            if (backgroundImageDrawableResId != INVALID_RESOURCE_ID) {
                mIconImageView?.background = AppCompatResources.getDrawable(context, backgroundImageDrawableResId)
            } else {
                mIconImageView?.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(context.colorAttributeValue(com.google.android.material.R.attr.colorPrimary))
                }
            }

            mTitleTextView?.setTextColor(titleTextColor)
            mHintTextView?.setTextColor(hintTextColor)

            mArrowImageView?.setColorFilter(arrowColor, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun setTitleTextView() {
        stopTitleAnimation()
        mTitleTextView?.text = mTitle
        startTitleAnimation()
    }

    private fun setHintTextView() {
        if (mHint.isNullOrEmpty()) {
            mHintTextView?.visibility = View.GONE
        } else {
            mHintTextView?.text = mHint
            mHintTextView?.visibility = View.VISIBLE
        }
    }

    fun startTitleAnimation() {
        if (mEnableTitleAnimation) {
            val myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.flashing)
            mTitleTextView?.startAnimation(myFadeInAnimation)
        }
    }

    fun setRightImageIcon(@DrawableRes drawableResId: Int?) =
            if (drawableResId != null) {
                mRightIconImageView?.setImageResource(drawableResId)
                mRightIconImageView?.isVisible = true
            } else {
                mRightIconImageView?.setImageDrawable(null)
                mRightIconImageView?.isVisible = false
            }

    private fun stopTitleAnimation() {
        mTitleTextView?.animation?.cancel()
    }
}