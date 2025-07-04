import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties
import java.util.UUID

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val envProperties = Properties()
file("$rootDir/local.properties").takeIf { it.exists() }?.inputStream()?.use {
    envProperties.load(it)
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
        buildConfigField ("String", "API_KEY", "\"${envProperties["API_KEY"]}\"")
    }

    buildFeatures {
        buildConfig = true
    }

//    signingConfigs {
//        release {
//            storeFile = file("my-release-key.jks")
//            storePassword = "01012004"
//            keyAlias = "my-alias"
//            keyPassword = "01012004"
//        }
//    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            signingConfig = signingConfigs.getByName("release")
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
    packaging {
        exclude ("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
    }
//    configurations.all {
//        resolutionStrategy {
//            force("androidx.test.ext:junit:1.1.5")
//        }
//    }
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
registerUuidTask("model-cn")
registerUuidTask("model-jp")

tasks.named("preBuild") {
    dependsOn( "generateUuidFormodel-en", "generateUuidFormodel-cn", "generateUuidFormodel-jp")
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
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.compose.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//    androidTestImplementation("androidx.test.ext:junit:1.1.5") // ✅ khớp với compose

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

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

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

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}