package sync.stub

import com.ruslan.hlushan.core.api.dto.OperationResult
import com.ruslan.hlushan.core.api.dto.getOrThrow
import com.ruslan.hlushan.game.storage.impl.remote.SyncRemoteRepository
import com.ruslan.hlushan.game.storage.impl.remote.dto.LocalModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.RemoteRecord
import com.ruslan.hlushan.game.storage.impl.remote.dto.UpdateLocalNonModifiedResponse
import com.ruslan.hlushan.game.storage.impl.remote.dto.UploadLocalModifiedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.GetNewRemoteCreatedRequest
import com.ruslan.hlushan.game.storage.impl.remote.dto.server.UpdateLocalSyncedRequest
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit

internal class SyncRemoteRepositoryMockImpl : SyncRemoteRepository {

    private val delay: Long = 100L
    private val timeUnit = TimeUnit.MILLISECONDS

    private val testScheduler = TestScheduler()

    lateinit var returnTimestampResult: OperationResult<Instant, Throwable>

    var receivedUploadLocalModifiedRequests: List<UploadLocalModifiedRequest>? = null
        private set
    lateinit var returnListLocalModifiedResponses: List<LocalModifiedResponse>

    var receivedUpdateLocalSyncedRequest: List<UpdateLocalSyncedRequest>? = null
        private set
    lateinit var returnListUpdateLocalNonModifiedResponses: List<UpdateLocalNonModifiedResponse>

    var receivedGetNewRemoteCreatedRequest: GetNewRemoteCreatedRequest? = null
        private set
    lateinit var returnListRemoteRecords: List<RemoteRecord>

    override fun getRemoteTimestamp(): Single<Instant> =
            createDelay()
                    .map { returnTimestampResult.getOrThrow() }

    override fun uploadLocalModified(
            requests: List<UploadLocalModifiedRequest>
    ): Single<List<LocalModifiedResponse>> =
            Completable.fromAction { receivedUploadLocalModifiedRequests = requests }
                    .andThen(createDelay())
                    .map { returnListLocalModifiedResponses }

    override fun updateLocalSynced(
            requests: List<UpdateLocalSyncedRequest>
    ): Single<List<UpdateLocalNonModifiedResponse>> =
            Completable.fromAction { receivedUpdateLocalSyncedRequest = requests }
                    .andThen(createDelay())
                    .map { returnListUpdateLocalNonModifiedResponses }

    override fun getNewRemoteCreated(
            request: GetNewRemoteCreatedRequest
    ): Single<List<RemoteRecord>> =
            Completable.fromAction { receivedGetNewRemoteCreatedRequest = request }
                    .andThen(createDelay())
                    .map { returnListRemoteRecords }

    fun advanceTimeToEndDelay() = testScheduler.advanceTimeBy(delay, timeUnit)

    fun cleanUp() {
        advanceTimeToEndDelay()
        testScheduler.triggerActions()
    }

    private fun createDelay(): Single<*> = Single.timer(delay, timeUnit, testScheduler)
}