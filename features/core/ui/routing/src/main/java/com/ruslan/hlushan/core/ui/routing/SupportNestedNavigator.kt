package com.ruslan.hlushan.core.ui.routing

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator

class SupportNestedNavigator(
        private val parentRouter: Router,
        activity: FragmentActivity,
        childFragmentManager: FragmentManager,
        @IdRes containerId: Int
) : AppNavigator(activity = activity, fragmentManager = childFragmentManager, containerId = containerId) {

    override fun activityBack() = parentRouter.exit()
}