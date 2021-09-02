package sync.play.base

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.api.play.dto.GameRecord
import com.ruslan.hlushan.game.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.api.play.dto.toLocalDeletedOrThrow
import com.ruslan.hlushan.game.api.play.dto.toModifyingNowOrThrow
import com.ruslan.hlushan.game.api.play.dto.toNextModifiedAfterModifyingOrThrow
import com.ruslan.hlushan.game.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import generateAndAddLocalUpdatedToLocalRepo
import org.junit.Assert.assertNotEquals

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
internal abstract class BasePlayRecordsInteractorImplLocallyUpdatedTest : BasePlayRecordsInteractorImplTest() {

    protected fun baseTestLocallyUpdatedUpdateAndGetRecordForPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.updateAndGetRecordForPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(original.record)

        val expectedSyncState = original.syncState.toModifyingNowOrThrow()
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    protected fun baseTestLocallyUpdatedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.updateAndGetRecordForPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(original.record)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyUpdatedMarkAsNonPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.markAsNonPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyUpdatedMarkAsNonPlayingAsynchronously(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.markAsNonPlayingAsynchronously(original.record.id)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyUpdatedStartedModifyingMarkAsNonPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.markAsNonPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        val expectedSyncState = original.syncState.copy(modifyingNow = false)
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    protected fun baseTestLocallyUpdatedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.markAsNonPlayingAsynchronously(original.record.id)

        val expectedSyncState = original.syncState.copy(modifyingNow = false)
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    protected fun baseTestLocallyUpdatedRemove(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.removeRecordById(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        val updatedLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        val expectedSyncState = original.syncState.toLocalDeletedOrThrow(newLocalActionId = updatedLocalActionId)
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    protected fun baseTestLocallyUpdatedStartedModifyingRemove(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.removeRecordById(original.record.id)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyUpdatedUpdateRecordAfterPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.updateRecordAfterPlaying(
                id = original.record.id,
                gameState = generateFakeGameState(),
                totalPlayed = generateFakeDuration(),
                localModifiedTimestamp = generateFakeInstantTimestamp()
        )
                .test()
                .assertNotComplete()
                .assertError(java.lang.IllegalStateException::class.java)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    @SuppressWarnings("MaxLineLength")
    protected fun baseTestLocallyUpdatedStartedModifyingUpdateRecordAfterPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalUpdatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val updatedLastLocalModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateRecordAfterPlaying(
                id = original.record.id,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed,
                localModifiedTimestamp = updatedLastLocalModifiedTimestamp
        )
                .test()
                .assertComplete()
                .assertNoErrors()

        val updatedLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        assertNotEquals(updatedLocalActionId, original.syncState.localAction?.actionId)

        val expectedSyncState = original.syncState.toNextModifiedAfterModifyingOrThrow(
                newLocalActionId = updatedLocalActionId,
                newLastLocalModifiedTimestamp = updatedLastLocalModifiedTimestamp
        )
        val expectedRecord = GameRecord(original.record.id, updatedGameState, updatedTotalPlayed)
        val expected = GameRecordWithSyncState(record = expectedRecord, syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }
}