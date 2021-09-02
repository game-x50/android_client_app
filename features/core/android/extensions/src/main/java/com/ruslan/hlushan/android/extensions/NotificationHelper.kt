package com.ruslan.hlushan.android.extensions

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

interface AndroidNotificationChannel {
    val channelId: String
    @get:StringRes val channelNameResId: Int
    @get:StringRes val channelDescriptionResId: Int
    val enableSoundAndVibrate: Boolean
}

class AndroidNotificationAction(
        @DrawableRes val actionImageResId: Int,
        val actionText: String,
        val actionPendingIntent: PendingIntent,
        val authenticationRequired: Boolean
)

private val Context.defaultContentTitle: String
    get() {
        @StringRes val labelResId = this.applicationInfo.labelRes

        return if (labelResId != 0) {
            this.getString(labelResId)
        } else {
            ""
        }
    }

fun createDefaultEmptyPendingIntent(context: Context, id: Int): PendingIntent {
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        (PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    return PendingIntent.getActivity(context, id, Intent(), flags)
}

@SuppressWarnings("LongParameterList")
class AndroidNotification(
        val id: Int,
        val pendingIntent: PendingIntent? = null,
        val contentTitle: String? = null,
        val contentText: String,
        @DrawableRes val smallIconResId: Int? = null,
        val channel: AndroidNotificationChannel,
        val autoCancel: Boolean = true,
        val cancelable: Boolean = true,
        val actions: Array<AndroidNotificationAction> = emptyArray()
) {

    fun build(context: Context): Notification {
        createNotificationChannel(context)

        val title = (contentTitle ?: context.defaultContentTitle)

        val builder = NotificationCompat.Builder(context, channel.channelId)
                .setSmallIcon(smallIconResId ?: context.appIconResourceId)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(pendingIntent ?: createDefaultEmptyPendingIntent(context, id))
                .setAutoCancel(autoCancel)
                .setStyle(
                        NotificationCompat.BigTextStyle()
                                .setBigContentTitle(title)
                                .bigText(contentText)
                )

        if (channel.enableSoundAndVibrate) {
            builder.setDefaults(
                    Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS
            )
        }

        for (action in actions) {
            val androidAction = NotificationCompat.Action.Builder(
                    action.actionImageResId,
                    action.actionText,
                    action.actionPendingIntent
            )
                    // todo: uncomment on compileSdk >=31
                    //.setAuthenticationRequired(action.authenticationRequired)
                    .build()
            builder.addAction(androidAction)
        }

        val notification = builder.build()

        if (!cancelable) {
            notification.flags = notification.flags or (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT)
        }

        return notification
    }

    fun show(context: Context) {
        NotificationManagerCompat.from(context).notify(id, build(context))
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager = NotificationManagerCompat.from(context)

            if (notificationManager.getNotificationChannel(channel.channelId) == null) {

                val importance = if (channel.enableSoundAndVibrate) {
                    NotificationManager.IMPORTANCE_HIGH
                } else {
                    NotificationManager.IMPORTANCE_DEFAULT
                }

                val notificationChannel = NotificationChannel(
                        channel.channelId,
                        context.getString(channel.channelNameResId),
                        importance
                )
                notificationChannel.description = context.getString(channel.channelDescriptionResId)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.BLUE

                if (channel.enableSoundAndVibrate) {
                    @SuppressWarnings("MagicNumber")
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                } else {
                    notificationChannel.setSound(null, null)
                }
                notificationChannel.enableVibration(channel.enableSoundAndVibrate)

                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }
}