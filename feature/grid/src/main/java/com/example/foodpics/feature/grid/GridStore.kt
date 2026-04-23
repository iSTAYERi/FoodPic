package com.example.foodpics.feature.grid

import com.example.foodpics.core.network.FoodishRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import money.vivid.elmslie.core.store.Actor
import money.vivid.elmslie.core.store.ElmStore
import money.vivid.elmslie.core.store.StateReducer
import money.vivid.elmslie.core.store.Store

data class GridState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val images: List<String> = emptyList(),
    val error: String? = null,
)

sealed interface GridEvent {
    sealed interface Ui : GridEvent {
        data object Init : Ui
        data object Refresh : Ui
        data object Retry : Ui
        data class ItemClicked(val url: String) : Ui
    }

    sealed interface Internal : GridEvent {
        data class Loaded(val images: List<String>) : Internal
        data class Failed(val throwable: Throwable) : Internal
    }
}

sealed interface GridCommand {
    data object LoadImages : GridCommand
}

sealed interface GridEffect {
    data class NavigateToViewer(val url: String) : GridEffect
}

internal object GridReducer : StateReducer<GridEvent, GridState, GridEffect, GridCommand>() {
    override fun Result.reduce(event: GridEvent) {
        when (event) {
            is GridEvent.Ui.Init -> {
                if (state.images.isEmpty() && !state.isLoading && state.error == null) {
                    state { copy(isLoading = true, error = null) }
                    commands { +GridCommand.LoadImages }
                } else if (state.images.isEmpty() && state.error == null) {
                    state { copy(isLoading = true, error = null) }
                    commands { +GridCommand.LoadImages }
                }
            }
            is GridEvent.Ui.Refresh -> {
                state { copy(isRefreshing = true, error = null) }
                commands { +GridCommand.LoadImages }
            }
            is GridEvent.Ui.Retry -> {
                state { copy(isLoading = true, error = null) }
                commands { +GridCommand.LoadImages }
            }
            is GridEvent.Ui.ItemClicked -> {
                effects { +GridEffect.NavigateToViewer(event.url) }
            }
            is GridEvent.Internal.Loaded -> {
                state {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        images = event.images,
                        error = null,
                    )
                }
            }
            is GridEvent.Internal.Failed -> {
                state {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = event.throwable.message ?: "Не удалось загрузить картинки",
                    )
                }
            }
        }
    }
}

internal class GridActor(
    private val repository: FoodishRepository,
) : Actor<GridCommand, GridEvent>() {

    override fun execute(command: GridCommand): Flow<GridEvent> = flow {
        when (command) {
            GridCommand.LoadImages -> {
                val result = repository.loadRandom(count = 20)
                result.fold(
                    onSuccess = { emit(GridEvent.Internal.Loaded(it)) },
                    onFailure = { emit(GridEvent.Internal.Failed(it)) },
                )
            }
        }
    }
}

fun gridStore(repository: FoodishRepository): Store<GridEvent, GridEffect, GridState> =
    ElmStore(
        initialState = GridState(),
        reducer = GridReducer,
        actor = GridActor(repository),
    )
