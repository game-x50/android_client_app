package command.strategy

import com.ruslan.hlushan.core.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.command.strategy.StrategyCommand

class StrategyCommandStub(
        private val producedStrategy: HandleStrategy
) : StrategyCommand {

    override fun produceStrategy(): HandleStrategy = producedStrategy
}