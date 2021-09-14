package extensions

import com.ruslan.hlushan.core.thread.test.utils.ThreadCheckerStub
import com.ruslan.hlushan.core.ui.lifecycle.test.utils.LifecyclePluginObserverOwnerFragmentStub
import com.ruslan.hlushan.core.ui.lifecycle.test.utils.from_init_go_to
import extensions.mock.FragmentStubWithBinder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@SuppressWarnings("MaxLineLength")
internal class AbstractViewLifecycleBinderFragmentLifecycleTest {

    private lateinit var fragmentStubWithBinder: FragmentStubWithBinder

    @Before
    fun before() {
        val threadChecker = ThreadCheckerStub(isNeededThread = true)

        fragmentStubWithBinder = FragmentStubWithBinder(threadChecker = threadChecker)
    }

    @Test
    fun `getValue before onBeforeSuperAttach returns null, wont call tryCreateValue, lifecycleObservers is empty`() =
            assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)

    @Test
    fun `getValue after onBeforeSuperAttach returns null, wont call tryCreateValue, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onAfterSuperAttach returns null, wont call tryCreateValue, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onBeforeSuperCreate returns null, wont call tryCreateValue, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onAfterSuperCreate returns null, wont call tryCreateValue, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onBeforeSuperCreateView returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onAfterSuperViewCreated returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `getValue after finishCreateProcess but before onBeforeSuperStart returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_CREATE_PROCESS)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onBeforeSuperStart returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_START)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onAfterSuperStart returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `getValue after finishStartProcess but before onBeforeSuperResume returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_START_PROCESS)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onBeforeSuperResume returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_RESUME)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onAfterSuperResume returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_RESUME)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `getValue after finishResumeProcess but before startPauseProcess returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_RESUME_PROCESS)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `getValue after startPauseProcess but before onBeforeSuperPause returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_PAUSE_PROCESS)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onBeforeSuperPause returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_PAUSE)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onAfterSuperPause returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_PAUSE)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `getValue after startStopProcess but before onBeforeSuperStop returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_STOP_PROCESS)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onBeforeSuperStop returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @Test
    fun `getValue after onAfterSuperStop returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_STOP)
        assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = 1)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `getValue after startDestroyProcess but before onBeforeSuperDestroyView returns non null, call tryCreateValue once, lifecycleObservers is not empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_DESTROY_PROCESS)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onBeforeSuperDestroyView returns null, wont call tryCreateValue once, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onAfterSuperDestroyView returns null, wont call tryCreateValue once, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `getValue after markAsUnInitialized but before onBeforeSuperDestroy returns null, wont call tryCreateValue once, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_MARK_AS_UN_INITIALIZED)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onBeforeSuperDestroy returns null, wont call tryCreateValue once, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onAfterSuperDestroy returns null, wont call tryCreateValue once, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onBeforeSuperDetach returns null, wont call tryCreateValue once, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DETACH)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @Test
    fun `getValue after onAfterSuperDetach returns null, wont call tryCreateValue once, lifecycleObservers is empty`() {
        fragmentStubWithBinder.from_init_go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DETACH)
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)
    }

    @SuppressWarnings("LongMethod")
    @Test
    fun `go through few fragment view lifecycles tryCreateValue should be called once per lifecycle and value should be recreated`() {
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)

        fragmentStubWithBinder.onBeforeSuperAttach()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)

        fragmentStubWithBinder.onAfterSuperAttach()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)

        fragmentStubWithBinder.onBeforeSuperCreate()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)

        fragmentStubWithBinder.onAfterSuperCreate()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = 0)

        val countOfLifeCycles = 10

        for (i in 1..countOfLifeCycles) {

            fragmentStubWithBinder.onBeforeSuperCreateView()
            assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

            fragmentStubWithBinder.onAfterSuperViewCreated()
            assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

            fragmentStubWithBinder.finishCreateProcess()
            assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

            repeat(10) { _ ->

                fragmentStubWithBinder.onBeforeSuperStart()
                assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                fragmentStubWithBinder.onAfterSuperStart()
                assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                fragmentStubWithBinder.finishStartProcess()
                assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                repeat(10) { _ ->

                    fragmentStubWithBinder.onBeforeSuperResume()
                    assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                    fragmentStubWithBinder.onAfterSuperResume()
                    assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                    fragmentStubWithBinder.finishResumeProcess()
                    assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                    fragmentStubWithBinder.startPauseProcess()
                    assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                    fragmentStubWithBinder.onBeforeSuperPause()
                    assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                    fragmentStubWithBinder.onAfterSuperPause()
                    assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)
                }

                fragmentStubWithBinder.startStopProcess()
                assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                fragmentStubWithBinder.onBeforeSuperStop()
                assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

                fragmentStubWithBinder.onAfterSuperStop()
                assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)
            }

            fragmentStubWithBinder.startDestroyProcess()
            assertBinder(expectedPropertyIsNull = false, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 1)

            fragmentStubWithBinder.onBeforeSuperDestroyView()
            assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 0)

            fragmentStubWithBinder.onAfterSuperDestroyView()
            assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 0)

            fragmentStubWithBinder.markAsUnInitialized()
            assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = i, lifecycleObserversCount = 0)
        }

        fragmentStubWithBinder.onBeforeSuperDestroy()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = countOfLifeCycles, lifecycleObserversCount = 0)

        fragmentStubWithBinder.onAfterSuperDestroy()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = countOfLifeCycles, lifecycleObserversCount = 0)

        fragmentStubWithBinder.onBeforeSuperDetach()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = countOfLifeCycles, lifecycleObserversCount = 0)

        fragmentStubWithBinder.onAfterSuperDetach()
        assertBinder(expectedPropertyIsNull = true, expectedTryCreateValueCalledTimes = countOfLifeCycles, lifecycleObserversCount = 0)
    }

    private fun assertBinder(
            expectedPropertyIsNull: Boolean,
            expectedTryCreateValueCalledTimes: Int,
            lifecycleObserversCount: Int = expectedTryCreateValueCalledTimes
    ) {
        assertEquals(expectedPropertyIsNull, (fragmentStubWithBinder.property == null))
        assertEquals(expectedTryCreateValueCalledTimes, fragmentStubWithBinder.binder.tryCreateValueCalledTimes)
        assertEquals(lifecycleObserversCount, fragmentStubWithBinder.lifecyclePluginObservers.size)
    }
}