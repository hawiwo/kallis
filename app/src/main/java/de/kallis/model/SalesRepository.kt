package de.kallis.model

import android.util.Log
import de.kallis.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SalesRepository(private val dao: SalesDao) {

    val allSales = dao.getAllSales()

    suspend fun addSale(sale: SaleRecordEntity) {
        dao.insert(sale)
        trySyncSale(sale)
    }

    suspend fun trySyncSale(sale: SaleRecordEntity) = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.api.addSale(sale).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    dao.markAsSynced(sale.id)
                    Log.i("SYNC", "Sale synced successfully: ${sale.id}")
                }
            } else {
                Log.w("SYNC", "Failed to sync sale: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Log.w("SYNC", "No network / sync failed: ${e.message}")
        }
    }

    suspend fun syncPendingSales() = withContext(Dispatchers.IO) {
        val unsent = dao.getUnsentSales()
        for (sale in unsent) {
            trySyncSale(sale)
        }
    }
}
