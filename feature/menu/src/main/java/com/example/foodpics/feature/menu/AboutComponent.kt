package com.example.foodpics.feature.menu

import com.arkivanov.decompose.ComponentContext

interface AboutComponent {
    fun onBackClicked()
}

class DefaultAboutComponent(
    componentContext: ComponentContext,
    private val onBack: () -> Unit,
) : AboutComponent, ComponentContext by componentContext {

    override fun onBackClicked() = onBack()
}
