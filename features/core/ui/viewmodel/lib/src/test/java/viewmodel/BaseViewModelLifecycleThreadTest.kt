package viewmodel

import com.ruslan.hlushan.core.api.test.utils.thread.ThreadCheckerMock
import com.ruslan.hlushan.core.ui.viewmodel.test.utils.callOnCleared
import com.ruslan.hlushan.test.utils.assertThrows
import org.junit.Before
import org.junit.Test
import viewmodel.stub.BaseViewModelTestImpl

class BaseViewModelLifecycleThreadTest {

    private lateinit var threadChecker: ThreadCheckerMock
    private lateinit var viewModel: BaseViewModelTestImpl

    @Before
    fun before() {
        threadChecker = ThreadCheckerMock(defaultIsNeededThread = true)

        viewModel = createViewModel()
    }

    @Test
    fun `init`() {
        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            createViewModel()
        }
    }

    @Test
    fun `init, onAfterAttachView`() {
        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            viewModel.onAfterAttachView()
        }
    }

    @Test
    fun `init, onCleared`() {
        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            viewModel.callOnCleared()
        }
    }

    @Test
    fun `init, onAfterAttachView, onBeforeDetachView`() {
        viewModel.onAfterAttachView()

        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            viewModel.onBeforeDetachView()
        }
    }

    @Test
    fun `init, repeat(onAfterAttachView, onBeforeDetachView), onAfterAttachView`() {
        repeat(10) {
            viewModel.onAfterAttachView()
            viewModel.onBeforeDetachView()
        }

        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            viewModel.onAfterAttachView()
        }
    }

    @Test
    fun `init, repeat(onAfterAttachView, onBeforeDetachView), onBeforeDetachView`() {
        repeat(10) {
            viewModel.onAfterAttachView()
            viewModel.onBeforeDetachView()
        }

        viewModel.onAfterAttachView()

        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            viewModel.onBeforeDetachView()
        }
    }

    @Test
    fun `init, onAfterAttachView, onBeforeDetachView, onCleared`() {
        viewModel.onAfterAttachView()
        viewModel.onBeforeDetachView()

        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            viewModel.callOnCleared()
        }
    }

    @Test
    fun `init, repeat(onAfterAttachView, onBeforeDetachView), onCleared`() {
        repeat(10) {
            viewModel.onAfterAttachView()
            viewModel.onBeforeDetachView()
        }

        threadChecker.isNeededThread = false

        assertThrows(IllegalStateException::class) {
            viewModel.callOnCleared()
        }
    }

    private fun createViewModel(): BaseViewModelTestImpl = BaseViewModelTestImpl(
            threadChecker = threadChecker
    )
}