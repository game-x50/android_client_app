@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.insets

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

//https://gitlab.com/terrakok/gitlab-client
//https://github.com/surfstudio/EdgeToEdge-Sample

fun Activity.applyWindowTransparencyAfterSetContentView(container: FrameLayout) {
    container.doOnApplyWindowInsets { view, insets, initialPadding ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
                left = (initialPadding.left + systemBarsInsets.left),
                right = (initialPadding.right + systemBarsInsets.right)
        )
        insets.withReplacedSystemWindowInsets(
                Rect(
                        0,
                        systemBarsInsets.top,
                        0,
                        systemBarsInsets.bottom
                )
        )
    }
}

@SuppressWarnings("LongParameterList", "ComplexMethod")
fun View.addSystemPadding(
        targetView: View = this,
        isConsumed: Boolean = false,
        left: Boolean = false,
        top: Boolean = false,
        right: Boolean = false,
        bottom: Boolean = false
) = doOnApplyWindowInsets { _, insets, initialPadding ->
    val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

    val leftPadding = if (left) {
        (initialPadding.left + systemBarsInsets.left)
    } else {
        targetView.paddingLeft
    }
    val topPadding = if (top) {
        (initialPadding.top + systemBarsInsets.top)
    } else {
        targetView.paddingTop
    }
    val rightPadding = if (right) {
        (initialPadding.right + systemBarsInsets.right)
    } else {
        targetView.paddingRight
    }
    val bottomPadding = if (bottom) {
        (initialPadding.bottom + systemBarsInsets.bottom)
    } else {
        targetView.paddingBottom
    }
    targetView.updatePadding(left = leftPadding, top = topPadding, right = rightPadding, bottom = bottomPadding)
    if (isConsumed) {
        insets.withReplacedSystemWindowInsets(
                createInsetsRect(
                        systemBarsInsets = systemBarsInsets,
                        left = left,
                        top = top,
                        right = right,
                        bottom = bottom
                )
        )
    } else {
        insets
    }
}

private fun createInsetsRect(systemBarsInsets: Insets, left: Boolean, top: Boolean, right: Boolean, bottom: Boolean): Rect =
        Rect(
                if (left) 0 else systemBarsInsets.left,
                if (top) 0 else systemBarsInsets.top,
                if (right) 0 else systemBarsInsets.right,
                if (bottom) 0 else systemBarsInsets.bottom
        )

private fun View.doOnApplyWindowInsets(
        block: (View, insets: WindowInsetsCompat, initialPadding: Rect) -> WindowInsetsCompat
) {
    val initialPadding = recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialPadding)
    }
    requestApplyInsetsWhenAttached()
}

private fun recordInitialPaddingForView(view: View): Rect =
        Rect(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

private fun View.requestApplyInsetsWhenAttached() =
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

private fun WindowInsetsCompat.withReplacedSystemWindowInsets(rect: Rect): WindowInsetsCompat =
        WindowInsetsCompat.Builder(this)
                .setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(rect))
                .build()