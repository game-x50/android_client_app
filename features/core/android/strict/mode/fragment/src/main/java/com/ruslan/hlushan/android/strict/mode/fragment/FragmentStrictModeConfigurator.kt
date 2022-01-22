package com.ruslan.hlushan.android.strict.mode.fragment

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.strictmode.FragmentStrictMode

object FragmentStrictModeConfigurator {

    fun configure(fragmentManager: FragmentManager) {

        val policy = FragmentStrictMode.Policy.Builder()
                .detectFragmentReuse()
                .detectTargetFragmentUsage()
                .detectWrongFragmentContainer()
                .detectSetUserVisibleHint()
                .detectFragmentTagUsage()
                .detectRetainInstanceUsage()
                .penaltyDeath()
                .build()

        fragmentManager.strictModePolicy = policy
    }
}