package com.example.foodpics.core.network

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class FoodishRepository(private val api: FoodishApi) {

    suspend fun loadRandom(count: Int = 20): Result<List<String>> = runCatching {
        coroutineScope {
            val results = List(count) {
                async { runCatching { api.random().image } }
            }.awaitAll()
            val urls = results.mapNotNull { it.getOrNull() }
            if (urls.isEmpty()) {
                val firstFailure = results.firstOrNull { it.isFailure }?.exceptionOrNull()
                throw firstFailure ?: IllegalStateException("Foodish returned empty result")
            }
            urls
        }
    }
}
