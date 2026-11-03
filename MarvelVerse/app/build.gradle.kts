plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "ru.mirea.korovkin.marvelverse"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "ru.mirea.korovkin.marvelverse"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // практика 7, п.1 методички: View Binding (ActivityMainBinding в MainActivity)
    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.glide)
    implementation(libs.fragment)
    // практика 7: Navigation Component (NavHostFragment, граф, NavController) + NavigationUI для нижнего меню
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    // практика 8: поворот фото с камеры по EXIF (PhotoDecoder)
    implementation(libs.exifinterface)
    implementation(project(":2_data"))
    implementation(project(":2_domain"))
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}