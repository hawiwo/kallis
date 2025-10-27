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
    suspend fun trySyncSale(sale: SaleRecordEntity) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.addSale(sale)
                if (response.isSuccessful) {
                    dao.markAsSynced(sale.id)
                    Log.i("SYNC", "Sale synced successfully: ${sale.id}")
                } else {
                    Log.w("SYNC", "Sale sync failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SYNC", "Error syncing sale", e)
            }
        }
    }
    suspend fun syncPendingSales() = withContext(Dispatchers.IO) {
        val unsent = dao.getUnsyncedSales()
        for (sale in unsent) {
            trySyncSale(sale)
        }
    }
}
