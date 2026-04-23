package com.example.foodpics.feature.menu

import com.arkivanov.decompose.ComponentContext

interface MenuComponent {
    fun onGridClicked()
    fun onAboutClicked()
}

class DefaultMenuComponent(
    componentContext: ComponentContext,
    private val onGridSelected: () -> Unit,
    private val onAboutSelected: () -> Unit,
) : MenuComponent, ComponentContext by componentContext {

    override fun onGridClicked() = onGridSelected()
    override fun onAboutClicked() = onAboutSelected()
}
