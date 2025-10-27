package de.kallis.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import de.kallis.model.SalesDatabase
import de.kallis.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class SalesSyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val db = SalesDatabase.getDatabase(applicationContext)
            val unsynced = db.salesDao().getUnsyncedSales()

            if (unsynced.isEmpty()) return@withContext Result.success()

            val api = RetrofitClient.api
            for (sale in unsynced) {
                try {
                    val response = api.addSale(sale)
                    if (response.isSuccessful) {
                        db.salesDao().update(sale.copy(synced = true))
                        Log.i("SYNC", "Sale synced successfully: ${sale.id}")
                    } else {
                        Log.w("SYNC", "Failed to sync sale ${sale.id}: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("SYNC", "Sync error: ${e.message}")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("SYNC", "Worker failed: ${e.message}")
            Result.retry()
        }
    }
}

