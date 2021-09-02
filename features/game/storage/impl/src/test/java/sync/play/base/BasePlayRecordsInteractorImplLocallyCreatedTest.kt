package sync.play.base

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.toModifyingNowOrThrow
import com.ruslan.hlushan.game.core.api.play.dto.toNextModifiedAfterModifyingOrThrow
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import generateAndAddLocalCreatedToLocalRepo
import org.junit.Assert.assertNotEquals

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
internal abstract class BasePlayRecordsInteractorImplLocallyCreatedTest : BasePlayRecordsInteractorImplTest() {

    protected fun baseTestLocallyCreatedUpdateAndGetRecordForPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.updateAndGetRecordForPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(original.record)

        val expectedSyncState = original.syncState.toModifyingNowOrThrow()
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    protected fun baseTestLocallyCreatedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.updateAndGetRecordForPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(original.record)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyCreatedMarkAsNonPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.markAsNonPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyCreatedMarkAsNonPlayingAsynchronously(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.markAsNonPlayingAsynchronously(original.record.id)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyCreatedStartedModifyingMarkAsNonPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.markAsNonPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        val expectedSyncState = original.syncState.copy(modifyingNow = false)
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    protected fun baseTestLocallyCreatedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.markAsNonPlayingAsynchronously(original.record.id)

        val expectedSyncState = original.syncState.copy(modifyingNow = false)
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    protected fun baseTestLocallyCreatedStartedModifyingRemove(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        playRecordsInteractor.removeRecordById(original.record.id)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyCreatedUpdateRecordAfterPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = false)

        playRecordsInteractor.updateRecordAfterPlaying(
                id = original.record.id,
                gameState = generateFakeGameState(),
                totalPlayed = generateFakeDuration(),
                localModifiedTimestamp = generateFakeInstantTimestamp()
        )
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyCreatedStartedModifyingUpdateRecordAfterPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = syncingNow, modifyingNow = true)

        val updatedGameState = generateFakeGameState()
        val updatedTotalPlayed = generateFakeDuration()
        val localModifiedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.updateRecordAfterPlaying(
                id = original.record.id,
                gameState = updatedGameState,
                totalPlayed = updatedTotalPlayed,
                localModifiedTimestamp = localModifiedTimestamp
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
                newLastLocalModifiedTimestamp = localModifiedTimestamp
        )
        val expectedRecord = GameRecord(original.record.id, updatedGameState, updatedTotalPlayed)
        val expected = GameRecordWithSyncState(record = expectedRecord, syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }
}