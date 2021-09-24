package sync.play

import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyDeletedTest

internal class PlayRecordsInteractorImplLocallyDeletedSyncingTest : BasePlayRecordsInteractorImplLocallyDeletedTest() {

    @Test
    fun testLocallyDeletedSyncingUpdateAndGetRecordForPlaying() =
            baseTestLocallyDeletedUpdateAndGetRecordForPlaying(syncingNow = true)

    @Test
    fun testLocallyDeletedSyncingMarkAsNonPlaying() =
            baseTestLocallyDeletedMarkAsNonPlaying(syncingNow = true)

    @Test
    fun testLocallyDeletedSyncingMarkAsNonPlayingAsynchronously() =
            baseTestLocallyDeletedMarkAsNonPlayingAsynchronously(syncingNow = true)

    @Test
    fun testLocallyDeletedSyncingRemove() =
            baseTestLocallyDeletedRemove(syncingNow = true)

    @Test
    fun testLocallyDeletedSyncingUpdateRecordAfterPlaying() =
            baseTestLocallyDeletedUpdateRecordAfterPlaying(syncingNow = true)
}