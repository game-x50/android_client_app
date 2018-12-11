package presentation.command.stub

import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.api.presentation.command.strategy.StrategyCommand

class StrategyCommandStub(
        private val producedStrategy: HandleStrategy
) : StrategyCommand {

    override fun produceStrategy(): HandleStrategy = producedStrategy
}