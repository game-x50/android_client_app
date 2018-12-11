package sync.play

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.toLocalDeletedOrThrow
import generateAndAddLocalCreatedToLocalRepo
import org.junit.Assert.assertNotEquals
import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyCreatedTest

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
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