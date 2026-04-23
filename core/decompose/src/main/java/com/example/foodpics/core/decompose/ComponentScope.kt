package com.example.foodpics.core.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

fun ComponentContext.componentScope(
    context: CoroutineContext = Dispatchers.Main.immediate + SupervisorJob(),
): CoroutineScope {
    val scope = CoroutineScope(context)
    lifecycle.doOnDestroy { scope.cancel() }
    return scope
}
