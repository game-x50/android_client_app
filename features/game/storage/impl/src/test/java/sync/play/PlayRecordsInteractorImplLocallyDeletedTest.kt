package sync.play

import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyDeletedTest

internal class PlayRecordsInteractorImplLocallyDeletedTest : BasePlayRecordsInteractorImplLocallyDeletedTest() {

    @Test
    fun testLocallyDeletedUpdateAndGetRecordForPlaying() =
            baseTestLocallyDeletedUpdateAndGetRecordForPlaying(syncingNow = false)

    @Test
    fun testLocallyDeletedMarkAsNonPlaying() =
            baseTestLocallyDeletedMarkAsNonPlaying(syncingNow = false)

    @Test
    fun testLocallyDeletedMarkAsNonPlayingAsynchronously() =
            baseTestLocallyDeletedMarkAsNonPlayingAsynchronously(syncingNow = false)

    @Test
    fun testLocallyDeletedRemove() =
            baseTestLocallyDeletedRemove(syncingNow = false)

    @Test
    fun testLocallyDeletedUpdateRecordAfterPlaying() =
            baseTestLocallyDeletedUpdateRecordAfterPlaying(syncingNow = false)
}