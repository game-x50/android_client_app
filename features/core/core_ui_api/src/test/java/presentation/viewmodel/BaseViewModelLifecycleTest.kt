package presentation.viewmodel

import com.ruslan.hlushan.core.api.test.utils.utils.thread.ThreadCheckerStub
import com.ruslan.hlushan.test.utils.assertThrows
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import presentation.viewmodel.stub.BaseViewModelTestImpl

class BaseViewModelLifecycleTest {

    private lateinit var viewModel: BaseViewModelTestImpl

    @Before
    fun before() {
        val threadChecker = ThreadCheckerStub(isNeededThread = true)

        viewModel = BaseViewModelTestImpl(
                threadChecker = threadChecker
        )
    }

    @Test
    fun `init`() {
        assertEquals(0, viewModel.onAfterFirstAttachViewCalledTimes)
    }

    @Test
    fun `init, onAfterAttachView`() {
        viewModel.onAfterAttachView()
        assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
    }

    @Test
    fun `init, onBeforeDetachView`() =
            assertThrows(IllegalStateException::class) {
                viewModel.onBeforeDetachView()
            }

    @Test
    fun `init, onCleared`() {
        viewModel.callOnCleared()
        assertEquals(0, viewModel.onAfterFirstAttachViewCalledTimes)
    }

    @Test
    fun `init, onAfterAttachView, onBeforeDetachView`() {
        viewModel.onAfterAttachView()
        assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
        viewModel.onBeforeDetachView()
        assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
    }

    @Test
    fun `init, repeat(onAfterAttachView, onBeforeDetachView)`() {
        repeat(10) {
            viewModel.onAfterAttachView()
            assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
            viewModel.onBeforeDetachView()
            assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
        }
    }

    @Test
    fun `init, onAfterAttachView, onBeforeDetachView, onCleared`() {
        viewModel.onAfterAttachView()
        assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
        viewModel.onBeforeDetachView()
        assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
        viewModel.callOnCleared()
        assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
    }

    @Test
    fun `init, repeat(onAfterAttachView, onBeforeDetachView), onCleared`() {
        repeat(10) {
            viewModel.onAfterAttachView()
            assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
            viewModel.onBeforeDetachView()
            assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
        }
        viewModel.callOnCleared()
        assertEquals(1, viewModel.onAfterFirstAttachViewCalledTimes)
    }

    @Test
    fun `init, onAfterAttachView, onAfterAttachView`() {
        viewModel.onAfterAttachView()

        assertThrows(IllegalStateException::class) {
            viewModel.onAfterAttachView()
        }
    }

    @Test
    fun `init, onAfterAttachView, onBeforeDetachView, onBeforeDetachView`() {
        viewModel.onAfterAttachView()
        viewModel.onBeforeDetachView()

        assertThrows(IllegalStateException::class) {
            viewModel.onBeforeDetachView()
        }
    }

    @Test
    fun `init, onCleared, onCleared`() {
        viewModel.callOnCleared()

        assertThrows(IllegalStateException::class) {
            viewModel.callOnCleared()
        }
    }

    @Test
    fun `init, onCleared, onAfterAttachView`() {
        viewModel.callOnCleared()

        assertThrows(IllegalStateException::class) {
            viewModel.onAfterAttachView()
        }
    }

    @Test
    fun `init, onCleared, onBeforeDetachView`() {
        viewModel.callOnCleared()

        assertThrows(IllegalStateException::class) {
            viewModel.onBeforeDetachView()
        }
    }
}