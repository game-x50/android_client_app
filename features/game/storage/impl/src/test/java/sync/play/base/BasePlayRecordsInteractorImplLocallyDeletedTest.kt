package sync.play.base

import com.ruslan.hlushan.game.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.game.api.test.utils.generateFakeRecordSyncStateLastLocalModifiedTimestamp
import com.ruslan.hlushan.test.utils.generateFakeDuration
import utils.assertRecordsWithSyncStateInLocalRepo
import utils.generateAndAddLocalDeletedToLocalRepo

internal abstract class BasePlayRecordsInteractorImplLocallyDeletedTest : BasePlayRecordsInteractorImplTest() {

    protected fun baseTestLocallyDeletedUpdateAndGetRecordForPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = syncingNow)

        playRecordsInteractor.updateAndGetRecordForPlaying(original.record.id)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyDeletedMarkAsNonPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = syncingNow)

        playRecordsInteractor.markAsNonPlaying(original.record.id)
                .test()
                .assertComplete()
                .assertNoErrors()

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyDeletedMarkAsNonPlayingAsynchronously(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = syncingNow)

        playRecordsInteractor.markAsNonPlayingAsynchronously(original.record.id)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyDeletedRemove(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = syncingNow)

        playRecordsInteractor.removeRecordById(original.record.id)
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }

    protected fun baseTestLocallyDeletedUpdateRecordAfterPlaying(syncingNow: Boolean) {
        val original = localRepo.generateAndAddLocalDeletedToLocalRepo(syncingNow = syncingNow)

        playRecordsInteractor.updateRecordAfterPlaying(
                id = original.record.id,
                gameState = generateFakeGameState(),
                totalPlayed = generateFakeDuration(),
                localModifiedTimestamp = generateFakeRecordSyncStateLastLocalModifiedTimestamp()
        )
                .test()
                .assertNotComplete()
                .assertError(IllegalStateException::class.java)

        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(original))
    }
}