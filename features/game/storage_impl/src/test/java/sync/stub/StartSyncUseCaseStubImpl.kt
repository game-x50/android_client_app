package sync.stub

import com.ruslan.hlushan.game.core.api.sync.StartSyncUseCase
import io.reactivex.Observable

internal class StartSyncUseCaseStubImpl : StartSyncUseCase {

    var startedCounter: Int = 0
        private set

    var canceledCounter: Int = 0
        private set

    override fun observeIsSynchronizing(): Observable<Boolean> =
            Observable.empty()

    override fun start() {
        startedCounter++
    }

    override fun cancel() {
        canceledCounter++
    }
}