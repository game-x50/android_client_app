package presentation.viewmodel

import com.ruslan.hlushan.core.ui.api.test.utils.callOnCleared
import com.ruslan.hlushan.rxjava2.extensions.isActive
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseViewModelCreateEmptyDisposableTest : AbstractBaseViewModelDisposableTest() {

    @Test
    fun `joinedWhileViewAttachedEmptyDisposable is auto-disposed before onAfterAttachView`() =
            `disposable is auto-disposed before onAfterAttachView`(
                    isAutoDisposed = true,
                    this::createJoinedWhileViewAttachedEmptyDisposable
            )

    @Test
    fun `joinedUntilDestroyEmptyDisposable is NOT auto-disposed before onAfterAttachView`() =
            `disposable is auto-disposed before onAfterAttachView`(
                    isAutoDisposed = false,
                    this::createJoinedUntilDestroyEmptyDisposable
            )

    @Test
    fun `joinedWhileViewAttachedEmptyDisposable is NOT auto-disposed after onAfterAttachView`() =
            `disposable is NOT auto-disposed after onAfterAttachView`(
                    this::createJoinedWhileViewAttachedEmptyDisposable
            )

    @Test
    fun `joinedUntilDestroyEmptyDisposable is NOT auto-disposed after onAfterAttachView`() =
            `disposable is NOT auto-disposed after onAfterAttachView`(
                    this::createJoinedUntilDestroyEmptyDisposable
            )

    @Test
    fun `joinedWhileViewAttachedEmptyDisposable is auto-dispose after onAfterAttachView, onBeforeDetachView`() =
            `disposable is NOT auto-dispose after onAfterAttachView, onBeforeDetachView`(
                    isAutoDisposed = true,
                    this::createJoinedWhileViewAttachedEmptyDisposable
            )

    @Test
    fun `joinedUntilDestroyEmptyDisposable is NOT auto-dispose after onAfterAttachView, onBeforeDetachView`() =
            `disposable is NOT auto-dispose after onAfterAttachView, onBeforeDetachView`(
                    isAutoDisposed = false,
                    this::createJoinedUntilDestroyEmptyDisposable
            )

    @Test
    fun `joinedWhileViewAttachedEmptyDisposable is auto-dispose after just onClear`() =
            `disposable is auto-dispose after just onClear`(
                    this::createJoinedWhileViewAttachedEmptyDisposable
            )

    @Test
    fun `joinedUntilDestroyEmptyDisposable is auto-dispose after just onClear`() =
            `disposable is auto-dispose after just onClear`(
                    this::createJoinedUntilDestroyEmptyDisposable
            )

    @Suppress("MaxLineLength")
    @Test
    fun `joinedWhileViewAttachedEmptyDisposable is auto-dispose after onAfterAttachView, onBeforeDetachView, onClear`() =
            `disposable is auto-dispose after onAfterAttachView, onBeforeDetachView, onClear`(
                    this::createJoinedWhileViewAttachedEmptyDisposable
            )

    @Test
    fun `joinedUntilDestroyEmptyDisposable is auto-dispose after onAfterAttachView, onBeforeDetachView, onClear`() =
            `disposable is auto-dispose after onAfterAttachView, onBeforeDetachView, onClear`(
                    this::createJoinedUntilDestroyEmptyDisposable
            )

    @Suppress("MaxLineLength")
    @Test
    fun `joinedWhileViewAttachedEmptyDisposable is NOT auto-dispose after onAfterAttachView and dispose after onBeforeDetachView`() =
            `disposable is NOT auto-dispose after onAfterAttachView and dispose after onBeforeDetachView`(
                    isAutoDisposed = true,
                    this::createJoinedWhileViewAttachedEmptyDisposable
            )

    @Suppress("MaxLineLength")
    @Test
    fun `joinedUntilDestroyEmptyDisposable is NOT auto-dispose after onAfterAttachView and NOT dispose after onBeforeDetachView`() =
            `disposable is NOT auto-dispose after onAfterAttachView and dispose after onBeforeDetachView`(
                    isAutoDisposed = false,
                    this::createJoinedUntilDestroyEmptyDisposable
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `joinedWhileViewAttachedEmptyDisposable is NOT auto-dispose after onAfterAttachView, dispose after onBeforeDetachView, disposed after onClear`() =
            `disposable is NOT auto-dispose after onAfterAttachView, dispose after onBeforeDetachView, dispose after onClear`(
                    isDisposedAfterOnBeforeDetachView = true,
                    this::createJoinedWhileViewAttachedEmptyDisposable
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `joinedUntilDestroyEmptyDisposable is NOT auto-dispose after onAfterAttachView, NOT dispose after onBeforeDetachView, dispose after onClear`() =
            `disposable is NOT auto-dispose after onAfterAttachView, dispose after onBeforeDetachView, dispose after onClear`(
                    isDisposedAfterOnBeforeDetachView = false,
                    this::createJoinedUntilDestroyEmptyDisposable
            )

    @SuppressWarnings("MaxLineLength")
    @Test
    fun `joinedUntilDestroyEmptyDisposable is NOT auto-dispose before onAfterAttachView, NOT dispose after onBeforeDetachView, dispose after onClear`() {
        val allDisposables = mutableListOf<Disposable>()

        repeat(10) {
            val disposable: Disposable = createJoinedUntilDestroyEmptyDisposable()
            allDisposables.add(disposable)
        }

        repeat(10) {
            assertTrue(allDisposables.all { d -> d.isActive })

            viewModel.onAfterAttachView()
            assertTrue(allDisposables.all { d -> d.isActive })

            viewModel.onBeforeDetachView()
            assertTrue(allDisposables.all { d -> d.isActive })
        }

        viewModel.callOnCleared()
        assertTrue(allDisposables.all { d -> d.isDisposed })
    }

    private fun `disposable is auto-disposed before onAfterAttachView`(
            isAutoDisposed: Boolean,
            createAndJoin: () -> Disposable
    ) {
        val allDisposables = mutableListOf<Disposable>()

        repeat(10) {
            val disposable: Disposable = createAndJoin()

            allDisposables.add(disposable)

            assertEquals(isAutoDisposed, disposable.isDisposed)
        }

        assertEquals(isAutoDisposed, allDisposables.all { d -> d.isDisposed })
    }

    private fun `disposable is NOT auto-disposed after onAfterAttachView`(
            createAndJoin: () -> Disposable
    ) {
        viewModel.onAfterAttachView()

        val allDisposables = mutableListOf<Disposable>()

        repeat(10) {
            val disposable: Disposable = createAndJoin()

            allDisposables.add(disposable)

            assertTrue(disposable.isActive)
        }

        assertTrue(allDisposables.all { d -> d.isActive })
    }

    private fun `disposable is NOT auto-dispose after onAfterAttachView, onBeforeDetachView`(
            isAutoDisposed: Boolean,
            createAndJoin: () -> Disposable
    ) {
        val allDisposables = mutableListOf<Disposable>()

        repeat(10) {
            viewModel.onAfterAttachView()
            viewModel.onBeforeDetachView()

            val disposable: Disposable = createAndJoin()

            allDisposables.add(disposable)

            assertEquals(isAutoDisposed, disposable.isDisposed)
        }

        assertEquals(isAutoDisposed, allDisposables.all { d -> d.isDisposed })
    }

    private fun `disposable is auto-dispose after just onClear`(
            createAndJoin: () -> Disposable
    ) {
        viewModel.callOnCleared()

        repeat(10) {
            val disposable: Disposable = createAndJoin()
            assertTrue(disposable.isDisposed)
        }
    }

    private fun `disposable is auto-dispose after onAfterAttachView, onBeforeDetachView, onClear`(
            createAndJoin: () -> Disposable
    ) {

        viewModel.onAfterAttachView()
        viewModel.onBeforeDetachView()

        viewModel.callOnCleared()

        repeat(10) {
            val disposable: Disposable = createAndJoin()
            assertTrue(disposable.isDisposed)
        }
    }

    private fun `disposable is NOT auto-dispose after onAfterAttachView and dispose after onBeforeDetachView`(
            isAutoDisposed: Boolean,
            createAndJoin: () -> Disposable
    ) {
        repeat(10) {
            viewModel.onAfterAttachView()

            val disposable: Disposable = createAndJoin()
            assertTrue(disposable.isActive)

            viewModel.onBeforeDetachView()
            assertEquals(isAutoDisposed, disposable.isDisposed)
        }
    }

    @Suppress("MaxLineLength")
    private fun `disposable is NOT auto-dispose after onAfterAttachView, dispose after onBeforeDetachView, dispose after onClear`(
            isDisposedAfterOnBeforeDetachView: Boolean,
            createAndJoin: () -> Disposable
    ) {
        val allDisposables = mutableListOf<Disposable>()

        repeat(10) {
            viewModel.onAfterAttachView()

            val disposable: Disposable = createAndJoin()

            allDisposables.add(disposable)

            assertTrue(disposable.isActive)

            viewModel.onBeforeDetachView()
            assertEquals(isDisposedAfterOnBeforeDetachView, disposable.isDisposed)
        }

        viewModel.callOnCleared()
        assertTrue(allDisposables.all { d -> d.isDisposed })
    }

    private fun createJoinedWhileViewAttachedEmptyDisposable(): Disposable {
        val disposable = Disposables.empty()
        viewModel.callJoinWhileViewAttached(disposable)
        return disposable
    }

    private fun createJoinedUntilDestroyEmptyDisposable(): Disposable {
        val disposable = Disposables.empty()
        viewModel.callJoinUntilDestroy(disposable)
        return disposable
    }
}