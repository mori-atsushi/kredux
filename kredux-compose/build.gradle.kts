plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.multiplatform)
    id("com.android.library")
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

kotlin {
    android()
    jvm("desktop")
    js(IR) {
        browser()
    }
    ios()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    watchos()
    tvos()
    tvosSimulatorArm64()

    mingwX64()
    linuxX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(compose.runtime)
                api(project(":kredux-core"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android {
    namespace = "com.moriatsushi.kredux.compose"
    compileSdk = 33

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}
