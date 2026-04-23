package com.example.foodpics.feature.viewer

import android.graphics.Bitmap
import com.arkivanov.decompose.ComponentContext
import com.example.foodpics.core.decompose.storeHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ViewerComponent {
    val imageUrl: String
    val states: StateFlow<ViewerState>
    val effects: Flow<ViewerEffect>
    fun onSaveRequested(bitmap: Bitmap)
    fun onBackClicked()
}

class DefaultViewerComponent(
    componentContext: ComponentContext,
    override val imageUrl: String,
    saveImageUseCase: SaveImageUseCase,
    private val onBack: () -> Unit,
) : ViewerComponent, ComponentContext by componentContext {

    private val holder = storeHolder<ViewerEvent, ViewerEffect, ViewerState>("ViewerStore") {
        viewerStore(saveImageUseCase)
    }

    override val states: StateFlow<ViewerState> = holder.store.states
    override val effects: Flow<ViewerEffect> = holder.store.effects

    override fun onSaveRequested(bitmap: Bitmap) {
        holder.store.accept(ViewerEvent.Ui.SaveRequested(bitmap))
    }

    override fun onBackClicked() = onBack()
}
