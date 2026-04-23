package com.example.foodpics

import android.app.Application
import com.example.foodpics.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class FoodPicsApplication : Application() {

    lateinit var appGraph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        appGraph = createGraphFactory<AppGraph.Factory>().create(applicationContext)
    }
}
