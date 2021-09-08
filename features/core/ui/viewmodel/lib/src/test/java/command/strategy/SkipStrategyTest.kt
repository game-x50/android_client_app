package command.strategy

import com.ruslan.hlushan.core.ui.viewmodel.command.strategy.SkipStrategy
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`assert afterApply not mutate state`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`assert beforeApply not mutate state`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create empty state`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains NO instances of command class but there are another commands`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains many duplicated equal commands and another commands`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains many duplicated equal commands but NO another commands`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains many instances of command class and another commands`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains many instances of command class but NO another commands`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single equal command and another commands after`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single equal command and another commands before and after`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single equal command and another commands before`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single equal command but NO other commands`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single instance of command class and another commands after`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single instance of command class and another commands before and after`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single instance of command class and another commands before`
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.strategy.`create state contains single instance of command class but NO other commands`
import org.junit.Test

@SuppressWarnings("MaxLineLength")
class SkipStrategyTest {

    @Test
    fun `beforeApply not mutate state if state contains single instance of command class and another commands after`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands after`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single instance of command class and another commands before`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single instance of command class and another commands before and after`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before and after`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single instance of command class but NO other commands`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class but NO other commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains many instances of command class and another commands`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many instances of command class and another commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains many instances of command class but NO another commands`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many instances of command class but NO another commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single equal command and another commands after`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands after`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single equal command and another commands before`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single equal command and another commands before and after`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before and after`()
            )

    @Test
    fun `beforeApply not mutate state if state contains single equal command but NO other commands`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command but NO other commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains many duplicated equal commands and another commands`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands and another commands`()
            )

    @Test
    fun `beforeApply not mutate state if state contains many duplicated equal commands but NO another commands`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands but NO another commands`()
            )

    @Test
    fun `beforeApply not mutate state if there is no instances of command class but there are another commands`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains NO instances of command class but there are another commands`()
            )

    @Test
    fun `beforeApply not mutate empty state`() =
            `assert beforeApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create empty state`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands after`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands before`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class and another commands before and after`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class and another commands before and after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single instance of command class but NO other commands`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single instance of command class but NO other commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many instances of command class and another commands`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many instances of command class and another commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many instances of command class but NO another commands`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many instances of command class but NO another commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command and another commands after`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command and another commands before`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command and another commands before and after`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command and another commands before and after`()
            )

    @Test
    fun `afterApply not mutate state if state contains single equal command but NO other commands`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains single equal command but NO other commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many duplicated equal commands and another commands`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands and another commands`()
            )

    @Test
    fun `afterApply not mutate state if state contains many duplicated equal commands but NO another commands`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains many duplicated equal commands but NO another commands`()
            )

    @Test
    fun `afterApply not mutate state if there is no instances of command class but there are another commands`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create state contains NO instances of command class but there are another commands`()
            )

    @Test
    fun `afterApply not mutate empty state`() =
            `assert afterApply not mutate state`(
                    strategy = SkipStrategy(),
                    oldStateWithIncomingCommand = `create empty state`()
            )
}