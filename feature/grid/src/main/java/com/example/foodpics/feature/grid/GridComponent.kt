package com.example.foodpics.feature.grid

import com.arkivanov.decompose.ComponentContext
import com.example.foodpics.core.decompose.componentScope
import com.example.foodpics.core.decompose.storeHolder
import com.example.foodpics.core.network.FoodishRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface GridComponent {
    val states: StateFlow<GridState>
    fun onRefresh()
    fun onRetry()
    fun onItemClicked(url: String)
    fun onBackClicked()
}

class DefaultGridComponent(
    componentContext: ComponentContext,
    repository: FoodishRepository,
    private val onImageSelected: (String) -> Unit,
    private val onBack: () -> Unit,
) : GridComponent, ComponentContext by componentContext {

    private val holder = storeHolder<GridEvent, GridEffect, GridState>("GridStore") {
        gridStore(repository)
    }

    override val states: StateFlow<GridState> = holder.store.states

    private val scope = componentScope()

    init {
        holder.store.accept(GridEvent.Ui.Init)
        scope.launch {
            holder.store.effects.collect { effect ->
                when (effect) {
                    is GridEffect.NavigateToViewer -> onImageSelected(effect.url)
                }
            }
        }
    }

    override fun onRefresh() = holder.store.accept(GridEvent.Ui.Refresh)
    override fun onRetry() = holder.store.accept(GridEvent.Ui.Retry)
    override fun onItemClicked(url: String) = holder.store.accept(GridEvent.Ui.ItemClicked(url))
    override fun onBackClicked() = onBack()
}
