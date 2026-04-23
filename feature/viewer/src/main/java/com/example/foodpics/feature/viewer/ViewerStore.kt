package com.example.foodpics.feature.viewer

import android.graphics.Bitmap
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import money.vivid.elmslie.core.store.Actor
import money.vivid.elmslie.core.store.ElmStore
import money.vivid.elmslie.core.store.StateReducer
import money.vivid.elmslie.core.store.Store

data class ViewerState(
    val saving: Boolean = false,
)

sealed interface ViewerEvent {
    sealed interface Ui : ViewerEvent {
        data class SaveRequested(val bitmap: Bitmap) : Ui
    }
    sealed interface Internal : ViewerEvent {
        data class Saved(val uri: Uri) : Internal
        data class SaveFailed(val throwable: Throwable) : Internal
    }
}

sealed interface ViewerCommand {
    data class SaveBitmap(val bitmap: Bitmap) : ViewerCommand
}

sealed interface ViewerEffect {
    data class Saved(val uri: Uri) : ViewerEffect
    data class Error(val message: String) : ViewerEffect
}

internal object ViewerReducer : StateReducer<ViewerEvent, ViewerState, ViewerEffect, ViewerCommand>() {
    override fun Result.reduce(event: ViewerEvent) {
        when (event) {
            is ViewerEvent.Ui.SaveRequested -> {
                if (!state.saving) {
                    state { copy(saving = true) }
                    commands { +ViewerCommand.SaveBitmap(event.bitmap) }
                }
            }
            is ViewerEvent.Internal.Saved -> {
                state { copy(saving = false) }
                effects { +ViewerEffect.Saved(event.uri) }
            }
            is ViewerEvent.Internal.SaveFailed -> {
                state { copy(saving = false) }
                effects {
                    +ViewerEffect.Error(
                        event.throwable.message ?: "Не удалось сохранить картинку",
                    )
                }
            }
        }
    }
}

internal class ViewerActor(
    private val saveImageUseCase: SaveImageUseCase,
) : Actor<ViewerCommand, ViewerEvent>() {

    override fun execute(command: ViewerCommand): Flow<ViewerEvent> = flow {
        when (command) {
            is ViewerCommand.SaveBitmap -> {
                val result = runCatching { saveImageUseCase.save(command.bitmap) }
                result.fold(
                    onSuccess = { emit(ViewerEvent.Internal.Saved(it)) },
                    onFailure = { emit(ViewerEvent.Internal.SaveFailed(it)) },
                )
            }
        }
    }
}

fun viewerStore(saveImageUseCase: SaveImageUseCase): Store<ViewerEvent, ViewerEffect, ViewerState> =
    ElmStore(
        initialState = ViewerState(),
        reducer = ViewerReducer,
        actor = ViewerActor(saveImageUseCase),
    )
