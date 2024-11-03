plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.apollo)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.justjeff.graphqlexample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.justjeff.graphqlexample"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.apollo)
    implementation(libs.hilt.android)
    implementation(libs.store5)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    ksp(libs.hilt.android.compiler)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.apollo.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.room.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

apollo {
    service("service") {
        packageName.set("com.justjeff.graphqlexample.models")
    }
}

kover.reports {
    filters {
        includes {
            classes("com.justjeff.graphqlexample.*")
        }
        excludes {
            classes("*_*") // Files with underscores that Dagger generates
            classes("*Activity")
            classes("com.justjeff.graphqlexample.ui.*")
            classes("com.justjeff.graphqlexample.data.db.*")
            classes("com.justjeff.graphqlexample.data.store.*")
            classes("com.justjeff.graphqlexample.models.*") // GraphQL generated models
        }
    }
    verify.rule {
        minBound(30)
    }
}