package sync.play

import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplSyncedTest

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
internal class PlayRecordsInteractorImplSyncedSyncingTest : BasePlayRecordsInteractorImplSyncedTest() {

    @Test
    fun testSyncedSyncingUpdateAndGetRecordForPlaying() =
            baseTestSyncedUpdateAndGetRecordForPlaying(syncingNow = true)

    @Test
    fun testSyncedSyncingStartedModifyingUpdateAndGetRecordForPlaying() =
            baseTestSyncedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow = true)

    @Test
    fun testSyncedSyncingMarkAsNonPlaying() =
            baseTestSyncedMarkAsNonPlaying(syncingNow = true)

    @Test
    fun testSyncedSyncingMarkAsNonPlayingAsynchronously() =
            baseTestSyncedMarkAsNonPlayingAsynchronously(syncingNow = true)

    @Test
    fun testSyncedSyncingStartedModifyingMarkAsNonPlaying() =
            baseTestSyncedStartedModifyingMarkAsNonPlaying(syncingNow = true)

    @Test
    fun testSyncedSyncingStartedModifyingMarkAsNonPlayingAsynchronously() =
            baseTestSyncedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow = true)

    @Test
    fun testSyncedSyncingRemove() =
            baseTestSyncedRemove(syncingNow = true)

    @Test
    fun testSyncedSyncingStartedModifyingRemove() =
            baseTestSyncedStartedModifyingRemove(syncingNow = true)

    @Test
    fun testSyncedSyncingUpdateRecordAfterPlaying() =
            baseTestSyncedUpdateRecordAfterPlaying(syncingNow = true)

    @Test
    fun testSyncedSyncingStartedModifyingUpdateRecordAfterPlaying() =
            baseTestSyncedStartedModifyingUpdateRecordAfterPlaying(syncingNow = true)
}