package extension.stub

import com.ruslan.hlushan.core.api.utils.thread.ThreadChecker
import com.ruslan.hlushan.core.ui.api.test.utils.LifecyclePluginObserverOwnerFragmentStub

internal class FragmentStubWithBinder(threadChecker: ThreadChecker) : LifecyclePluginObserverOwnerFragmentStub() {

    val binder = ViewLifecycleBinderMock<FragmentStubWithBinder>(threadChecker)

    val property by binder
}