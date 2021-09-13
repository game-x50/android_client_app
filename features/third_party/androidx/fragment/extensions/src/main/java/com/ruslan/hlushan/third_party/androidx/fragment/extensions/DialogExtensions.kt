@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.fragment.extensions

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ruslan.hlushan.core.extensions.ifNotNull

fun DialogFragment.showNowSafety(parentFragmentManager: FragmentManager, tag: String?) {
    show(parentFragmentManager, tag)
    parentFragmentManager.executePendingTransactions()
}

fun DialogFragment.dismissNowSafety() {
    ifNotNull(parentFragmentManager) { fm ->
        dismissAllowingStateLoss()
        fm.executePendingTransactions()
    }
}