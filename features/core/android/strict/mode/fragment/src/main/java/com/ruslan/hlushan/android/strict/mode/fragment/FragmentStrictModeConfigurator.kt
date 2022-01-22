package com.ruslan.hlushan.android.strict.mode.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.strictmode.FragmentStrictMode
import androidx.fragment.app.strictmode.FragmentTagUsageViolation
import kotlin.reflect.KClass

object FragmentStrictModeConfigurator {

    fun configure(
            fragmentManager: FragmentManager,
            fragmentTagUsageViolationClasses: List<KClass<out Fragment>>
    ) {

        val policyBuilder = FragmentStrictMode.Policy.Builder()
                .detectFragmentReuse()
                .detectTargetFragmentUsage()
                .detectWrongFragmentContainer()
                .detectSetUserVisibleHint()
                .detectFragmentTagUsage()
                .detectRetainInstanceUsage()
                .penaltyDeath()

        for (tagUsageViolationClass in fragmentTagUsageViolationClasses) {
            policyBuilder.allowViolation(
                    tagUsageViolationClass.java,
                    FragmentTagUsageViolation::class.java
            )
        }

        val policy = policyBuilder.build()

        fragmentManager.strictModePolicy = policy
    }
}