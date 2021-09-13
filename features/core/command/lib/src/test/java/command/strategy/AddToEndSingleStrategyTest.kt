package command.strategy

import com.ruslan.hlushan.core.command.strategy.AddToEndSingleStrategy
import com.ruslan.hlushan.core.extensions.withoutFirst
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

@SuppressWarnings("MaxLineLength")
class AddToEndSingleStrategyTest {

    @Test
    fun `beforeApply removes single instance of command class if there are another commands after in state and adds new command`() {
        val (oldState, incomingCommand) = `create state contains single instance of command class and another commands after`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.drop(1).plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply removes single instance of command class if there are another commands before in state and adds new command`() {
        val (oldState, incomingCommand) = `create state contains single instance of command class and another commands before`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.dropLast(1).plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply removes single instance of command class if there are another commands before and after in state and adds new command`() {
        val (oldState, incomingCommand) = `create state contains single instance of command class and another commands before and after`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.filter { command -> command.javaClass != incomingCommand.javaClass }.plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply removes single instance of command class if there are NO other commands in state and adds new command`() {
        val (oldState, incomingCommand) = `create state contains single instance of command class but NO other commands`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = listOf(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply not removes other than first instance of command class if there are another commands in state and adds new command`() {
        val (oldState, incomingCommand) = `create state contains many instances of command class and another commands`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.withoutFirst { command -> command.javaClass == incomingCommand.javaClass }.plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply not removes other than first instance of command class if there are NO other commands in state and adds new command`() {
        val (oldState, incomingCommand) = `create state contains many instances of command class but NO another commands`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.drop(1).plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply removes single equal command if there are another commands after in state and adds new command again`() {
        val (oldState, incomingCommand) = `create state contains single equal command and another commands after`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.drop(1).plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply removes single equal command if there are another commands before in state and adds new command again`() =
            `assert beforeApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before`()
            )

    @Test
    fun `beforeApply removes single equal command if there are another commands before and after in state and adds new command again`() {
        val (oldState, incomingCommand) = `create state contains single equal command and another commands before and after`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.filter { command -> command.javaClass != incomingCommand.javaClass }.plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply removes single equal command if there are NO other commands in state and adds new command again`() =
            `assert beforeApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command but NO other commands`()
            )

    @Suppress("MaxLineLength")
    @Test
    fun `beforeApply not removes other than first duplicated equal commands if state contains many duplicated equal commands and another commands and adds new command again`() =
            `assert beforeApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands but NO another commands`()
            )

    @Suppress("MaxLineLength")
    @Test
    fun `beforeApply not removes other than first duplicated equal commands if state contains just many duplicated equal commands and adds new command again`() {
        val (oldState, incomingCommand) = `create state contains many duplicated equal commands and another commands`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.withoutFirst { command -> command.javaClass == incomingCommand.javaClass }.plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply not mutate state if there is no instances of command class but there are another commands in state and adds new command`() {
        val (oldState, incomingCommand) = `create state contains NO instances of command class but there are another commands`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = oldState.plus(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `beforeApply just adds new command to empty state`() {
        val (oldState, incomingCommand) = `create empty state`()
        val newState = AddToEndSingleStrategy().beforeApply(oldState, incomingCommand)
        val expectedNewState = listOf(incomingCommand)
        assertNotEquals(oldState, newState)
        assertEquals(expectedNewState, newState)
    }

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands after`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands before`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands before and after`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before and after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class but NO other commands`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class but NO other commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many instances of command class and another commands`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains many instances of command class and another commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many instances of command class but NO another commands`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains many instances of command class but NO another commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command and another commands after`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command and another commands before`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command and another commands before and after`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before and after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command but NO other commands`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command but NO other commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many duplicated equal commands and another commands`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands and another commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many duplicated equal commands but NO other commands`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands but NO another commands`()
            )

    @Test
    fun `afterApply not mutate state if there is no instances of command class but there are other commands`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create state contains NO instances of command class but there are another commands`()
            )

    @Test
    fun `afterApply not mutate empty state`() =
            `assert afterApply not mutate state`(
                    strategy = AddToEndSingleStrategy(),
                    oldStateWithIncomingCommand = `create empty state`()
            )
}