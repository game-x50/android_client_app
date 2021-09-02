package utils.thread

import com.ruslan.hlushan.core.api.utils.thread.SingleThreadChecker
import com.ruslan.hlushan.core.api.utils.thread.checkThread
import com.ruslan.hlushan.test.utils.assertThrows
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class SingleThreadCheckerTest {

    @Test
    fun `assert default thread is valid`() =
            `assert thread is valid`(threadChecker = SingleThreadChecker())

    @Test
    fun `assert new thread is valid thread passed as param`() =
            `assert thread is valid`(threadChecker = SingleThreadChecker(thread = Thread.currentThread()))

    @Test
    fun `assert new thread is valid`() = thread {
        `assert thread is valid`(threadChecker = SingleThreadChecker())
    }.join()

    @Test
    fun `assert new thread is valid passed as param`() {
        var newThread: Thread? = null
        newThread = thread(start = false) {
            `assert thread is valid`(threadChecker = SingleThreadChecker(thread = newThread!!))
        }
        newThread.join()
    }

    @Test
    fun `assert new thread is valid but created in another thread`() {
        val threadCheckerReference = AtomicReference<SingleThreadChecker?>()
        val countDownLatch = CountDownLatch(2)

        val newThread = thread(start = false) {
            var threadChecker: SingleThreadChecker? = null
            do {
                threadChecker = threadCheckerReference.get()
            } while (threadChecker == null)

            assertTrue(threadChecker.isNeededThread)
            threadChecker.checkThread()

            countDownLatch.countDown()
        }

        thread {
            threadCheckerReference.set(SingleThreadChecker(thread = newThread))

            countDownLatch.countDown()
        }

        newThread.start()

        countDownLatch.await()
    }

    @Test
    fun `assert fail in another thread`() {
        val threadCheckerReference = AtomicReference<SingleThreadChecker?>()
        val countDownLatch = CountDownLatch(2)

        thread {
            threadCheckerReference.set(SingleThreadChecker())

            countDownLatch.countDown()
        }

        thread {
            var threadChecker: SingleThreadChecker? = null
            do {
                threadChecker = threadCheckerReference.get()
            } while (threadChecker == null)

            `assert invalid thread`(threadChecker)

            countDownLatch.countDown()
        }

        countDownLatch.await()
    }

    @Test
    fun `assert new thread fail because created for another thread`() {

        val newThread = Thread()

        thread {
            val threadChecker = SingleThreadChecker(thread = newThread)
            `assert invalid thread`(threadChecker)
        }.join()
    }
}

private fun `assert thread is valid`(threadChecker: SingleThreadChecker) {
    assertTrue(threadChecker.isNeededThread)
    threadChecker.checkThread()
}

private fun `assert invalid thread`(threadChecker: SingleThreadChecker) {
    assertFalse(threadChecker.isNeededThread)
    assertThrows(IllegalStateException::class) {
        threadChecker.checkThread()
    }
}