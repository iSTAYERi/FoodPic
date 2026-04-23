package com.example.foodpics.di

import android.content.Context
import com.example.foodpics.core.network.FoodishApi
import com.example.foodpics.core.network.FoodishRepository
import com.example.foodpics.core.network.NetworkFactory
import com.example.foodpics.feature.viewer.SaveImageUseCase
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@DependencyGraph
interface AppGraph {

    val foodishRepository: FoodishRepository
    val saveImageUseCase: SaveImageUseCase
    val context: Context

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides context: Context): AppGraph
    }

    @Provides
    fun provideOkHttp(): OkHttpClient = NetworkFactory.createOkHttp()

    @Provides
    fun provideJson(): Json = NetworkFactory.createJson()

    @Provides
    fun provideRetrofit(okHttp: OkHttpClient, json: Json): Retrofit =
        NetworkFactory.createRetrofit(okHttp, json)

    @Provides
    fun provideFoodishApi(retrofit: Retrofit): FoodishApi =
        NetworkFactory.createFoodishApi(retrofit)

    @Provides
    fun provideFoodishRepository(api: FoodishApi): FoodishRepository = FoodishRepository(api)

    @Provides
    fun provideSaveImageUseCase(context: Context): SaveImageUseCase = SaveImageUseCase(context)
}
