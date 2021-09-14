package extensions.mock

import com.ruslan.hlushan.core.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.lifecycle.test.utils.LifecyclePluginObserverOwnerFragmentStub

internal class FragmentStubWithBinder(threadChecker: ThreadChecker) : LifecyclePluginObserverOwnerFragmentStub() {

    val binder = ViewLifecycleBinderMock<FragmentStubWithBinder>(threadChecker)

    val property by binder
}