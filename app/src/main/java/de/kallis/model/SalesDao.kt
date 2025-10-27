package de.kallis.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesDao {
    @Query("SELECT * FROM sales ORDER BY id DESC")
    fun getAllSales(): Flow<List<SaleRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: SaleRecordEntity)

    @Query("SELECT * FROM sales WHERE synced = 0")
    suspend fun getUnsentSales(): List<SaleRecordEntity>

    @Query("UPDATE sales SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Query("SELECT * FROM sales WHERE synced = 0")
    suspend fun getUnsyncedSales(): List<SaleRecordEntity>

}
