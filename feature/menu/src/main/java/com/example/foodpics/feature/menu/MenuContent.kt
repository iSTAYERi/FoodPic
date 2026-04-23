package com.example.foodpics.feature.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodpics.ui.theme.FoodPicsTheme

@Composable
fun MenuContent(component: MenuComponent, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "FoodPics",
            style = MaterialTheme.typography.headlineLarge,
        )
        Button(
            onClick = component::onGridClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Лента с едой")
        }
        OutlinedButton(
            onClick = component::onAboutClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "О приложении")
        }
    }
}

private object PreviewMenuComponent : MenuComponent {
    override fun onGridClicked() {}
    override fun onAboutClicked() {}
}

@Preview(showBackground = true)
@Composable
private fun MenuContentPreview() {
    FoodPicsTheme {
        MenuContent(component = PreviewMenuComponent)
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MenuContentDarkPreview() {
    FoodPicsTheme {
        MenuContent(component = PreviewMenuComponent)
    }
}
