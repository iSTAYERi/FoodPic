package com.example.foodpics.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.example.foodpics.di.AppGraph
import com.example.foodpics.feature.grid.DefaultGridComponent
import com.example.foodpics.feature.grid.GridComponent
import com.example.foodpics.feature.menu.AboutComponent
import com.example.foodpics.feature.menu.DefaultAboutComponent
import com.example.foodpics.feature.menu.DefaultMenuComponent
import com.example.foodpics.feature.menu.MenuComponent
import com.example.foodpics.feature.viewer.DefaultViewerComponent
import com.example.foodpics.feature.viewer.ViewerComponent
import kotlinx.serialization.Serializable

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        data class Menu(val component: MenuComponent) : Child
        data class About(val component: AboutComponent) : Child
        data class Grid(val component: GridComponent) : Child
        data class Viewer(val component: ViewerComponent) : Child
    }
}

@OptIn(DelicateDecomposeApi::class)
class DefaultRootComponent(
    componentContext: ComponentContext,
    private val appGraph: AppGraph,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Menu,
        handleBackButton = true,
        childFactory = ::createChild,
    )

    private fun createChild(config: Config, ctx: ComponentContext): RootComponent.Child =
        when (config) {
            Config.Menu -> RootComponent.Child.Menu(
                DefaultMenuComponent(
                    componentContext = ctx,
                    onGridSelected = { navigation.push(Config.Grid) },
                    onAboutSelected = { navigation.push(Config.About) },
                )
            )
            Config.About -> RootComponent.Child.About(
                DefaultAboutComponent(
                    componentContext = ctx,
                    onBack = { navigation.pop() },
                )
            )
            Config.Grid -> RootComponent.Child.Grid(
                DefaultGridComponent(
                    componentContext = ctx,
                    repository = appGraph.foodishRepository,
                    onImageSelected = { url -> navigation.push(Config.Viewer(url)) },
                    onBack = { navigation.pop() },
                )
            )
            is Config.Viewer -> RootComponent.Child.Viewer(
                DefaultViewerComponent(
                    componentContext = ctx,
                    imageUrl = config.imageUrl,
                    saveImageUseCase = appGraph.saveImageUseCase,
                    onBack = { navigation.pop() },
                )
            )
        }

    @Serializable
    private sealed interface Config {
        @Serializable data object Menu : Config
        @Serializable data object About : Config
        @Serializable data object Grid : Config
        @Serializable data class Viewer(val imageUrl: String) : Config
    }
}
