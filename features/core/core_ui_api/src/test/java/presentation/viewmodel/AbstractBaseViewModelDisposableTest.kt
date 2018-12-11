package presentation.viewmodel

import com.ruslan.hlushan.core.api.test.utils.utils.thread.ThreadCheckerStub
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import presentation.viewmodel.stub.BaseViewModelTestImpl

abstract class AbstractBaseViewModelDisposableTest {

    protected lateinit var testScheduler: TestScheduler
    protected lateinit var viewModel: BaseViewModelTestImpl

    @Before
    fun before() {
        val threadChecker = ThreadCheckerStub(isNeededThread = true)

        testScheduler = TestScheduler()

        viewModel = BaseViewModelTestImpl(
                threadChecker = threadChecker
        )
    }

    @After
    fun after() {
        testScheduler.triggerActions()
        viewModel.cleanUpAll()
    }
}