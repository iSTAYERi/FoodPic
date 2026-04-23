package com.example.foodpics.feature.viewer

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodpics.ui.theme.FoodPicsTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.example.foodpics.core.ui.CenteredLoader
import com.example.foodpics.core.ui.ErrorState
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty

@Composable
fun ViewerContent(component: ViewerComponent, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val state by component.states.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var crop by remember { mutableStateOf(false) }

    LaunchedEffect(component.imageUrl) {
        loadError = null
        imageBitmap = null
        val loader = SingletonImageLoader.get(context)
        val request = ImageRequest.Builder(context)
            .data(component.imageUrl)
            .allowHardware(false)
            .build()
        val result = loader.execute(request)
        if (result is SuccessResult) {
            imageBitmap = result.image.toBitmap().asImageBitmap()
        } else {
            loadError = "Не удалось загрузить картинку"
        }
    }

    LaunchedEffect(component) {
        component.effects.collect { effect ->
            when (effect) {
                is ViewerEffect.Saved -> snackbarHostState.showSnackbar(
                    "Сохранено в галерею",
                )
                is ViewerEffect.Error -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                val bitmap = imageBitmap
                val error = loadError
                when {
                    error != null -> ErrorState(
                        message = error,
                        onRetry = { component.onBackClicked() },
                    )
                    bitmap != null -> ImageCropperBody(
                        imageBitmap = bitmap,
                        crop = crop,
                        onCropped = { cropped ->
                            crop = false
                            component.onSaveRequested(cropped.asAndroidBitmap())
                        },
                    )
                    else -> CenteredLoader()
                }
            }
            Button(
                onClick = { crop = true },
                enabled = imageBitmap != null && !state.saving && !crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                if (state.saving || crop) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(text = "Сохранение…")
                    }
                } else {
                    Text(text = "Сохранить")
                }
            }
        }
    }
}

@Composable
private fun ImageCropperBody(
    imageBitmap: ImageBitmap,
    crop: Boolean,
    onCropped: (ImageBitmap) -> Unit,
) {
    val density = LocalDensity.current
    val handleSize = remember(density) { with(density) { 20.dp.toPx() } }
    val cropProperties = remember(handleSize) {
        CropDefaults.properties(
            cropOutlineProperty = CropOutlineProperty(
                OutlineType.Rect,
                RectCropShape(id = 0, title = "Rect"),
            ),
            handleSize = handleSize,
        )
    }
    val cropStyle = remember { CropDefaults.style() }

    ImageCropper(
        modifier = Modifier.fillMaxSize(),
        imageBitmap = imageBitmap,
        contentDescription = null,
        cropStyle = cropStyle,
        cropProperties = cropProperties,
        crop = crop,
        onCropStart = {},
        onCropSuccess = onCropped,
    )
}

private class PreviewViewerComponent(
    override val imageUrl: String = "https://foodish-api.com/images/demo/1.jpg",
    initial: ViewerState = ViewerState(),
) : ViewerComponent {
    override val states: StateFlow<ViewerState> = MutableStateFlow(initial).asStateFlow()
    override val effects: Flow<ViewerEffect> = emptyFlow()
    override fun onSaveRequested(bitmap: Bitmap) {}
    override fun onBackClicked() {}
}

@Preview(showBackground = true)
@Composable
private fun ViewerContentPreview() {
    FoodPicsTheme {
        ViewerContent(component = PreviewViewerComponent())
    }
}

@Preview(showBackground = true)
@Composable
private fun ViewerContentSavingPreview() {
    FoodPicsTheme {
        ViewerContent(
            component = PreviewViewerComponent(
                initial = ViewerState(saving = true),
            ),
        )
    }
}
