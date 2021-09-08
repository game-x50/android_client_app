@file:SuppressWarnings("MaxLineLength")

package extension

import com.ruslan.hlushan.core.ui.api.test.utils.LifecyclePluginObserverOwnerFragmentStub
import com.ruslan.hlushan.core.ui.api.test.utils.from_init_go_to
import com.ruslan.hlushan.core.ui.api.test.utils.go_to
import com.ruslan.hlushan.core.ui.viewmodel.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.viewmodel.command.handleCommandQueue
import com.ruslan.hlushan.core.ui.viewmodel.command.strategy.StrategyCommand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import presentation.command.strategy.`create default real command`

class CommandHandlerExtensionsFewLifeCyclesTest {

    @Test
    fun `handleCommandQueue before onBeforeSuperAttach will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.BEFORE_ON_BEFORE_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperAttach will be called between onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperAttach will be called between onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreate will be called between onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperCreate will be called between onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreateView will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperViewCreated will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after finishCreateProcess but before onBeforeSuperStart will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_CREATE_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStart will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_START
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStart will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after finishStartProcess but before onBeforeSuperResume will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_START_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperResume will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after onAfterSuperResume will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_RESUME
            )

    @Test
    fun `handleCommandQueue after finishResumeProcess but before startPauseProcess will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_RESUME_PROCESS
            )

    @Test
    fun `handleCommandQueue after startPauseProcess but before onBeforeSuperPause will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_PAUSE_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperPause will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperPause will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_PAUSE
            )

    @Test
    fun `handleCommandQueue after startStopProcess but before onBeforeSuperStop will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_STOP_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStop will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP
            )

    @Test
    fun `handleCommandQueue after onAfterSuperStop will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_STOP
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after startDestroyProcess but before onBeforeSuperDestroyView will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_DESTROY_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperDestroyView will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_DESTROY_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperDestroyView will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_DESTROY_VIEW
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after markAsUnInitialized but before onBeforeSuperDestroy will be called between onAfterSuperStart and onBeforeSuperStop`() =
            `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_MARK_AS_UN_INITIALIZED
            )
}

private fun `handleCommandQueue after some lifecycle method will be called between onAfterSuperStart and onBeforeSuperStop`(
        goToState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    val fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
    fragmentStub.from_init_go_to(goToState)

    `assert handleCommandQueue will be called between onAfterSuperStart and onBeforeSuperStop`(
            fragmentStub = fragmentStub
    )
}

@SuppressWarnings("ComplexMethod")
private fun `assert handleCommandQueue will be called between onAfterSuperStart and onBeforeSuperStop`(
        fragmentStub: LifecyclePluginObserverOwnerFragmentStub
) {
    val commandQueue = MutableCommandQueue<StrategyCommand>()

    val expectedState = mutableListOf<StrategyCommand>()

    var expectedCalledCounter = 0

    var handlerCalledCounter = 0

    fragmentStub.handleCommandQueue(commandQueue) { receivedCommand ->
        handlerCalledCounter++
        assertEquals(expectedState.firstOrNull(), receivedCommand)
        assertEquals(expectedState, commandQueue.commands)

        expectedState.removeFirst()
    }

    val subscribedAfter_onAfterSuperStart_or_startStopProcess = arrayOf(
            LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_START_STOP_PROCESS,
            LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START
    ).contains(fragmentStub.detailState)

    assertTrue(expectedState.isEmpty())
    assertEquals(expectedState, commandQueue.commands)
    assertEquals(expectedCalledCounter, handlerCalledCounter)

    repeat(5) { n ->

        val shouldBeCommandsHandled = (!subscribedAfter_onAfterSuperStart_or_startStopProcess || (n > 0))

        LifecyclePluginObserverOwnerFragmentStub.DetailState.values()
                .filter { state ->
                    ((state >= LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW)
                     && (state <= LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_MARK_AS_UN_INITIALIZED))
                }.forEach { newState ->

                    val beforeCommandsQueueSize = expectedState.size

                    if (fragmentStub.detailState < newState) {
                        fragmentStub.go_to(newState)
                    }

                    val addedAfterHandleCommand = `create default real command`()
                    expectedState.add(addedAfterHandleCommand)
                    commandQueue.add(addedAfterHandleCommand)

                    when {
                        (shouldBeCommandsHandled
                         && (fragmentStub.detailState == LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START)) -> {

                            expectedCalledCounter += (beforeCommandsQueueSize + 1)
                            assertTrue(expectedState.isEmpty())
                        }
                        (shouldBeCommandsHandled
                         && (fragmentStub.detailState > LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START)
                         && (fragmentStub.detailState < LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_STOP))  -> {

                            expectedCalledCounter++
                            assertTrue(expectedState.isEmpty())
                        }
                        else                                                                                                               -> {
                            assertTrue(expectedState.isNotEmpty())
                        }
                    }

                    assertEquals(expectedState, commandQueue.commands)
                    assertEquals(expectedCalledCounter, handlerCalledCounter)
                }
    }
}