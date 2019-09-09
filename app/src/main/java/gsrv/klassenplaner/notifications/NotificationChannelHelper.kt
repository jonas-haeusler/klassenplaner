package gsrv.klassenplaner.notifications

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import gsrv.klassenplaner.R

class NotificationChannelHelper(val context: Context) {

    private val manager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    /**
     * Creates the notification channels.
     *
     * Attempting to create an existing notification channel with its original values
     * performs no operation, so it's safe to execute the method when starting the app.
     */
    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannels(
                listOf(
                    createHomeworkChannel(),
                    createExamChannel()
                )
            )
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createHomeworkChannel(): NotificationChannel {
        val channelId = context.getString(R.string.notification_channel_id_homework)
        val channelName = context.getString(R.string.notification_channel_name_homework)
        val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, channelImportance)
        channel.enableVibration(true)

        return channel
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createExamChannel(): NotificationChannel {
        val channelId = context.getString(R.string.notification_channel_id_exam)
        val channelName = context.getString(R.string.notification_channel_name_exam)
        val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, channelImportance)
        channel.enableVibration(true)

        return channel
    }
}
