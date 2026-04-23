package com.example.foodpics.feature.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodpics.ui.theme.FoodPicsTheme

@Composable
fun AboutContent(component: AboutComponent, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "FoodPics",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Версия 1.0",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "Демо-приложение: лента случайных картинок с едой (Foodish API), " +
                "просмотр с зумом и кропом, сохранение в галерею.",
            style = MaterialTheme.typography.bodyMedium,
        )
        TextButton(onClick = component::onBackClicked) {
            Text(text = "Назад")
        }
    }
}

private object PreviewAboutComponent : AboutComponent {
    override fun onBackClicked() {}
}

@Preview(showBackground = true)
@Composable
private fun AboutContentPreview() {
    FoodPicsTheme {
        AboutContent(component = PreviewAboutComponent)
    }
}
