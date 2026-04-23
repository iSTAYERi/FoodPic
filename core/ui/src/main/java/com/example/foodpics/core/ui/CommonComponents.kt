package com.example.foodpics.core.ui

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodpics.ui.theme.FoodPicsTheme

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer-progress",
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = lerp(base, Color.White, 0.35f)
    val shift = progress * 2f - 1f
    val brush = Brush.linearGradient(
        colorStops = arrayOf(
            (0f + shift).coerceIn(0f, 1f) to base,
            (0.5f + shift).coerceIn(0f, 1f) to highlight,
            (1f + shift).coerceIn(0f, 1f) to base,
        ),
    )
    Box(modifier = modifier.background(brush))
}

@Composable
fun CenteredLoader(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onRetry) { Text(text = "Повторить") }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerBoxPreview() {
    FoodPicsTheme {
        ShimmerBox(modifier = Modifier.fillMaxSize().padding(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun CenteredLoaderPreview() {
    FoodPicsTheme {
        CenteredLoader()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStatePreview() {
    FoodPicsTheme {
        ErrorState(
            message = "Не удалось загрузить картинки",
            onRetry = {},
        )
    }
}
