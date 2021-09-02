package extension

import com.ruslan.hlushan.core.ui.api.presentation.command.handleCommandQueue
import com.ruslan.hlushan.core.ui.api.test.utils.LifecyclePluginObserverOwnerFragmentStub
import com.ruslan.hlushan.core.ui.api.test.utils.from_init_go_to
import com.ruslan.hlushan.core.ui.api.test.utils.go_to
import com.ruslan.hlushan.extensions.copy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import presentation.command.strategy.`create NOT empty CommandQueue`

class CommandHandlerExtensionsDelayedTest {

    @Test
    fun `handleCommandQueue before onBeforeSuperAttach will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.BEFORE_ON_BEFORE_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperAttach will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onAfterSuperAttach will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_ATTACH
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreate will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onAfterSuperCreate will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperCreateView will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_CREATE_VIEW
            )

    @Test
    fun `handleCommandQueue after onAfterSuperViewCreated will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_CREATE_VIEW
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `handleCommandQueue after finishCreateProcess but before onBeforeSuperStart will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_FINISH_CREATE_PROCESS
            )

    @Test
    fun `handleCommandQueue after onBeforeSuperStart will call handler in onAfterSuperStart`() =
            `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
                    goToState = LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_BEFORE_SUPER_START
            )
}

private fun `handleCommandQueue after some lifecycle method will be called in onAfterSuperStart`(
        goToState: LifecyclePluginObserverOwnerFragmentStub.DetailState
) {
    val fragmentStub = LifecyclePluginObserverOwnerFragmentStub()
    fragmentStub.from_init_go_to(goToState)

    `assert handleCommandQueue will be called in onAfterSuperStart`(
            fragmentStub = fragmentStub
    )
}

private fun `assert handleCommandQueue will be called in onAfterSuperStart`(
        fragmentStub: LifecyclePluginObserverOwnerFragmentStub
) {
    val commandQueue = `create NOT empty CommandQueue`()

    val expectedState = commandQueue.commands.copy().toMutableList()

    var expectedCalledCounter = 0

    var handlerCalledCounter = 0

    fragmentStub.handleCommandQueue(commandQueue) { receivedCommand ->
        handlerCalledCounter++
        assertEquals(expectedState.firstOrNull(), receivedCommand)
        assertEquals(expectedState, commandQueue.commands)

        expectedState.removeFirst()
    }

    assertTrue(expectedState.isNotEmpty())
    assertEquals(expectedState, commandQueue.commands)
    assertEquals(expectedCalledCounter, handlerCalledCounter)

    expectedCalledCounter = expectedState.size

    fragmentStub.go_to(LifecyclePluginObserverOwnerFragmentStub.DetailState.AFTER_ON_AFTER_SUPER_START)

    assertTrue(expectedState.isEmpty())
    assertEquals(expectedState, commandQueue.commands)
    assertEquals(expectedCalledCounter, handlerCalledCounter)
}