package presentation.command.strategy

import com.ruslan.hlushan.core.ui.viewmodel.command.strategy.OneExecutionStateStrategy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

@SuppressWarnings("MaxLineLength")
class OneExecutionStateStrategyTest {

    @Test
    fun `beforeApply not mutate state if state contains single instance of command class and another commands after in state and adds new command`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands after`()
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `beforeApply not mutate state if state contains single instance of command class and another commands before in state and adds new command`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before`()
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `beforeApply not mutate state if state contains single instance of command class and another commands before and after in state and adds new command`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before and after`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single instance of command class but NO other commands in state and adds new command`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class but NO other commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains many instances of command class and another commands in state and adds new command`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains many instances of command class and another commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains many instances of command class but NO other commands in state and adds new command`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains many instances of command class but NO another commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains another commands after in state and adds new command again`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands after`()
            )

    @Test
    fun `beforeApply not mutate state if state contains another commands before in state and adds new command again`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before`()
            )

    @Test
    fun `beforeApply not mutate state if state contains another commands before and after in state and adds new command again`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before and after`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single equal command but there are NO other commands in state and adds new command again`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command but NO other commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains many duplicated equal commands and another commands and adds new command again`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands and another commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains just many duplicated equal commands and adds new command again`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands but NO another commands`()
            )

    @Test
    fun `beforeApply not mutate state if there is no instances of command class but there are another commands and adds new command`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create state contains NO instances of command class but there are another commands`()
            )

    @Test
    fun `beforeApply just adds new command to empty state`() =
            `assert beforeApply not mutate state and adds incoming command`(
                    oldStateWithIncomingCommand = `create empty state`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands after`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands after`(),
                    isOldStateEqualToNew = true
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands before`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before`(),
                    isOldStateEqualToNew = true
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands before and after`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before and after`(),
                    isOldStateEqualToNew = true
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class but NO other commands`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single instance of command class but NO other commands`(),
                    isOldStateEqualToNew = true
            )

    @Test
    fun `afterApply not mutate state if state contains many instances of command class and another commands`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains many instances of command class and another commands`(),
                    isOldStateEqualToNew = true
            )

    @Test
    fun `afterApply not mutate state if state contains many instances of command class but NO other commands`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains many instances of command class but NO another commands`(),
                    isOldStateEqualToNew = true
            )

    @Test
    fun `afterApply removes single equal command if state contains another commands after`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands after`(),
                    isOldStateEqualToNew = false
            )

    @Test
    fun `afterApply removes single equal command if state contains another commands before`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before`(),
                    isOldStateEqualToNew = false
            )

    @Test
    fun `afterApply removes single equal command if state contains another commands before and after`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before and after`(),
                    isOldStateEqualToNew = false
            )

    @Test
    fun `afterApply removes single equal command if there are NO other commands`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains single equal command but NO other commands`(),
                    isOldStateEqualToNew = false
            )

    @Test
    fun `afterApply removes just first equal command if state contains many duplicated equal commands and another commands`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands and another commands`(),
                    isOldStateEqualToNew = false
            )

    @Test
    fun `afterApply removes just first equal command if state contains many duplicated equal commands but NO other commands`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands but NO another commands`(),
                    isOldStateEqualToNew = false
            )

    @Test
    fun `afterApply not mutate state if there is no instances of command class but there are another commands`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create state contains NO instances of command class but there are another commands`(),
                    isOldStateEqualToNew = true
            )

    @Test
    fun `afterApply not mutate empty state`() =
            `assert afterApply not mutate state and removes incoming command`(
                    oldStateWithIncomingCommand = `create empty state`(),
                    isOldStateEqualToNew = true
            )
}

private fun `assert beforeApply not mutate state and adds incoming command`(
        oldStateWithIncomingCommand: OldStateWithIncomingCommand
) {
    val newState = OneExecutionStateStrategy().beforeApply(
            oldStateWithIncomingCommand.oldState,
            oldStateWithIncomingCommand.incomingCommand
    )
    val expectedNewState = oldStateWithIncomingCommand.oldState.plus(oldStateWithIncomingCommand.incomingCommand)
    assertNotEquals(oldStateWithIncomingCommand.oldState, newState)
    assertEquals(expectedNewState, newState)
}

private fun `assert afterApply not mutate state and removes incoming command`(
        oldStateWithIncomingCommand: OldStateWithIncomingCommand,
        isOldStateEqualToNew: Boolean
) {
    val newState = OneExecutionStateStrategy().afterApply(
            oldStateWithIncomingCommand.oldState,
            oldStateWithIncomingCommand.incomingCommand
    )
    val expectedNewState = oldStateWithIncomingCommand.oldState.minus(oldStateWithIncomingCommand.incomingCommand)
    assertEquals(isOldStateEqualToNew, (oldStateWithIncomingCommand.oldState == newState))
    assertEquals(expectedNewState, newState)
}