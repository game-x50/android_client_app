package com.ruslan.hlushan.core.ui.api.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import com.ruslan.hlushan.android.extensions.setThrottledOnClickListener
import com.ruslan.hlushan.extensions.ifNotNull
import com.ruslan.hlushan.third_party.androidx.fragment.extensions.dismissNowSafety

abstract class BaseTwoOptionsAlert : BaseDialogFragment() {

    @get:LayoutRes
    override val layoutResId: Int? get() = null

    protected abstract val positiveOnClickListener: () -> Unit
    protected abstract val negativeOnClickListener: (() -> Unit)?

    protected abstract fun extractData(): TwoOptionsAlertData?

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val data = extractData()

        val builder: AlertDialog.Builder
        if (data != null) {
            @SuppressWarnings("UnsafeCallOnNullableType")
            builder = AlertDialog.Builder(requireContext(), data.styleResId)
                    .setTitle(data.title)
                    .setMessage(data.message)

            if (data.iconResId != null) {
                builder.setIcon(data.iconResId)
            }

            builder.setPositiveButton(data.positiveButtonText, null)

            if (data.negativeButtonText != null) {
                builder.setNegativeButton(data.negativeButtonText, null)
            }

            builder.setCancelable(data.cancelable)
        } else {
            @SuppressWarnings("UnsafeCallOnNullableType")
            builder = AlertDialog.Builder(requireContext())
                    .setTitle("Unexpected Error")
                    .setMessage("Sorry")
                    .setPositiveButton(android.R.string.cancel, null)
        }

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val data = extractData()
        if (data != null) {
            isCancelable = data.cancelable
            dialog?.setCanceledOnTouchOutside(data.cancelable)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        ifNotNull((dialog as? AlertDialog)) { alert ->

            val positiveButton = alert.getButton(Dialog.BUTTON_POSITIVE)
            positiveButton?.setThrottledOnClickListener {
                positiveOnClickListener()
                dismissDialogIfNeededAfterButtonClick()
            }

            val negativeButton = alert.getButton(Dialog.BUTTON_NEGATIVE)
            negativeButton?.setThrottledOnClickListener {
                negativeOnClickListener?.invoke()
                dismissDialogIfNeededAfterButtonClick()
            }
        }
    }

    private fun dismissDialogIfNeededAfterButtonClick() {
        val data = extractData()
        if (data?.cancelOnButtonClick == true) {
            dismissNowSafety()
        }
    }
}