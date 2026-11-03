plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.mirea.korovkin.navigationdrawerapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.mirea.korovkin.navigationdrawerapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // п.1 методички — View Binding. В методичке viewBinding { enable = true },
    // в новом AGP это то же самое, только через buildFeatures
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
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.fragment)
    // Navigation Component: хост/граф/контроллер + NavigationUI для шторки и тулбара
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
