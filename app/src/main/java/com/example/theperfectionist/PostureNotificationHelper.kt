package com.example.theperfectionist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat

object PostureNotificationHelper {
    private const val CHANNEL_ID = "posture_alert_heads_up_v3"
    private const val CHANNEL_NAME = "Posture Alerts"

    private const val BAD_POSTURE_NOTIFICATION_ID = 1001
    private const val BLUETOOTH_DISCONNECT_NOTIFICATION_ID = 1002

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Posture and Bluetooth alerts"
                enableVibration(false)
                setSound(null, null)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    fun showBadPostureNotification(context: Context) {
        if (NotificationSettingsState.sleepMode) return

        showNotification(
            context = context,
            id = BAD_POSTURE_NOTIFICATION_ID,
            title = "Bad posture detected",
            message = "Fix your posture."
        )

        playAlertSound(context)
        vibratePhone(context)
    }

    fun showBluetoothDisconnectedNotification(context: Context) {
        if (NotificationSettingsState.sleepMode) return

        showNotification(
            context = context,
            id = BLUETOOTH_DISCONNECT_NOTIFICATION_ID,
            title = "Bluetooth disconnected",
            message = "Your Perfectionist device has disconnected."
        )

        playAlertSound(context)
        vibratePhone(context)
    }

    private fun showNotification(
        context: Context,
        id: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false)
            .setDefaults(0)
            .build()

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(id, notification)
    }

    private fun playAlertSound(context: Context) {
        val soundLevel = NotificationSettingsState.soundLevel.coerceIn(0f, 1f)
        if (soundLevel <= 0f) return

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mediaPlayer = MediaPlayer().apply {
            setDataSource(context, soundUri)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            setVolume(soundLevel, soundLevel)

            setOnCompletionListener {
                it.release()
            }

            setOnErrorListener { mp, _, _ ->
                mp.release()
                true
            }

            prepare()
            start()
        }
    }

    private fun vibratePhone(context: Context) {
        val vibrationLevel = NotificationSettingsState.vibrationLevel.coerceIn(0f, 1f)
        if (vibrationLevel <= 0f) return

        val amplitude = (vibrationLevel * 255)
            .toInt()
            .coerceIn(1, 255)

        val vibrator: Vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(
                longArrayOf(0, 300, 150, 300),
                intArrayOf(0, amplitude, 0, amplitude),
                -1
            )
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 300, 150, 300), -1)
        }
    }
}