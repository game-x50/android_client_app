package com.ruslan.hlushan.android.extensions

import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.extensions.ifNotNull

/**
 * @author Ruslan Hlushan on 11/1/18.
 */

fun DialogFragment.showNowSafety(parentFragmentManager: FragmentManager, tag: String?) {
    show(parentFragmentManager, tag)
    parentFragmentManager.executePendingTransactions()
}

fun DialogFragment.dismissNowSafety() {
    ifNotNull(fragmentManager) { fm ->
        dismissAllowingStateLoss()
        fm.executePendingTransactions()
    }
}

fun AppCompatDialog.safetyDismiss() {
    if (isShowing) {
        dismiss()
    }
}