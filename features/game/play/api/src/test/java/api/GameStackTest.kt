package api

import com.ruslan.hlushan.game.api.play.dto.MatrixAndNewItemsState
import com.ruslan.hlushan.game.api.test.utils.generateFakeMatrixAndNewItemsState
import com.ruslan.hlushan.game.play.api.GameStack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStackTest {

    @Test
    fun empty() {
        assertStack(
                expected = emptyList(),
                limit = GameStack.DEFAULT_LIMIT,
                stack = GameStack.empty()
        )
    }

    @Test
    fun defaultSize() {
        assertStack(
                expected = emptyList(),
                limit = GameStack.DEFAULT_LIMIT,
                stack = GameStack()
        )
    }

    @Test
    fun negativeLimit() {
        repeat(10) { i ->
            assertStack(
                    expected = emptyList(),
                    limit = 1,
                    stack = GameStack(limit = (1 - i))
            )
        }
    }

    @Test
    fun iterateFewSetLastAndFewUndo() {
        val limit = GameStack.DEFAULT_LIMIT

        val stack = GameStack(limit = limit)

        assertFalse(stack.canUndo)

        repeat(10) { iteration ->

            repeat(limit) { addIteration ->
                stack.setLast(generateFakeMatrixAndNewItemsState())
                assertTrue(stack.canUndo)
            }

            repeat(limit - 1) { undoIteration ->
                stack.undo()
                assertTrue(stack.canUndo)
            }

            stack.undo()
            assertFalse(stack.canUndo)
        }
    }

    @Test
    fun iterateFewSetLastAndUndo() {
        val limit = GameStack.DEFAULT_LIMIT

        val stack = GameStack(limit = limit)

        assertFalse(stack.canUndo)

        repeat(2 * limit) { addIteration ->
            stack.setLast(generateFakeMatrixAndNewItemsState())
            assertTrue(stack.canUndo)
            stack.undo()
            assertFalse(stack.canUndo)
        }

        assertFalse(stack.canUndo)
        stack.setLast(generateFakeMatrixAndNewItemsState())
        assertTrue(stack.canUndo)
    }

    @Test
    fun undoIfCant() {
        val stack = GameStack.empty()

        repeat(10) {
            assertFalse(stack.canUndo)
            stack.undo()
            assertFalse(stack.canUndo)
        }
    }

    @Test
    fun addMoreThenLimit() {
        val limit = GameStack.DEFAULT_LIMIT

        val stack = GameStack(limit = limit)

        val expected = mutableListOf<MatrixAndNewItemsState>()

        repeat(limit) {
            val newItem = generateFakeMatrixAndNewItemsState()

            stack.setLast(newItem)

            expected.add(newItem)

            assertStack(
                    expected = expected,
                    limit = limit,
                    stack = stack
            )
        }

        repeat(2 * limit) {
            val newItem = generateFakeMatrixAndNewItemsState()
            stack.setLast(newItem)

            expected.removeAt(0)
            expected.add(newItem)

            assertStack(
                    expected = expected,
                    limit = limit,
                    stack = stack
            )
        }
    }

    @Test
    fun copyFromEmpty() {
        val limit = 16
        val current = GameStack(limit = limit)

        repeat(limit) {
            current.setLast(generateFakeMatrixAndNewItemsState())
        }

        val other = GameStack(limit = limit)
        assertCopyFromSameSizes(current = current, other = other)
    }

    @Test
    fun copyFromSmallerThenCurrentSizeSameLimits() {
        val limit = 16

        val current = GameStack(limit = limit)
        val other = GameStack(limit = limit)

        repeat(limit / 2) {
            current.setLast(generateFakeMatrixAndNewItemsState())
        }

        repeat(limit / 4) {
            other.setLast(generateFakeMatrixAndNewItemsState())
        }

        assertCopyFromSameSizes(current = current, other = other)
    }

    @Test
    fun copyFromSameSizeSmallerThenLimits() {
        val limit = GameStack.DEFAULT_LIMIT
        prepareAndAssertCopyFromSameSizes(limit = limit, stackSize = (limit / 2))
    }

    @Test
    fun copyFromSameSizeOnMaxLimit() {
        val limit = GameStack.DEFAULT_LIMIT
        prepareAndAssertCopyFromSameSizes(limit = limit, stackSize = limit)
    }

    @Test
    fun copyFromSmallerThenCurrentSizeAndOtherLimitSmallerThenCurrentLimit() {
        val currentLimit = 16
        val otherLimit = (currentLimit / 2)

        val current = GameStack(limit = currentLimit)
        val other = GameStack(limit = otherLimit)

        repeat(currentLimit / 2) {
            current.setLast(generateFakeMatrixAndNewItemsState())
        }

        repeat(otherLimit / 2) {
            other.setLast(generateFakeMatrixAndNewItemsState())
        }

        assertCopyFromSameSizes(current = current, other = other)
    }

    @Test
    fun copyFromOtherSizeGraterThenCurrentLimit() {
        val currentLimit = GameStack.DEFAULT_LIMIT
        val otherLimit = (2 * currentLimit)

        val current = GameStack(limit = currentLimit)
        val other = GameStack(limit = otherLimit)

        repeat(currentLimit / 2) {
            current.setLast(generateFakeMatrixAndNewItemsState())
        }

        repeat(otherLimit) {
            other.setLast(generateFakeMatrixAndNewItemsState())
        }

        val otherList = other.copyAsList()

        assertNotEquals(current.copyAsList().size, otherList.size)
        assertTrue(otherList.size > current.limit)

        current.copyFrom(otherList)
        assertEquals(current.copyAsList(), otherList.takeLast(current.limit))
    }

    private fun prepareAndAssertCopyFromSameSizes(limit: Int, stackSize: Int) {
        val current = GameStack(limit = limit)
        val other = GameStack(limit = limit)

        repeat(stackSize) {
            current.setLast(generateFakeMatrixAndNewItemsState())
            other.setLast(generateFakeMatrixAndNewItemsState())
        }

        assertCopyFromSameSizes(current = current, other = other)
    }

    private fun assertCopyFromSameSizes(current: GameStack, other: GameStack) {
        val otherList = other.copyAsList()

        assertNotEquals(current.copyAsList(), otherList)
        current.copyFrom(otherList)
        assertEquals(current.copyAsList(), otherList)
    }

    private fun assertStack(
            expected: List<MatrixAndNewItemsState>,
            limit: Int,
            stack: GameStack
    ) {
        assertEquals(expected.isNotEmpty(), stack.canUndo)
        assertEquals(expected, stack.copyAsList())
        assertEquals(limit, stack.limit)
    }
}