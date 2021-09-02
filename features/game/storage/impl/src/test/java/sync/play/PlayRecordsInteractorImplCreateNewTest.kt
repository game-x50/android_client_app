package sync.play

import assertRecordsWithSyncStateInLocalRepo
import com.ruslan.hlushan.game.core.api.play.dto.GameRecord
import com.ruslan.hlushan.game.core.api.play.dto.GameRecordWithSyncState
import com.ruslan.hlushan.game.core.api.play.dto.RecordSyncState
import com.ruslan.hlushan.game.core.api.test.utils.generateFakeGameState
import com.ruslan.hlushan.test.utils.generateFakeDuration
import com.ruslan.hlushan.test.utils.generateFakeInstantTimestamp
import org.junit.Test
import sync.play.base.BasePlayRecordsInteractorImplTest

/**
 * @author Ruslan Hlushan on 2019-06-10
 */
internal class PlayRecordsInteractorImplCreateNewTest : BasePlayRecordsInteractorImplTest() {

    @Test
    fun testCreateNew() {
        val gameState = generateFakeGameState()
        val totalPlayed = generateFakeDuration()
        val localCreatedTimestamp = generateFakeInstantTimestamp()

        playRecordsInteractor.addNewRecordAfterPlaying(
                gameState = gameState,
                totalPlayed = totalPlayed,
                localCreatedTimestamp = localCreatedTimestamp
        )
                .test()
                .assertNoErrors()
                .assertComplete()

        val localActionId = localRepo.getAll()
                .last()
                .syncState
                .localAction!!
                .actionId

        val expectedSyncState = RecordSyncState.forLocalCreated(
                localActionId = localActionId,
                modifyingNow = false,
                localCreatedTimestamp = localCreatedTimestamp
        )
        val expectedRecord = GameRecord(2, gameState, totalPlayed)
        val expected = GameRecordWithSyncState(record = expectedRecord, syncState = expectedSyncState)
        localRepo.assertRecordsWithSyncStateInLocalRepo(listOf(expected))
    }

    @Test
    fun testCreateManyNew() {
        val count = 100
        val startRecordId = 2L

        val expected = (startRecordId..(startRecordId + count)).map { newRecordId ->

            val gameState = generateFakeGameState()
            val totalPlayed = generateFakeDuration()
            val localCreatedTimestamp = generateFakeInstantTimestamp()

            playRecordsInteractor.addNewRecordAfterPlaying(
                    gameState = gameState,
                    totalPlayed = totalPlayed,
                    localCreatedTimestamp = localCreatedTimestamp
            )
                    .test()
                    .assertNoErrors()
                    .assertComplete()

            val localActionId = localRepo.getAll()
                    .last()
                    .syncState
                    .localAction!!
                    .actionId

            val expectedSyncState = RecordSyncState.forLocalCreated(
                    localActionId = localActionId,
                    modifyingNow = false,
                    localCreatedTimestamp = localCreatedTimestamp
            )
            val expectedRecord = GameRecord(newRecordId, gameState, totalPlayed)

            return@map GameRecordWithSyncState(record = expectedRecord, syncState = expectedSyncState)
        }

        localRepo.assertRecordsWithSyncStateInLocalRepo(expected)
    }
}