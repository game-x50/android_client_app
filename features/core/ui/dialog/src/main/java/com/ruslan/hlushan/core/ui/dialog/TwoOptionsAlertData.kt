package com.ruslan.hlushan.core.ui.dialog

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import com.ruslan.hlushan.core.ui.dialog.R
import kotlinx.parcelize.Parcelize

@SuppressWarnings("LongParameterList")
@Parcelize
class TwoOptionsAlertData(
        @StyleRes val styleResId: Int = R.style.AlertDialogStyle,
        @DrawableRes val iconResId: Int? = null,
        val title: String,
        val message: String,
        val positiveButtonText: String,
        val negativeButtonText: String? = null,
        val cancelable: Boolean = true,
        val cancelOnButtonClick: Boolean = true
) : Parcelable