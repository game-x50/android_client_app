package com.ruslan.hlushan.android.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

@ColorInt
fun Context.colorAttributeValue(@AttrRes colorAttrResId: Int): Int {
    val typedValue = TypedValue()

    val a = obtainStyledAttributes(typedValue.data, intArrayOf(colorAttrResId))
    @ColorInt val color = a.getColor(0, 0)

    a.recycle()

    return color
}

@ColorInt
fun Context.getContextCompatColor(@ColorRes colorResId: Int) = ContextCompat.getColor(this, colorResId)

@Suppress("TooGenericExceptionCaught")
val Context.applicationLabel: String
    get() = try {
        this.applicationInfo.loadLabel(this.packageManager).toString()
    } catch (e: Throwable) {
        ""
    }

class ScreenSizePx(
        val width: Int,
        val height: Int
)

fun Context.fullScreenDimension(): ScreenSizePx {
    val displayMetrics = DisplayMetrics()
    this.getDefaultDisplay().getRealMetrics(displayMetrics)
    return displayMetrics.getScreenSizePx()
}

fun Activity.applicationScreenDimension(): ScreenSizePx =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = this.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            ScreenSizePx(
                    width = (windowMetrics.bounds.width() - insets.left - insets.right),
                    height = (windowMetrics.bounds.height() - insets.top - insets.bottom)
            )
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("Deprecation")
            this.getDefaultDisplay().getMetrics(displayMetrics)
            displayMetrics.getScreenSizePx()
        }

@SuppressWarnings("UnsafeCast")
private fun Context.getDefaultDisplay(): Display =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.display!!
        } else {
            val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            @Suppress("Deprecation")
            windowManager.defaultDisplay
        }

private fun DisplayMetrics.getScreenSizePx(): ScreenSizePx =
        ScreenSizePx(
                width = widthPixels,
                height = heightPixels
        )

val Context.areNotificationsEnabledForApp: Boolean
    get() = NotificationManagerCompat.from(this).areNotificationsEnabled()

@get:SuppressLint("WrongConstant", "PackageManagerGetSignatures")
val Context.signatureSha: ByteArray
    get() {
        val packageManager = this.packageManager
        val packageName = this.packageName

        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo
                    .apkContentsSigners
        } else {
            @Suppress("Deprecation")
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
        }

        return CryptoUtils.sha256(signatures.first().toByteArray())
    }

@get:DrawableRes
val Context.appIconResourceId: Int
    get() = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).icon