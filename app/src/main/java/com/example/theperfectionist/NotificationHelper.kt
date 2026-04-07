package com.example.theperfectionist

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "posture_alerts"
        const val CHANNEL_NAME = "Posture Alerts"
        const val BAD_POSTURE_NOTIFICATION_ID = 1001
        const val DISCONNECT_NOTIFICATION_ID = 1002
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for posture alerts and Bluetooth disconnects"
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showBadPostureNotification(
        soundLevel: Float,
        vibrationLevel: Float
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Bad posture")
            .setContentText("Bad posture")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        if (vibrationLevel > 0f) {
            val vibrationPattern = when {
                vibrationLevel >= 0.75f -> longArrayOf(0, 500, 200, 500)
                vibrationLevel >= 0.35f -> longArrayOf(0, 300, 150, 300)
                else -> longArrayOf(0, 150, 100, 150)
            }
            builder.setVibrate(vibrationPattern)
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        if (soundLevel <= 0f) {
            builder.setSilent(true)
        } else {
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND)
        }

        notifySafely(BAD_POSTURE_NOTIFICATION_ID, builder.build())
    }

    fun showBluetoothDisconnectedNotification(
        soundLevel: Float,
        vibrationLevel: Float
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Bluetooth disconnected")
            .setContentText("Your posture device disconnected")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        if (vibrationLevel > 0f) {
            val vibrationPattern = when {
                vibrationLevel >= 0.75f -> longArrayOf(0, 400, 200, 400)
                vibrationLevel >= 0.35f -> longArrayOf(0, 250, 120, 250)
                else -> longArrayOf(0, 120, 80, 120)
            }
            builder.setVibrate(vibrationPattern)
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        if (soundLevel <= 0f) {
            builder.setSilent(true)
        } else {
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND)
        }

        notifySafely(DISCONNECT_NOTIFICATION_ID, builder.build())
    }

    private fun notifySafely(notificationId: Int, notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) return
        }

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}