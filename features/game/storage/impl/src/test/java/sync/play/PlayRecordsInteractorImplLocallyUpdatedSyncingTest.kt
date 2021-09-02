package sync.play

import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyUpdatedTest

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
internal class PlayRecordsInteractorImplLocallyUpdatedSyncingTest : BasePlayRecordsInteractorImplLocallyUpdatedTest() {

    @Test
    fun testLocallyUpdatedSyncingUpdateAndGetRecordForPlaying() =
            baseTestLocallyUpdatedUpdateAndGetRecordForPlaying(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingStartedModifyingUpdateAndGetRecordForPlaying() =
            baseTestLocallyUpdatedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingMarkAsNonPlaying() =
            baseTestLocallyUpdatedMarkAsNonPlaying(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingMarkAsNonPlayingAsynchronously() =
            baseTestLocallyUpdatedMarkAsNonPlayingAsynchronously(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingStartedModifyingMarkAsNonPlaying() =
            baseTestLocallyUpdatedStartedModifyingMarkAsNonPlaying(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingStartedModifyingMarkAsNonPlayingAsynchronously() =
            baseTestLocallyUpdatedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingRemove() =
            baseTestLocallyUpdatedRemove(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingStartedModifyingRemove() =
            baseTestLocallyUpdatedStartedModifyingRemove(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingUpdateRecordAfterPlaying() =
            baseTestLocallyUpdatedUpdateRecordAfterPlaying(syncingNow = true)

    @Test
    fun testLocallyUpdatedSyncingStartedModifyingUpdateRecordAfterPlaying() =
            baseTestLocallyUpdatedStartedModifyingUpdateRecordAfterPlaying(syncingNow = true)
}