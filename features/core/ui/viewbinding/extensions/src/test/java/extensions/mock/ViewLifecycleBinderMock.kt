package extensions.mock

import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.core.ui.viewbinding.extensions.AbstractViewLifecycleBinder

internal class ViewLifecycleBinderMock<in Owner : LifecyclePluginObserver.Owner>(
        override val threadChecker: ThreadChecker,
) : AbstractViewLifecycleBinder<Owner, ViewLifecycleBinderMock.ReturnValue>() {

    object ReturnValue

    var tryCreateValueCalledTimes: Int = 0
        private set

    override fun tryCreateValue(thisRef: Owner): ReturnValue {
        tryCreateValueCalledTimes++
        return ReturnValue
    }
}