package com.ruslan.hlushan.android.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
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

fun Context.fullScreenDimension(): DisplayMetrics =
        DisplayMetrics().also {
            getDefaultDisplay().getRealMetrics(it)
        }

fun Context.applicationScreenDimension(): DisplayMetrics =
        DisplayMetrics().also {
            getDefaultDisplay().getMetrics(it)
        }

@SuppressWarnings("UnsafeCast")
private fun Context.getDefaultDisplay(): Display {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return windowManager.defaultDisplay
}

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
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
        }

        return CryptoUtils.sha256(signatures.first().toByteArray())
    }

@get:DrawableRes
val Context.appIconResourceId: Int
    get() = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).icon