plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dev.sorcerer.trueastronaut.runtime.lsposed"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.sorcerer.trueastronaut.runtime.lsposed"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-dev"
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core-api"))
    // Legacy Xposed API adapter. Runtime compatibility on Android 16 must be
    // verified on the target ROM; compile-time API availability is not proof.
    compileOnly("de.robv.android.xposed:api:82")
}
