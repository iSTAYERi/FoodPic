package com.example.foodpics.feature.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.foodpics.core.ui.CenteredLoader
import com.example.foodpics.core.ui.ErrorState
import com.example.foodpics.core.ui.ShimmerBox
import com.example.foodpics.ui.theme.FoodPicsTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private class PreviewGridComponent(initial: GridState) : GridComponent {
    override val states: StateFlow<GridState> = MutableStateFlow(initial).asStateFlow()
    override fun onRefresh() {}
    override fun onRetry() {}
    override fun onItemClicked(url: String) {}
    override fun onBackClicked() {}
}

@Preview(showBackground = true)
@Composable
private fun GridContentLoadingPreview() {
    FoodPicsTheme {
        GridContent(
            component = PreviewGridComponent(GridState(isLoading = true)),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GridContentImagesPreview() {
    val fakeUrls = List(8) { "https://foodish-api.com/images/demo/$it.jpg" }
    FoodPicsTheme {
        GridContent(
            component = PreviewGridComponent(
                GridState(
                    isLoading = false,
                    images = fakeUrls,
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GridContentErrorPreview() {
    FoodPicsTheme {
        GridContent(
            component = PreviewGridComponent(
                GridState(
                    isLoading = false,
                    error = "Не удалось загрузить картинки",
                ),
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridContent(component: GridComponent, modifier: Modifier = Modifier) {
    val state by component.states.collectAsState()

    Scaffold(modifier = modifier) { padding ->
        when {
            state.isLoading && state.images.isEmpty() -> {
                CenteredLoader(modifier = Modifier.padding(padding))
            }
            state.error != null && state.images.isEmpty() -> {
                ErrorState(
                    message = state.error ?: "Ошибка",
                    onRetry = component::onRetry,
                    modifier = Modifier.padding(padding),
                )
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = component::onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(
                            items = state.images,
                            key = { index, url -> "$index:$url" },
                        ) { _, url ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { component.onItemClicked(url) },
                            ) {
                                ShimmerBox(modifier = Modifier.fillMaxSize())
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                        if (state.error != null) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = state.error ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
