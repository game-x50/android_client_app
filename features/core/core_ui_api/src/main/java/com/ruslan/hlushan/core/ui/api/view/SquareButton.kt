package com.ruslan.hlushan.core.ui.api.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton

/**
 * Created by User on 02.02.2018.
 */

class SquareButton : MaterialButton {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}