package presentation.command

import com.ruslan.hlushan.core.ui.api.presentation.command.MutableCommandQueue
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand
import com.ruslan.hlushan.extensions.copy
import com.ruslan.hlushan.rxjava2.test.utils.assertNotCompleteNoErrorsNoValues
import com.ruslan.hlushan.test.utils.assertThrows
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import presentation.command.strategy.`generate new state of empty commands`
import presentation.command.stub.HandleStrategyStub
import presentation.command.stub.StrategyCommandStub
import kotlin.concurrent.thread

class MutableCommandQueueTest {

    @Test
    fun `check init state commands empty`() {
        val queue = MutableCommandQueue<StrategyCommand>()
        assertTrue(queue.commands.isEmpty())
    }

    @Test
    fun `check init state observing nothing`() {
        val queue = MutableCommandQueue<StrategyCommand>()
        queue.observeNewCommand()
                .test()
                .assertNotCompleteNoErrorsNoValues()
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `notifyAfterCommandExecute calls on produced strategy just afterApply, without beforeApply, passes previous commands as params and saves returned from afterApply`() {
        val queue = MutableCommandQueue<StrategyCommand>()

        val testObserver = queue.observeNewCommand()
                .test()

        repeat(10) { i ->

            val previousState = queue.commands.copy()

            val newState = `generate new state of empty commands`(size = (i + 1))

            val strategy = HandleStrategyStub(returnBeforeApply = null, returnAfterApply = newState)

            val command = StrategyCommandStub(strategy)

            queue.notifyAfterCommandExecute(command)

            assertNotEquals(previousState, newState)
            assertEquals(previousState, strategy.passedAfterApplyCurrentState)
            assertEquals(command, strategy.passedAfterApplyIncomingCommand)
            assertEquals(newState, queue.commands)

            assertNull(strategy.passedBeforeApplyCurrentState)
            assertNull(strategy.passedBeforeApplyIncomingCommand)
        }

        testObserver.assertNotCompleteNoErrorsNoValues()
    }

    @Test
    fun `notifyAfterCommandExecute observing nothing`() {
        val queue = MutableCommandQueue<StrategyCommand>()

        val testObserver = queue.observeNewCommand()
                .test()

        repeat(10) { i ->

            val newState = `generate new state of empty commands`(size = (i + 1))

            val strategy = HandleStrategyStub(returnBeforeApply = null, returnAfterApply = newState)

            val command = StrategyCommandStub(strategy)

            queue.notifyAfterCommandExecute(command)
        }

        testObserver.assertNotCompleteNoErrorsNoValues()
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `add calls on produced strategy just beforeApply, without afterApply, passes previous commands as params and saves returned from beforeApply`() {
        val queue = MutableCommandQueue<StrategyCommand>()

        repeat(10) { i ->
            val previousState = queue.commands.copy()

            val newState = `generate new state of empty commands`(size = (i + 1))

            val strategy = HandleStrategyStub(returnBeforeApply = newState, returnAfterApply = null)

            val command = StrategyCommandStub(strategy)

            queue.add(command)

            assertNotEquals(previousState, newState)
            assertEquals(previousState, strategy.passedBeforeApplyCurrentState)
            assertEquals(command, strategy.passedBeforeApplyIncomingCommand)
            assertEquals(newState, queue.commands)

            assertNull(strategy.passedAfterApplyCurrentState)
            assertNull(strategy.passedAfterApplyIncomingCommand)
        }
    }

    @Test
    fun `add observing just single commands passed to add`() {
        val queue = MutableCommandQueue<StrategyCommand>()

        val globalTestObserver = queue.observeNewCommand()
                .test()

        val allObservedCommands = mutableListOf<StrategyCommand>()

        repeat(10) { i ->

            val localTestObserver = queue.observeNewCommand()
                    .test()

            val newState = `generate new state of empty commands`(size = (i + 1))

            val strategy = HandleStrategyStub(returnBeforeApply = newState, returnAfterApply = null)

            val command = StrategyCommandStub(strategy)

            allObservedCommands.add(command)

            localTestObserver.assertNotCompleteNoErrorsNoValues()

            queue.add(command)

            localTestObserver.assertNotComplete()
                    .assertNoErrors()
                    .assertValue(command)
        }

        assertTrue(allObservedCommands.isNotEmpty())

        globalTestObserver
                .assertNotComplete()
                .assertNoErrors()
                .assertValues(*allObservedCommands.toTypedArray())
    }

    @Test
    fun `when observing event commands already updated`() {
        val queue = MutableCommandQueue<StrategyCommand>()

        repeat(10) { i ->

            val previousState = queue.commands.copy()

            val newState = `generate new state of empty commands`(size = (i + 1))

            var wasReceived = false

            val disposable = queue.observeNewCommand()
                    .doOnNext { receivedCommand: StrategyCommand ->
                        assertEquals(newState, queue.commands)
                        wasReceived = true
                    }
                    .subscribe()

            val strategy = HandleStrategyStub(returnBeforeApply = newState, returnAfterApply = null)

            val command = StrategyCommandStub(strategy)

            queue.add(command)

            assertNotEquals(previousState, newState)
            assertTrue(wasReceived)

            disposable.dispose()
        }
    }

    @Test
    fun `notifyAfterCommandExecute can't be called from another thread of constructing and not mutate state`() =
            `assert can't be called from another thread of constructing and not mutate state`(
                    MutableCommandQueue<StrategyCommand>::notifyAfterCommandExecute
            )

    @Test
    fun `add can't be called from another thread of constructing and not mutate state`() =
            `assert can't be called from another thread of constructing and not mutate state`(
                    MutableCommandQueue<StrategyCommand>::add
            )
}

private fun `assert can't be called from another thread of constructing and not mutate state`(
        action: MutableCommandQueue<StrategyCommand>.(StrategyCommand) -> Unit
) {
    val queue = MutableCommandQueue<StrategyCommand>()

    val initState = queue.commands.copy()

    thread {
        repeat(10) { i ->
            val newState = `generate new state of empty commands`(size = (i + 1))
            val strategy = HandleStrategyStub(returnBeforeApply = newState, returnAfterApply = null)
            val command = StrategyCommandStub(strategy)

            assertThrows(IllegalStateException::class) {
                queue.action(command)
            }
        }
    }.join()

    assertEquals(initState, queue.commands)
}