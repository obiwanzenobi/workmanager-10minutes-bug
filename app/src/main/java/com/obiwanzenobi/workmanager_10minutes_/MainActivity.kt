package com.obiwanzenobi.workmanager_10minutes_

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workManager = WorkManager.getInstance(this)
//        val workRequest = OneTimeWorkRequest.from(ForegroundCoroutineNotificationUpdatingWorker::class.java)
        val workRequest = OneTimeWorkRequest.from(ForegroundCoroutineSetForegroundWorker::class.java)
        workManager.enqueue(workRequest)

        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this, Observer { workInfo: WorkInfo ->

                Log.d(Constants.LOGGER_TAG, "Worker state change: ${workInfo.state}")

                state.text = workInfo.state.toString()

                val progress = workInfo.progress
                val value = progress.getInt(Constants.PROGRESS, 0)
                progressBar.progress = value ?: 0

            })
    }
}
