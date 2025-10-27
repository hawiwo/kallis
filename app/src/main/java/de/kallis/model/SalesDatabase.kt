package de.kallis.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SaleRecordEntity::class], version = 1)
abstract class SalesDatabase : RoomDatabase() {
    abstract fun salesDao(): SalesDao

    companion object {
        @Volatile
        private var INSTANCE: SalesDatabase? = null

        fun getDatabase(context: Context): SalesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalesDatabase::class.java,
                    "sales_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
