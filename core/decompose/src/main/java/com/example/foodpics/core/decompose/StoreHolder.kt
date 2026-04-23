package com.example.foodpics.core.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import money.vivid.elmslie.core.store.Store

/**
 * Lifecycle-safe wrapper around an Elmslie [Store].
 *
 * Registers itself in the [InstanceKeeper] of a [ComponentContext] so that the same [Store]
 * survives component recreation (configuration changes) and gets [Store.stop] called when the
 * owning component is permanently destroyed.
 */
class StoreHolder<Event : Any, Effect : Any, State : Any>(
    storeFactory: () -> Store<Event, Effect, State>,
) : InstanceKeeper.Instance {
    val store: Store<Event, Effect, State> = storeFactory().start()
    override fun onDestroy() {
        store.stop()
    }
}

inline fun <reified Event : Any, reified Effect : Any, reified State : Any> ComponentContext.storeHolder(
    key: String,
    noinline storeFactory: () -> Store<Event, Effect, State>,
): StoreHolder<Event, Effect, State> =
    instanceKeeper.getOrCreate(key) { StoreHolder(storeFactory) }
