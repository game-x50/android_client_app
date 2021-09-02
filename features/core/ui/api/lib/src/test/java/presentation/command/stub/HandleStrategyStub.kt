package presentation.command.stub

import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand

class HandleStrategyStub(
        private val returnBeforeApply: List<StrategyCommand>?,
        private val returnAfterApply: List<StrategyCommand>?
) : HandleStrategy {

    var passedBeforeApplyCurrentState: List<Any>? = null
        private set
    var passedBeforeApplyIncomingCommand: Any? = null
        private set

    var passedAfterApplyCurrentState: List<Any>? = null
        private set
    var passedAfterApplyIncomingCommand: Any? = null
        private set

    override fun <Command : Any> beforeApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> {
        passedBeforeApplyCurrentState = currentState
        passedBeforeApplyIncomingCommand = incomingCommand

        return (returnBeforeApply as List<Command>)
    }

    override fun <Command : Any> afterApply(
            currentState: List<Command>,
            incomingCommand: Command
    ): List<Command> {
        passedAfterApplyCurrentState = currentState
        passedAfterApplyIncomingCommand = incomingCommand

        return (returnAfterApply as List<Command>)
    }
}