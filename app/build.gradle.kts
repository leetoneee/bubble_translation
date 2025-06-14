import java.util.UUID

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bteamcoding.bubbletranslation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bteamcoding.bubbletranslation"
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
    buildFeatures {
        compose = true
    }
}

fun registerUuidTask(modelName: String) {
    tasks.register("generateUuidFor${modelName}") {
        val outputDir = file("src/main/assets/$modelName")
        val uuidFile = File(outputDir, "uuid")

        outputs.file(uuidFile)

        doLast {
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            val uuid = UUID.randomUUID().toString()
            uuidFile.writeText(uuid)
            println("Generated uuid for $modelName: $uuid")
        }
    }
}

registerUuidTask("model-en")

tasks.named("preBuild") {
    dependsOn( "generateUuidFormodel-en")
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
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.compose.testing)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.foundation.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)

    implementation (libs.text.recognition)
    implementation (libs.translate)
    implementation (libs.kotlinx.coroutines.android)

    implementation (libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.video)

    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
    implementation (libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.coil.compose)
    implementation(libs.androidx.graphics.shapes) // or latest version
    implementation(libs.coil.compose.v250) // Or latest version
    implementation(libs.coil.svg)
    implementation(libs.vosk.android)

    // To recognize Latin script
    implementation ("com.google.mlkit:text-recognition:16.0.1")

    // To recognize Chinese script
    implementation ("com.google.mlkit:text-recognition-chinese:16.0.1")

    // To recognize Devanagari script
    implementation ("com.google.mlkit:text-recognition-devanagari:16.0.1")

    // To recognize Japanese script
    implementation ("com.google.mlkit:text-recognition-japanese:16.0.1")

    // To recognize Korean script
    implementation ("com.google.mlkit:text-recognition-korean:16.0.1")
}