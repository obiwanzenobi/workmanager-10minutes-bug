package com.obiwanzenobi.workmanager_10minutes_

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.obiwanzenobi.workmanager_10minutes_.Constants.NOTIFICATION_ID
import kotlinx.coroutines.delay


class ForegroundCoroutineSetForegroundWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val notificationManager = appContext.getSystemService(NotificationManager::class.java)

    override suspend fun doWork(): Result {
        Log.d(Constants.LOGGER_TAG, "Worker: START")

        createNotificationChannel()
        val notification = getNotificationWithProgress(0)

        val foregroundInfo = ForegroundInfo(Constants.NOTIFICATION_ID, notification)
        setForeground(foregroundInfo)

        for (percent in 0..100) {
            createNotificationChannel()

            setProgress(workDataOf(Constants.PROGRESS to percent))
            val progressInfo =
                ForegroundInfo(Constants.NOTIFICATION_ID, getNotificationWithProgress(percent))
            setForeground(progressInfo)

            delay(Constants.PER_ITERATION_DELAY)
        }

        Log.d(Constants.LOGGER_TAG, "Worker: END")
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel =
                notificationManager?.getNotificationChannel(Constants.CHANNEL_ID)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager?.createNotificationChannel(notificationChannel)
            }
        }
    }

    private fun getNotificationWithProgress(progress: Int): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Long job")
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotificationProgress(progress: Int) {
        val notification = getNotificationWithProgress(progress)
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }
}
