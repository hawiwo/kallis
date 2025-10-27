package de.kallis.network

import retrofit2.http.*
import retrofit2.Call
import de.kallis.model.SaleRecordEntity

interface SalesApiService {
    @GET("sales")
    fun getSales(): Call<List<SaleRecordEntity>>

    @POST("sales")
    fun addSale(@Body sale: SaleRecordEntity): Call<SaleRecordEntity>
}

