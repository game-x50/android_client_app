package sync.play

import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyDeletedTest

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
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