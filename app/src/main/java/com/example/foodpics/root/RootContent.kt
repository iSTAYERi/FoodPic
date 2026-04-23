package com.example.foodpics.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.example.foodpics.feature.grid.GridContent
import com.example.foodpics.feature.menu.AboutContent
import com.example.foodpics.feature.menu.MenuContent
import com.example.foodpics.feature.viewer.ViewerContent

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(fade() + scale()),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Menu -> MenuContent(instance.component)
            is RootComponent.Child.About -> AboutContent(instance.component)
            is RootComponent.Child.Grid -> GridContent(instance.component)
            is RootComponent.Child.Viewer -> ViewerContent(instance.component)
        }
    }
}
