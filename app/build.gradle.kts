plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.axyl.the_atelier"
    // androidx.activity / core 1.18+ yêu cầu compileSdk >= 36
    compileSdk = 36

    defaultConfig {
        applicationId = "com.axyl.the_atelier"
        // Trước đây minSdk = 36 → không cài được lên emulator/thiết bị thường.
        // Adaptive icon (mipmap-anydpi) cần minSdk >= 26.
        minSdk = 26
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.drawerlayout)
    implementation("com.android.volley:volley:1.2.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}