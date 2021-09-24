package sync.play

import assertRecordsWithSyncStateInLocalRepo
import generateAndAddLocalCreatedToLocalRepo
import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyCreatedTest

internal class PlayRecordsInteractorImplLocallyCreatedTest : BasePlayRecordsInteractorImplLocallyCreatedTest() {

    @Test
    fun testLocallyCreatedUpdateAndGetRecordForPlaying() =
            baseTestLocallyCreatedUpdateAndGetRecordForPlaying(syncingNow = false)

    @Test
    fun testLocallyCreatedStartedModifyingUpdateAndGetRecordForPlaying() =
            baseTestLocallyCreatedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow = false)

    @Test
    fun testLocallyCreatedMarkAsNonPlaying() =
            baseTestLocallyCreatedMarkAsNonPlaying(syncingNow = false)

    @Test
    fun testLocallyCreatedMarkAsNonPlayingAsynchronously() =
            baseTestLocallyCreatedMarkAsNonPlayingAsynchronously(syncingNow = false)

    @Test
    fun testLocallyCreatedStartedModifyingMarkAsNonPlaying() =
            baseTestLocallyCreatedStartedModifyingMarkAsNonPlaying(syncingNow = false)

    @Test
    fun testLocallyCreatedStartedModifyingMarkAsNonPlayingAsynchronously() =
            baseTestLocallyCreatedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow = false)

    @Test
    fun testLocallyCreatedRemove() {
        val original = localRepo.generateAndAddLocalCreatedToLocalRepo(syncingNow = false, modifyingNow = false)

        playRecordsInteractor.removeRecordById(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        localRepo.assertRecordsWithSyncStateInLocalRepo(emptyList())
    }

    @Test
    fun testLocallyCreatedStartedModifyingRemove() =
            baseTestLocallyCreatedStartedModifyingRemove(syncingNow = false)

    @Test
    fun testLocallyCreatedUpdateRecordAfterPlaying() =
            baseTestLocallyCreatedUpdateRecordAfterPlaying(syncingNow = false)

    @Test
    fun testLocallyCreatedStartedModifyingUpdateRecordAfterPlaying() =
            baseTestLocallyCreatedStartedModifyingUpdateRecordAfterPlaying(syncingNow = false)
}