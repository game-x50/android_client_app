package com.ruslan.hlushan.core.ui.lifecycle.utils

import com.ruslan.hlushan.core.api.log.AppLogger
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver

//TODO: #write_unit_tests
@SuppressWarnings("TooManyFunctions")
class LoggerLifecyclePluginObserver(
        owner: Any,
        private val appLogger: AppLogger
) : LifecyclePluginObserver {

    private val ownerClass = owner.javaClass.name

    override fun onBeforeSuperAttach() = appLogger.log(this, ownerClass)
    override fun onAfterSuperAttach() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperCreate() = appLogger.log(this, ownerClass)
    override fun onAfterSuperCreate() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperCreateView() = appLogger.log(this, ownerClass)
    override fun onAfterSuperViewCreated() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperStart() = appLogger.log(this, ownerClass)
    override fun onAfterSuperStart() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperResume() = appLogger.log(this, ownerClass)
    override fun onAfterSuperResume() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperPause() = appLogger.log(this, ownerClass)
    override fun onAfterSuperPause() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperStop() = appLogger.log(this, ownerClass)
    override fun onAfterSuperStop() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperDestroyView() = appLogger.log(this, ownerClass)
    override fun onAfterSuperDestroyView() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperDestroy() = appLogger.log(this, ownerClass)
    override fun onAfterSuperDestroy() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperDetach() = appLogger.log(this, ownerClass)
    override fun onAfterSuperDetach() = appLogger.log(this, ownerClass)

    override fun onBeforeSuperSaveInstanceState() = appLogger.log(this, ownerClass)
    override fun onAfterSuperSaveInstanceState() = appLogger.log(this, ownerClass)
}