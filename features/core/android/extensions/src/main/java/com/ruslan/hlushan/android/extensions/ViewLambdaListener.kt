package com.ruslan.hlushan.android.extensions

import android.view.View

class ViewLambdaListener(private val onClickListener: () -> Unit) : View.OnClickListener {

    override fun onClick(v: View) {
        onClickListener()
    }
}