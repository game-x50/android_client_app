package sync.play

import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplSyncedTest

internal class PlayRecordsInteractorImplSyncedTest : BasePlayRecordsInteractorImplSyncedTest() {

    @Test
    fun testSyncedUpdateAndGetRecordForPlaying() =
            baseTestSyncedUpdateAndGetRecordForPlaying(syncingNow = false)

    @Test
    fun testSyncedStartedModifyingUpdateAndGetRecordForPlaying() =
            baseTestSyncedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow = false)

    @Test
    fun testSyncedMarkAsNonPlaying() =
            baseTestSyncedMarkAsNonPlaying(syncingNow = false)

    @Test
    fun testSyncedMarkAsNonPlayingAsynchronously() =
            baseTestSyncedMarkAsNonPlayingAsynchronously(syncingNow = false)

    @Test
    fun testSyncedStartedModifyingMarkAsNonPlaying() =
            baseTestSyncedStartedModifyingMarkAsNonPlaying(syncingNow = false)

    @Test
    fun testSyncedStartedModifyingMarkAsNonPlayingAsynchronously() =
            baseTestSyncedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow = false)

    @Test
    fun testSyncedRemove() =
            baseTestSyncedRemove(syncingNow = false)

    @Test
    fun testSyncedStartedModifyingRemove() =
            baseTestSyncedStartedModifyingRemove(syncingNow = false)

    @Test
    fun testSyncedUpdateRecordAfterPlaying() =
            baseTestSyncedUpdateRecordAfterPlaying(syncingNow = false)

    @Test
    fun testSyncedStartedModifyingUpdateRecordAfterPlaying() =
            baseTestSyncedStartedModifyingUpdateRecordAfterPlaying(syncingNow = false)
}