package sync.play

import com.ruslan.hlushan.game.api.play.dto.toLocalDeletedOrThrow
import org.junit.Assert.assertNotEquals
import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyCreatedTest
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalCreatedToLocalRepo

internal class PlayRecordsInteractorImplLocallyCreatedSyncingTest : BasePlayRecordsInteractorImplLocallyCreatedTest() {

    @Test
    fun testLocallyCreatedSyncingUpdateAndGetRecordForPlaying() =
            baseTestLocallyCreatedUpdateAndGetRecordForPlaying(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingStartedModifyingUpdateAndGetRecordForPlaying() =
            baseTestLocallyCreatedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingMarkAsNonPlaying() =
            baseTestLocallyCreatedMarkAsNonPlaying(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingMarkAsNonPlayingAsynchronously() =
            baseTestLocallyCreatedMarkAsNonPlayingAsynchronously(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingStartedModifyingMarkAsNonPlaying() =
            baseTestLocallyCreatedStartedModifyingMarkAsNonPlaying(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingStartedModifyingMarkAsNonPlayingAsynchronously() =
            baseTestLocallyCreatedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingRemove() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = true, modifyingNow = false)

        playRecordsInteractor.removeRecordById(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        val updatedLocalActionId = localRepo.getAll()
                .first()
                .syncState
                .localAction!!
                .actionId

        assertNotEquals(updatedLocalActionId, original.syncState.localAction?.actionId)

        val expectedSyncState = original.syncState.toLocalDeletedOrThrow(newLocalActionId = updatedLocalActionId)
        val expected = original.copy(syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    @Test
    fun testLocallyCreatedSyncingStartedModifyingRemove() =
            baseTestLocallyCreatedStartedModifyingRemove(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingUpdateRecordAfterPlaying() =
            baseTestLocallyCreatedUpdateRecordAfterPlaying(syncingNow = true)

    @Test
    fun testLocallyCreatedSyncingStartedModifyingUpdateRecordAfterPlaying() =
            baseTestLocallyCreatedStartedModifyingUpdateRecordAfterPlaying(syncingNow = true)
}