plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file(project.property("KEY_STORE_FILE") as String)
            keyAlias = project.property("KEY_ALIAS") as String
            storePassword = project.property("KEY_STORE_PASSWORD") as String
            keyPassword = project.property("KEY_PASSWORD") as String
        }
        create("release") {
            storeFile = file(project.property("KEY_STORE_FILE") as String)
            storePassword = project.property("KEY_STORE_PASSWORD") as String
            keyAlias = project.property("KEY_ALIAS") as String
            keyPassword = project.property("KEY_PASSWORD") as String
        }
    }
    namespace = "com.lovigin.app.skillify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lovigin.app.skillify"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0" // UPDATE in User.java, UserViewModel.java

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.transition)
    implementation(libs.coil)
    implementation(libs.androidx.compose)

    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation (libs.androidx.lifecycle.viewmodel.compose)
    implementation (libs.ucrop)
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")

//    implementation ("com.github.agoraio-community:videouikit-android:4.0.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.foundation:foundation:1.4.2")

    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.play.services.auth)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}