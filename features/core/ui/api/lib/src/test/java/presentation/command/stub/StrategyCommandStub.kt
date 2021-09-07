package presentation.command.stub

import com.ruslan.hlushan.core.ui.viewmodel.command.strategy.HandleStrategy
import com.ruslan.hlushan.core.ui.viewmodel.command.strategy.StrategyCommand

class StrategyCommandStub(
        private val producedStrategy: HandleStrategy
) : StrategyCommand {

    override fun produceStrategy(): HandleStrategy = producedStrategy
}