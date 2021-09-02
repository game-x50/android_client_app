package sync.play

import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplLocallyUpdatedTest

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
internal class PlayRecordsInteractorImplLocallyUpdatedTest : BasePlayRecordsInteractorImplLocallyUpdatedTest() {

    @Test
    fun testLocallyUpdatedUpdateAndGetRecordForPlaying() =
            baseTestLocallyUpdatedUpdateAndGetRecordForPlaying(syncingNow = false)

    @Test
    fun testLocallyUpdatedStartedModifyingUpdateAndGetRecordForPlaying() =
            baseTestLocallyUpdatedStartedModifyingUpdateAndGetRecordForPlaying(syncingNow = false)

    @Test
    fun testLocallyUpdatedMarkAsNonPlaying() =
            baseTestLocallyUpdatedMarkAsNonPlaying(syncingNow = false)

    @Test
    fun testLocallyUpdatedMarkAsNonPlayingAsynchronously() =
            baseTestLocallyUpdatedMarkAsNonPlayingAsynchronously(syncingNow = false)

    @Test
    fun testLocallyUpdatedStartedModifyingMarkAsNonPlaying() =
            baseTestLocallyUpdatedStartedModifyingMarkAsNonPlaying(syncingNow = false)

    @Test
    fun testLocallyUpdatedStartedModifyingMarkAsNonPlayingAsynchronously() =
            baseTestLocallyUpdatedStartedModifyingMarkAsNonPlayingAsynchronously(syncingNow = false)

    @Test
    fun testLocallyUpdatedRemove() =
            baseTestLocallyUpdatedRemove(syncingNow = false)

    @Test
    fun testLocallyUpdatedStartedModifyingRemove() =
            baseTestLocallyUpdatedStartedModifyingRemove(syncingNow = false)

    @Test
    fun testLocallyUpdatedUpdateRecordAfterPlaying() =
            baseTestLocallyUpdatedUpdateRecordAfterPlaying(syncingNow = false)

    @Test
    fun testLocallyUpdatedStartedModifyingUpdateRecordAfterPlaying() =
            baseTestLocallyUpdatedStartedModifyingUpdateRecordAfterPlaying(syncingNow = false)
}