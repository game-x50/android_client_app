package viewmodel

import com.ruslan.hlushan.core.ui.viewmodel.test.utils.callOnCleared
import com.ruslan.hlushan.third_party.rxjava2.extensions.isActive
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

@Suppress("MaxLineLength")
class BaseViewModelCreateTimerDisposableTest : AbstractBaseViewModelDisposableTest() {

    @Test
    fun `timer joinedWhileViewAttachedDisposable launched after onAfterAttachView with self dispose before onBeforeDetachView`() {
        viewModel.onAfterAttachView()

        val disposable = createJoinedWhileViewAttachedTimerDisposable()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.onBeforeDetachView()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched before onAfterAttachView self dispose before onAfterAttachView`() {
        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.onAfterAttachView()
        assertTrue(disposable.isDisposed)

        viewModel.onBeforeDetachView()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched before onAfterAttachView self dispose after onAfterAttachView and before onBeforeDetachView`() {
        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        viewModel.onAfterAttachView()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.onBeforeDetachView()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched before onAfterAttachView self dispose after onAfterAttachView, onBeforeDetachView and before onCleared`() {
        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        viewModel.onAfterAttachView()
        assertTrue(disposable.isActive)

        viewModel.onBeforeDetachView()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched before onAfterAttachView self dispose after repeat(onAfterAttachView, onBeforeDetachView) and before onCleared`() {
        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        repeat(10) {
            viewModel.onAfterAttachView()
            assertTrue(disposable.isActive)

            viewModel.onBeforeDetachView()
            assertTrue(disposable.isActive)
        }

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched before onAfterAttachView self dispose before onCleared`() {
        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched after onAfterAttachView and before onBeforeDetachView self dispose before onBeforeDetachView`() {
        viewModel.onAfterAttachView()

        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.onBeforeDetachView()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched after onAfterAttachView and before onBeforeDetachView self dispose after onBeforeDetachView and before onCleared`() {
        viewModel.onAfterAttachView()

        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        viewModel.onBeforeDetachView()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched after onAfterAttachView, onBeforeDetachView and before onCleared self dispose before onCleared`() {
        viewModel.onAfterAttachView()
        viewModel.onBeforeDetachView()

        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched after repeat(onAfterAttachView, onBeforeDetachView) and before onCleared self dispose before onCleared`() {
        repeat(10) {
            viewModel.onAfterAttachView()
            viewModel.onBeforeDetachView()
        }

        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun `timer joinedUntilDestroyEmptyDisposable launched before onCleared with self dispose before onCleared`() {
        val disposable = createJoinedUntilDestroyTimerDisposable()
        assertTrue(disposable.isActive)

        advanceTimeByDelay()
        assertTrue(disposable.isDisposed)

        viewModel.callOnCleared()
        assertTrue(disposable.isDisposed)
    }

    private fun createTimerDisposable(): Disposable =
            Single.timer(DELAY, TIME_UNIT, testScheduler)
                    .subscribe()

    private fun advanceTimeByDelay() {
        testScheduler.advanceTimeBy(DELAY, TIME_UNIT)
    }

    private fun createJoinedWhileViewAttachedTimerDisposable(): Disposable {
        val disposable = createTimerDisposable()
        viewModel.callJoinWhileViewAttached(disposable)
        return disposable
    }

    private fun createJoinedUntilDestroyTimerDisposable(): Disposable {
        val disposable = createTimerDisposable()
        viewModel.callJoinUntilDestroy(disposable)
        return disposable
    }

    companion object {
        const val DELAY = 1_000L
        val TIME_UNIT = TimeUnit.MILLISECONDS
    }
}