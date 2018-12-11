package extension

import com.ruslan.hlushan.core.api.test.utils.utils.thread.ThreadCheckerMock
import com.ruslan.hlushan.core.ui.api.test.utils.LifecyclePluginObserverOwnerFragmentStub
import com.ruslan.hlushan.core.ui.api.test.utils.from_init_go_to
import com.ruslan.hlushan.core.ui.api.test.utils.go_to
import com.ruslan.hlushan.test.utils.assertThrows
import extension.stub.FragmentStubWithBinder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AbstractViewLifecycleBinderThreadTest {

    private lateinit var fragmentStubWithBinder: FragmentStubWithBinder
    private lateinit var threadChecker: ThreadCheckerMock

    @Before
    fun before() {
        threadChecker = ThreadCheckerMock(defaultIsNeededThread = true)
        fragmentStubWithBinder = FragmentStubWithBinder(threadChecker = threadChecker)
    }

    @Test
    fun `getValue before onBeforeSuperAttach in invalid thread throw error`() {
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperAttach in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperAttach in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperCreate in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperCreate in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperCreateView in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperViewCreated in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperStart in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperStart in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperResume in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_RESUME)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperResume in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_RESUME)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperPause in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_PAUSE)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperPause in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_PAUSE)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperStop in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperStop in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_STOP)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperDestroyView in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperDestroyView in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperDestroy in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperDestroy in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onBeforeSuperDetach in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DETACH)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `getValue after onAfterSuperDetach in invalid thread throw error`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DETACH)
        setInvalidThreadAndAssert()
    }

    @Test
    fun `lifecycle transition will fail just in case onBeforeSuperDestroyView`() {
        threadChecker.isNeededThread = false
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_RESUME_PROCESS)

        threadChecker.isNeededThread = true
        fragmentStubWithBinder.property
        threadChecker.isNeededThread = false

        fragmentStubWithBinder.go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_DESTROY_PROCESS)

        assertThrows(IllegalStateException::class) {
            fragmentStubWithBinder.onBeforeSuperDestroyView()
        }
    }

    @Test
    fun `lifecycle transition wont fail for non view lifecycle`() {
        threadChecker.isNeededThread = false

        fragmentStubWithBinder.onBeforeSuperAttach()
        fragmentStubWithBinder.onAfterSuperAttach()
        fragmentStubWithBinder.onBeforeSuperCreate()
        fragmentStubWithBinder.onAfterSuperCreate()

        fragmentStubWithBinder.onBeforeSuperDestroy()
        fragmentStubWithBinder.onAfterSuperDestroy()
        fragmentStubWithBinder.onBeforeSuperDetach()
        fragmentStubWithBinder.onAfterSuperDetach()
    }

    private fun setInvalidThreadAndAssert() {
        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            fragmentStubWithBinder.property
        }

        assertEquals(0, fragmentStubWithBinder.binder.tryCreateValueCalledTimes)
    }
}