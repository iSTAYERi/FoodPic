package com.example.foodpics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.example.foodpics.root.DefaultRootComponent
import com.example.foodpics.root.RootContent
import com.example.foodpics.ui.theme.FoodPicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appGraph = (application as FoodPicsApplication).appGraph
        val root = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            appGraph = appGraph,
        )

        setContent {
            AppContent(root)
        }
    }
}

@Composable
private fun AppContent(root: com.example.foodpics.root.RootComponent) {
    FoodPicsTheme {
        RootContent(
            component = root,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        )
    }
}
