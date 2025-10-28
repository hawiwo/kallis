package de.kallis

import android.app.Application
import androidx.work.*
import java.util.concurrent.TimeUnit
import de.kallis.work.SalesSyncWorker

class KallisApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SalesSyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sales_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}

