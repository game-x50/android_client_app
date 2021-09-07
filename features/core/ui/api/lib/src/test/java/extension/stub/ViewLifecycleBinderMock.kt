package extension.stub

import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.viewbinding.extensions.AbstractViewLifecycleBinder
import com.ruslan.hlushan.core.ui.api.presentation.lifecycle.LifecyclePluginObserver

internal class ViewLifecycleBinderMock<in Owner : LifecyclePluginObserver.Owner>(
        override val threadChecker: ThreadChecker,
) : com.ruslan.hlushan.core.ui.viewbinding.extensions.AbstractViewLifecycleBinder<Owner, ViewLifecycleBinderMock.ReturnValue>() {

    object ReturnValue

    var tryCreateValueCalledTimes: Int = 0
        private set

    override fun tryCreateValue(thisRef: Owner): ReturnValue {
        tryCreateValueCalledTimes++
        return ReturnValue
    }
}