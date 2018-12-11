package com.ruslan.hlushan.core.ui.api.view

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun spanDifferentColor(str: CharSequence, color: Int, start: Int, end: Int): Spannable {
    val span = SpannableString(str)
    span.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return span
}

private fun spanDifferentTypeface(str: CharSequence, start: Int, end: Int): SpannableStringBuilder {
    val sBuilder = SpannableStringBuilder()
    sBuilder.append(str)
    sBuilder.setSpan(android.text.style.StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return sBuilder
}

fun setTextWithDifferentColorsAndTypeFace(hint: String?, value: String?, textView: TextView?, @ColorRes hintColorResId: Int) {
    if (textView != null && textView.context != null) {
        if (value.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            val fullValue = "$hint $value"
            textView.visibility = View.VISIBLE
            var text = spanDifferentColor(
                    fullValue,
                    ContextCompat.getColor(textView.context, hintColorResId),
                    0,
                    (hint?.length ?: 0)
            )
            text = spanDifferentTypeface(
                    text,
                    (hint?.length ?: 0),
                    fullValue.length
            )
            textView.text = text
        }
    }
}