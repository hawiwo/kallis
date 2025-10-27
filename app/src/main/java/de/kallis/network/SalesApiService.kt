package de.kallis.network

import de.kallis.model.SaleRecordEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesApiService {
    @POST("sales/")
    suspend fun addSale(@Body sale: SaleRecordEntity): Response<Unit>
}