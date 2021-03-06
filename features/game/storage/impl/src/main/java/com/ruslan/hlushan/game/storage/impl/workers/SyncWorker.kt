package com.ruslan.hlushan.game.storage.impl.workers

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.RxWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ruslan.hlushan.android.extensions.AndroidNotification
import com.ruslan.hlushan.android.extensions.AndroidNotificationAction
import com.ruslan.hlushan.android.extensions.AndroidNotificationChannel
import com.ruslan.hlushan.core.foreground.observer.api.AppForegroundObserver
import com.ruslan.hlushan.game.storage.impl.R
import com.ruslan.hlushan.game.storage.impl.SyncInteractor
import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

internal class SyncWorker(
        context: Context,
        params: WorkerParameters,
        private val syncInteractor: SyncInteractor,
        private val appForegroundObserver: AppForegroundObserver
) : RxWorker(context, params) {

    override fun createWork(): Single<Result> =
            Completable.fromAction {
                if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) || (appForegroundObserver.isInForeground)) {
                    setForegroundAsync(createForegroundInfo(applicationContext, id))
                }
            }
                    .andThen(syncInteractor.sync())
                    .toSingleDefault(Result.success())
                    .onErrorReturn { error ->
                        if (runAttemptCount < MAX_NUMBER_OF_RETRY_FOR_INIT_WORKER) {
                            Result.retry()
                        } else {
                            Result.failure()
                        }
                    }

    class Factory
    @Inject
    constructor(
            private val syncInteractorProvider: Provider<SyncInteractor>,
            private val appForegroundObserverProvider: Provider<AppForegroundObserver>
    ) : WorkerFactory() {

        override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
        ): ListenableWorker? =
                if (workerClassName == SyncWorker::class.java.name) {
                    SyncWorker(
                            context = appContext,
                            params = workerParameters,
                            syncInteractor = syncInteractorProvider.get(),
                            appForegroundObserver = appForegroundObserverProvider.get()
                    )
                } else {
                    null
                }
    }

    companion object {

        private const val MAX_NUMBER_OF_RETRY_FOR_INIT_WORKER = 3

        private const val SYNC_SERVICE_NOTIFICATION_ID = 8812

        private const val PERIODIC_SYNC_WORK_NAME = "PERIODIC_SYNC_WORK_NAME"

        fun start(appContext: Context) {

            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            @Suppress("MagicNumber")
            val request = PeriodicWorkRequest.Builder(
                    SyncWorker::class.java,
                    16, TimeUnit.HOURS
            )
                    .setConstraints(constraints)
                    /**
                     * [PeriodicWorkRequest] cannot be expedited
                     * check [PeriodicWorkRequest.Builder.buildInternal]
                     */
                    .build()

            WorkManager.getInstance(appContext)
                    .enqueueUniquePeriodicWork(
                            PERIODIC_SYNC_WORK_NAME,
                            ExistingPeriodicWorkPolicy.REPLACE,
                            request
                    )
        }

        fun cancel(appContext: Context) {
            WorkManager.getInstance(appContext)
                    .cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
        }

        private fun createForegroundInfo(appContext: Context, workRequestId: UUID): ForegroundInfo {

            val cancelActionPendingIntent = WorkManager.getInstance(appContext)
                    .createCancelPendingIntent(workRequestId)

            val cancelAction = AndroidNotificationAction(
                    actionImageResId = android.R.drawable.ic_delete,
                    actionText = appContext.getString(R.string.game_storage_impl_cancel_sync),
                    actionPendingIntent = cancelActionPendingIntent,
                    authenticationRequired = true
            )

            val notification = AndroidNotification(
                    id = SYNC_SERVICE_NOTIFICATION_ID,
                    contentTitle = appContext.getString(R.string.game_storage_impl_sync_notification_title),
                    contentText = appContext.getString(R.string.game_storage_impl_sync_notification_message),
                    channel = SyncNotificationChannel(),
                    actions = arrayOf(cancelAction),
                    cancelable = false,
                    autoCancel = false
            )

            return ForegroundInfo(SYNC_SERVICE_NOTIFICATION_ID, notification.build(appContext))
        }
    }
}

private class SyncNotificationChannel : AndroidNotificationChannel {

    override val channelId: String get() = "com.ruslan.hlushan.game.notification.channel.SYNC"

    @get:StringRes
    override val channelNameResId
        get() = R.string.game_storage_impl_sync_notification_channel_name

    @get:StringRes
    override val channelDescriptionResId
        get() = R.string.game_storage_impl_sync_notification_channel_description

    override val enableSoundAndVibrate get() = false
}