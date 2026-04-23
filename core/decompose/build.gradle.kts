plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.foodpics.core.decompose"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(libs.decompose)
    api(libs.decompose.extensions.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)

    implementation(libs.elmslie.core)
    implementation(libs.elmslie.android)
}
