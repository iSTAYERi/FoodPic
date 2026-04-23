package com.example.foodpics.core.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

object NetworkFactory {

    private const val BASE_URL = "https://foodish-api.com/"

    fun createOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun createJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun createRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    fun createFoodishApi(retrofit: Retrofit): FoodishApi = retrofit.create()
}
