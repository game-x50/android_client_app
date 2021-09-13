package com.ruslan.hlushan.android.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.ruslan.hlushan.core.extensions.ifNotNull

fun Activity.executeShowKeyboard() {
    val imm = (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
    imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.executeHideKeyboardForView(view: View?) {
    if (view != null) {
        val imm = (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }
}

fun Activity.executeHideKeyboard() {
    val inputManager = (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
    ifNotNull(currentFocus) { nonNullCurrentFocus ->
        inputManager?.hideSoftInputFromWindow(nonNullCurrentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}