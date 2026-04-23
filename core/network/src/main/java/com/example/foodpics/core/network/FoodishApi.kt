package com.example.foodpics.core.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET

interface FoodishApi {
    @GET("api/")
    suspend fun random(): FoodishResponse
}

@Serializable
data class FoodishResponse(val image: String)
