package com.ruslan.hlushan.android.extensions

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * @author Ruslan Hlushan on 2019-08-22
 */
//https://gitlab.com/terrakok/gitlab-client
//https://github.com/surfstudio/EdgeToEdge-Sample

fun Activity.applyWindowTransparencyAfterSetContentView(container: FrameLayout) {
    container.doOnApplyWindowInsets { view, insets, initialPadding ->
        view.updatePadding(
                left = (initialPadding.left + insets.systemWindowInsetLeft),
                right = (initialPadding.right + insets.systemWindowInsetRight)
        )
        insets.withReplacedSystemWindowInsets(
                Rect(
                        0,
                        insets.systemWindowInsetTop,
                        0,
                        insets.systemWindowInsetBottom
                )
        )
    }
}

@SuppressWarnings("LongParameterList")
fun View.addSystemPadding(
        targetView: View = this,
        isConsumed: Boolean = false,
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
) {
    doOnApplyWindowInsets { _, insets, initialPadding ->
        targetView.updatePadding(
                left = (initialPadding.left + +insets.systemWindowInsetLeft).takeIf { left },
                top = (initialPadding.top + insets.systemWindowInsetTop).takeIf { top },
                right = (initialPadding.right + insets.systemWindowInsetRight).takeIf { right },
                bottom = (initialPadding.bottom + insets.systemWindowInsetBottom).takeIf { bottom }
        )
        if (isConsumed) {
            insets.withReplacedSystemWindowInsets(
                    Rect(
                            if (left) 0 else insets.systemWindowInsetLeft,
                            if (top) 0 else insets.systemWindowInsetTop,
                            if (right) 0 else insets.systemWindowInsetRight,
                            if (bottom) 0 else insets.systemWindowInsetBottom
                    )
            )
        } else {
            insets
        }
    }
}

fun View.doOnApplyWindowInsets(block: (View, insets: WindowInsetsCompat, initialPadding: Rect) -> WindowInsetsCompat) {
    val initialPadding = recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding)
    }
    requestApplyInsetsWhenAttached()
}

private fun recordInitialPaddingForView(view: View) =
        Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

private fun WindowInsetsCompat.withReplacedSystemWindowInsets(rect: Rect): WindowInsetsCompat =
        WindowInsetsCompat.Builder(this)
                .setSystemWindowInsets(Insets.of(rect))
                .build()