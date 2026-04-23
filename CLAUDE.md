# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Planning mode

When entering plan mode (`EnterPlanMode`), **always run a full interview before proposing a plan**:

- Ask **no more than one question per round** — wait for the answer before the next question.
- For each question, offer **several concrete answer options** and **mark the recommended one** (e.g. `(рекомендую)` / `(recommended)`).
- Continue the interview until requirements, scope, and design trade-offs are clear; only then call `ExitPlanMode` with the final plan.

## Tooling

- **Always use the `context7` MCP server** (`mcp__plugin_context7_context7__resolve-library-id` + `mcp__plugin_context7_context7__query-docs`) when the task touches any library, framework, SDK, API, or CLI tool — Android SDK, Jetpack Compose, Material 3, AndroidX, Kotlin, Gradle, AGP, JUnit, Espresso, etc. Do this even for well-known APIs, because training data may be out of date. Prefer context7 over web search for library/API questions. Skip only for pure refactoring, business-logic debugging, or code review.

## Project

Kotlin + Jetpack Compose + Material 3 Android app with a multi-module ELM/MVI layout. See **Stack** and **Module layout** below.

- `compileSdk = 36` (minor API 1), `minSdk = 29`, `targetSdk = 36`
- Java toolchain target: 11
- Kotlin 2.2.20 with the **Kotlin Compose Compiler plugin** (`org.jetbrains.kotlin.plugin.compose`) — not the older `kotlinCompilerExtensionVersion` approach. Compose BOM drives all Compose library versions.
- Dependencies are managed via the Gradle **Version Catalog** in `gradle/libs.versions.toml`. Add new libs/plugins there first, then reference as `libs.xxx` / `libs.plugins.xxx` in `app/build.gradle.kts`. Do not hard-code versions in module build files.
- `settings.gradle.kts` sets `RepositoriesMode.FAIL_ON_PROJECT_REPOS` and declares `jitpack.io` (needed for `SmartToolFactory/Compose-Cropper`) — declare repos there, never in a module.

## Stack

- **DI:** Metro (`dev.zacsweers.metro`, compile-time compiler plugin). Single `AppGraph` in `:app`; feature components receive deps via constructor.
- **Arch:** Elmslie 3 (ELM/MVI) — `State` / `Event(Ui+Internal)` / `Command` / `Effect` + `StateReducer` + `Actor`; `StoreHolder` in `:core:decompose` bridges store lifecycle with Decompose `InstanceKeeper`.
- **Navigation:** Decompose 3, single-activity. `RootComponent` owns `StackNavigation<Config>`. No Fragments.
- **Network:** Retrofit 2 + OkHttp + `kotlinx.serialization` converter. Base URL `https://foodish-api.com/`, `GET /api/` → one random image per call.
- **Images:** Coil 3 (`io.coil-kt.coil3:coil-compose` + `coil-network-okhttp`).
- **Zoom/crop:** `com.github.SmartToolFactory:Compose-Cropper` (JitPack).
- **Save to gallery:** MediaStore scoped storage → `Pictures/FoodPics/*.jpg` (`RELATIVE_PATH` + `IS_PENDING`). No runtime permissions on `minSdk=29+`.

## Module layout

```
:app              MainActivity, FoodPicsApplication, RootComponent, AppGraph (Metro)
:core:ui          FoodPicsTheme + shared composables (ShimmerBox, CenteredLoader, ErrorState)
:core:network     Retrofit/OkHttp/kotlinx-serialization, FoodishApi, FoodishRepository
:core:decompose   componentScope() + StoreHolder (Elmslie ↔ InstanceKeeper)
:feature:menu     MenuComponent + AboutComponent + their Composables
:feature:grid     GridStore, GridComponent, GridContent
:feature:viewer   ViewerStore, ViewerComponent, ViewerContent, SaveImageUseCase
```

**Adding a feature:** new `:feature:X` module → apply `android.library` + `kotlin.compose` → depend on `:core:ui`, `:core:decompose` (+ network if needed) → write `State/Event/Command/Effect` + `StateReducer` + `Actor` → `Component` holds the store via `storeHolder(key)` → register a new `Config.X` branch in `RootComponent`.

## Commands

Always use the Gradle wrapper (`./gradlew`), not a system `gradle`.

| Task | Command |
|---|---|
| Debug build | `./gradlew :app:assembleDebug` |
| Release build | `./gradlew :app:assembleRelease` |
| Install on connected device/emulator | `./gradlew :app:installDebug` |
| Unit tests (JVM, `app/src/test`) | `./gradlew :app:testDebugUnitTest` |
| Single unit test | `./gradlew :app:testDebugUnitTest --tests "com.example.foodpics.ExampleUnitTest.addition_isCorrect"` |
| Instrumented tests (device/emulator, `app/src/androidTest`) | `./gradlew :app:connectedDebugAndroidTest` |
| Single instrumented test | `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.foodpics.ExampleInstrumentedTest` |
| Android Lint | `./gradlew :app:lintDebug` (report at `app/build/reports/lint-results-debug.html`) |
| Clean | `./gradlew clean` |

## Architecture notes

- Entry point: `MainActivity` (`ComponentActivity`) → `enableEdgeToEdge()` → builds `DefaultRootComponent(defaultComponentContext(), appGraph)` and sets `FoodPicsTheme { RootContent(root) }`. `appGraph` comes from `FoodPicsApplication` (registered in manifest as `.FoodPicsApplication`).
- Theme lives in **`:core:ui` → `com.example.foodpics.ui.theme`** (`Theme.kt` / `Color.kt` / `Type.kt`). `FoodPicsTheme` uses dynamic color on Android 12+; older devices fall back to hand-tuned `LightColorScheme`/`DarkColorScheme` — extend both when you add semantic colors.
- Compose-only: `buildFeatures { compose = true }` is set per module; no View-based XML layouts.
- Release has `isMinifyEnabled = false` with the default `proguard-android-optimize.txt` + `proguard-rules.pro`. If you enable R8, add Compose / Metro / kotlinx-serialization keep rules before shipping.
- Backup is on (`allowBackup = true`); rules live in `res/xml/backup_rules.xml` and `res/xml/data_extraction_rules.xml` — update if you add sensitive local data.

## Gotchas (hard-won)

- **Never apply `org.jetbrains.kotlin.android` plugin** — AGP 9 auto-applies it; explicit apply fails with `Cannot add extension with name 'kotlin'`. For all modules, use only `android.library`/`android.application` + `kotlin.compose` (+ `kotlin.serialization`, `metro` where needed).
- **Metro 0.7+ requires Kotlin ≥ 2.2.20.** Older Kotlin builds emit a compile-time warning before failing later.
- **Elmslie `Actor<Command, Event>` is an `abstract class`**, not an interface — write `: Actor<C, E>()` with parentheses.
- **Decompose 3 `navigation.push(...)` requires `@OptIn(DelicateDecomposeApi::class)`** on the owning class.
- `material-icons-core` is **not** transitive via `material3` — add explicitly if you use `Icons.Default.*`, or avoid them.
- `@Preview` in a feature module needs `ui-tooling-preview` (implementation) + `ui-tooling` (debugImplementation); `:core:ui` already ships both.
- Saving to the gallery uses MediaStore scoped storage — never add `WRITE_EXTERNAL_STORAGE` (not needed on `minSdk=29+`).
