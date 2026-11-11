plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dev.sorcerer.trueastronaut.runtime.lsposed"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
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
