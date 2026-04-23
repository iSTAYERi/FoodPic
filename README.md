# FoodPics

Demo Android app that pulls random food photos from the open [Foodish API](https://github.com/surhud004/Foodish), displays them in a grid, and lets you zoom, crop, and save a picture to the gallery.

## Stack

- **UI** — Kotlin + Jetpack Compose + Material 3 (dynamic color on API 31+).
- **Architecture** — ELM3 ([Elmslie](https://github.com/vivid-money/elmslie) 3.x), essentially MVI: `State` / `Event` (Ui + Internal) / `Command` / `Effect`, one store per feature.
- **DI** — [Metro](https://github.com/zacsweers/metro) (compile-time, Kotlin compiler plugin). Single `AppGraph` in `:app`, feature components receive dependencies through constructor parameters.
- **Navigation** — [Decompose](https://github.com/arkivanov/Decompose) 3 in single-activity mode, no Fragments. `RootComponent` owns a `StackNavigation<Config>` stack: `Menu → About | Grid → Viewer(url)`.
- **Networking** — Retrofit 2 + OkHttp + `kotlinx.serialization` converter.
- **Images** — [Coil 3](https://coil-kt.github.io/coil/) for thumbnails in the grid and for loading the original bitmap before cropping.
- **Zoom + crop** — [SmartToolFactory/Compose-Cropper](https://github.com/SmartToolFactory/Compose-Cropper) 0.5.0 (via JitPack).
- **Storage** — scoped storage via `MediaStore.Images` with `RELATIVE_PATH = Pictures/FoodPics` and `IS_PENDING` handshake. No runtime permissions on `minSdk = 29+`.

## Module layout

```
:app                    Application, MainActivity, RootComponent/RootContent, AppGraph (Metro)
:core:ui                FoodPicsTheme + shared composables (ShimmerBox, CenteredLoader, ErrorState)
:core:network           Retrofit / OkHttp / kotlinx-serialization wiring, FoodishApi, FoodishRepository
:core:decompose         componentScope() + StoreHolder (Elmslie <-> InstanceKeeper bridge)
:feature:menu           MenuComponent + AboutComponent (+ their Composables)
:feature:grid           GridStore (Elmslie), GridComponent, GridContent (LazyVerticalGrid + pull-to-refresh)
:feature:viewer         ViewerStore, ViewerComponent, ViewerContent (ImageCropper + Snackbar), SaveImageUseCase
```

Dependencies are managed via the Gradle [Version Catalog](gradle/libs.versions.toml); `settings.gradle.kts` adds JitPack for the Compose-Cropper.

## Screens

| Screen | What it does |
|---|---|
| **Menu** | Two buttons: `Лента с едой` (→ Grid) and `О приложении` (→ About). |
| **About** | Static info card about the app. |
| **Grid** | Fires 20 parallel `GET /api/` requests, shows results as square tiles in a 2-column grid with Coil + shimmer placeholder. Swipe-down → pull-to-refresh. Tap a tile → Viewer. |
| **Viewer** | Loads the original image via Coil, renders it inside `ImageCropper` (pinch-zoom + draggable crop rectangle). `Сохранить` triggers the cropper → `onCropSuccess` → `SaveImageUseCase` writes a JPEG to `Pictures/FoodPics/FoodPics_<timestamp>.jpg`. Snackbar confirms success/error. |

## Build / run

Use the Gradle wrapper (never a system `gradle`).

| Task | Command |
|---|---|
| Debug APK | `./gradlew :app:assembleDebug` |
| Install on device/emulator | `./gradlew :app:installDebug` |
| Lint | `./gradlew :app:lintDebug` |
| Clean | `./gradlew clean` |

Requirements: Android SDK with API 36 (with minor API 1) installed, JDK 17+, `local.properties` with `sdk.dir=…`.

- `compileSdk = 36`, `minSdk = 29`, `targetSdk = 36`, Java toolchain target 11.
- Kotlin 2.2.20 (Metro 0.7.5 requires ≥ 2.2.20).
- Compose Compiler via the `org.jetbrains.kotlin.plugin.compose` plugin (not the old `kotlinCompilerExtensionVersion`).

## Previews

Each feature screen has one or more `@Preview` composables (Menu light + dark, About, Grid loading / content / error, Viewer idle / saving, plus previews for the shared components in `:core:ui`). They mock the component interfaces inline so no real `ComponentContext` / `Store` is needed to render in the IDE.

## Known limits (MVP)

- Foodish API may return duplicate URLs within a single batch of 20 — accepted.
- Cold start on the Foodish side can take a few seconds (the README on their repo warns about a free hosting instance); the grid shows `CenteredLoader` in the meantime.
- Crop state doesn't survive process death; Decompose restores the navigation stack, Elmslie store survives configuration changes via `InstanceKeeper`.
- No tests shipped with the MVP.
