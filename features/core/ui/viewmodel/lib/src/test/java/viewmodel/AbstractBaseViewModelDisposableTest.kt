package viewmodel

import com.ruslan.hlushan.core.thread.test.utils.ThreadCheckerStub
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.TestBaseViewModelCleanUpRule
import com.ruslan.hlushan.third_party.rxjava2.test.utils.TestSchedulerCleanUpRule
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Rule
import viewmodel.stub.BaseViewModelTestImpl

abstract class AbstractBaseViewModelDisposableTest {

    protected lateinit var testScheduler: TestScheduler
    protected lateinit var viewModel: BaseViewModelTestImpl

    @Rule
    @JvmField
    val testSchedulerCleanUpRule = TestSchedulerCleanUpRule { testScheduler }

    @Rule
    @JvmField
    val baseViewModelCleanUpRule = TestBaseViewModelCleanUpRule { viewModel }

    @Before
    fun before() {
        val threadChecker = ThreadCheckerStub(isNeededThread = true)

        testScheduler = TestScheduler()

        viewModel = BaseViewModelTestImpl(
                threadChecker = threadChecker
        )
    }
}