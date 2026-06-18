import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.kapt")
}

val appVersionProperties = Properties().apply {
    val versionFile = rootProject.file("version.properties")
    if (versionFile.exists()) {
        versionFile.inputStream().use(::load)
    }
}

val fallbackVersionCode = appVersionProperties
    .getProperty("VERSION_CODE", "6")
    .toInt()

val fallbackVersionName = appVersionProperties
    .getProperty("VERSION_NAME", "3.1.0-native")

fun String.asBuildConfigString(): String =
    replace("\\", "\\\\")
        .replace("\"", "\\\"")

val updateRepository = System.getenv("APP_UPDATE_REPOSITORY")
    ?.takeIf { it.isNotBlank() }
    ?: System.getenv("GITHUB_REPOSITORY")
        ?.takeIf { it.isNotBlank() }
    ?: appVersionProperties
        .getProperty("UPDATE_REPOSITORY", "")
        .takeIf { it.isNotBlank() }
    ?: ""

val updateManifestUrl = appVersionProperties
    .getProperty("UPDATE_MANIFEST_URL", "")
    .takeIf { it.isNotBlank() }
    ?: if (updateRepository.isNotBlank()) {
        "https://github.com/$updateRepository/releases/latest/download/update.json"
    } else {
        ""
    }


android {
    namespace = "com.luisangel.calculadoramedicamentos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.luisangel.calculadoramedicamentos"
        minSdk = 26
        targetSdk = 36
        versionCode = System.getenv("APP_VERSION_CODE")
            ?.toIntOrNull()
            ?: fallbackVersionCode
        versionName = System.getenv("APP_VERSION_NAME")
            ?.takeIf(String::isNotBlank)
            ?: fallbackVersionName
        multiDexEnabled = true

        buildConfigField(
            "String",
            "UPDATE_REPOSITORY",
            "\"${updateRepository.asBuildConfigString()}\""
        )
        buildConfigField(
            "String",
            "UPDATE_MANIFEST_URL",
            "\"${updateManifestUrl.asBuildConfigString()}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        val keystorePath = System.getenv("ANDROID_KEYSTORE_FILE")
        val keystorePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
        val signingKeyPassword = System.getenv("ANDROID_KEY_PASSWORD")
            ?.takeIf { it.isNotBlank() }
            ?: keystorePassword

        if (!keystorePath.isNullOrBlank()) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjvm-default=all")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kapt {
        arguments {
            arg("room.incremental", "true")
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            )
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")

    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation("org.apache.poi:poi:5.4.1")
    implementation("org.apache.poi:poi-ooxml:5.4.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation("androidx.multidex:multidex:2.0.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
