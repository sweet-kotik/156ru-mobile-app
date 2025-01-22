plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("kotlin-kapt")
}

android {
    namespace = "com.example.a156ru"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.a156ru"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)

    // Material Design
    implementation(libs.android.material)

    // Сеть и изображения
    implementation(libs.okhttp)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // WebView
    implementation(libs.androidx.webkit)

    // Корутины
    implementation(libs.kotlinx.coroutines)

    // RSS-парсинг
    implementation(libs.rome)
    implementation(libs.jdom)
    implementation(libs.androidsvg)

}