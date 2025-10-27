package de.kallis.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "sales")
data class SaleRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customer: String,
    val total: Double,
    val time: String,
    val synced: Boolean = false
)
