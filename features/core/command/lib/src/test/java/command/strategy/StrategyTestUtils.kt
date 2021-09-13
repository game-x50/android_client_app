@file:SuppressWarnings("MaxLineLength")

package command.strategy

import com.ruslan.hlushan.core.command.MutableCommandQueue
import com.ruslan.hlushan.core.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.command.strategy.OneExecutionStateStrategy
import com.ruslan.hlushan.core.command.strategy.StrategyCommand
import com.ruslan.hlushan.extensions.addAsFirstTo
import org.junit.Assert.assertEquals

fun `create state contains single instance of command class and another commands after`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = CommandImpl().addAsFirstTo((1..3).map { AnotherCommandImpl() }),
                incomingCommand = CommandImpl()
        )

fun `create state contains single instance of command class and another commands before`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = ((1..3).map { AnotherCommandImpl() }).plus(CommandImpl()),
                incomingCommand = CommandImpl()
        )

fun `create state contains single instance of command class and another commands before and after`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = ((1..3).map { AnotherCommandImpl() }).plus(CommandImpl()).plus((1..3).map { AnotherCommandImpl() }),
                incomingCommand = CommandImpl()
        )

fun `create state contains single instance of command class but NO other commands`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = listOf(CommandImpl()),
                incomingCommand = CommandImpl()
        )

fun `create state contains many instances of command class and another commands`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = listOf(AnotherCommandImpl(), CommandImpl(), AnotherCommandImpl(), CommandImpl(), AnotherCommandImpl()),
                incomingCommand = CommandImpl()
        )

fun `create state contains many instances of command class but NO another commands`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = (1..3).map { CommandImpl() },
                incomingCommand = CommandImpl()
        )

fun `create state contains single equal command and another commands after`(): OldStateWithIncomingCommand {
    val command = CommandImpl()

    return OldStateWithIncomingCommand(
            oldState = command.addAsFirstTo((1..3).map { AnotherCommandImpl() }),
            incomingCommand = command
    )
}

fun `create state contains single equal command and another commands before`(): OldStateWithIncomingCommand {
    val command = CommandImpl()

    return OldStateWithIncomingCommand(
            oldState = ((1..3).map { AnotherCommandImpl() }).plus(command),
            incomingCommand = command
    )
}

fun `create state contains single equal command and another commands before and after`(): OldStateWithIncomingCommand {
    val command = CommandImpl()

    return OldStateWithIncomingCommand(
            oldState = ((1..3).map { AnotherCommandImpl() }).plus(command).plus((1..3).map { AnotherCommandImpl() }),
            incomingCommand = command
    )
}

fun `create state contains single equal command but NO other commands`(): OldStateWithIncomingCommand {
    val command = CommandImpl()

    return OldStateWithIncomingCommand(
            oldState = listOf(command),
            incomingCommand = command
    )
}

fun `create state contains many duplicated equal commands and another commands`(): OldStateWithIncomingCommand {
    val command = CommandImpl()

    return OldStateWithIncomingCommand(
            oldState = listOf(AnotherCommandImpl(), command, AnotherCommandImpl(), command, AnotherCommandImpl()),
            incomingCommand = command
    )
}

fun `create state contains many duplicated equal commands but NO another commands`(): OldStateWithIncomingCommand {
    val command = CommandImpl()

    return OldStateWithIncomingCommand(
            oldState = (1..3).map { command },
            incomingCommand = command
    )
}

fun `create state contains NO instances of command class but there are another commands`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = (1..3).map { AnotherCommandImpl() },
                incomingCommand = CommandImpl()
        )

fun `create empty state`(): OldStateWithIncomingCommand =
        OldStateWithIncomingCommand(
                oldState = emptyList(),
                incomingCommand = CommandImpl()
        )

fun `assert beforeApply not mutate state`(
        strategy: HandleStrategy,
        oldStateWithIncomingCommand: OldStateWithIncomingCommand
) {
    val newState = strategy.beforeApply(oldStateWithIncomingCommand.oldState, oldStateWithIncomingCommand.incomingCommand)
    assertEquals(oldStateWithIncomingCommand.oldState, newState)
}

fun `assert afterApply not mutate state`(
        strategy: HandleStrategy,
        oldStateWithIncomingCommand: OldStateWithIncomingCommand
) {
    val newState = strategy.afterApply(oldStateWithIncomingCommand.oldState, oldStateWithIncomingCommand.incomingCommand)
    assertEquals(oldStateWithIncomingCommand.oldState, newState)
}

data class OldStateWithIncomingCommand(
        val oldState: List<StrategyCommand>,
        val incomingCommand: StrategyCommand
)

fun `create NOT empty CommandQueue`(): MutableCommandQueue<StrategyCommand> {
    val commandQueue = MutableCommandQueue<StrategyCommand>()

    repeat(2) { _ ->
        val command = `create default real command`()
        commandQueue.add(command)
    }

    return commandQueue
}

fun `create default real command`(): StrategyCommandStub =
        StrategyCommandStub(OneExecutionStateStrategy())

fun `create empty default command`(
        returnBeforeApply: List<StrategyCommand>? = null,
        returnAfterApply: List<StrategyCommand>? = null
): StrategyCommandStub {
    val strategy = HandleStrategyStub(returnBeforeApply = returnBeforeApply, returnAfterApply = returnAfterApply)
    return StrategyCommandStub(strategy)
}

fun `generate new state of empty commands`(size: Int): List<StrategyCommand> =
        (1..size).map { `create empty default command`() }

private class CommandImpl : StrategyCommand {
    override fun produceStrategy(): HandleStrategy = throw IllegalStateException()
}

private class AnotherCommandImpl : StrategyCommand {
    override fun produceStrategy(): HandleStrategy = throw IllegalStateException()
}