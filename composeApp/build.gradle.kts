import org.jetbrains.compose.ComposePlugin.CommonComponentsDependencies.resources
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)//ROOM
    alias(libs.plugins.room)//ROOM
}

kotlin {
    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
           // Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }

        val commonMain by getting
        val jbMain by creating {
            dependsOn(commonMain)
        }
        val desktopMain by getting {
            dependsOn(jbMain)
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(jbMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
       /* val wasmJsMain by getting {
            dependsOn(jbMain)
        }*/

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.okhttp)
        //    implementation ("io.ktor:ktor-utils-jvm:$2.3.12")
            implementation("io.ktor:ktor-client-core-jvm:2.3.12")
            implementation("io.ktor:ktor-client-json-jvm:2.3.12")
            implementation(libs.kotlinx.coroutines.android)
            implementation("io.insert-koin:koin-android-ext:3.0.2")
            runtimeOnly("io.insert-koin:koin-android:4.0.0")

        }
        commonMain.dependencies {

            implementation(compose.runtime)
            implementation(compose.foundation)
           // implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.bundles.ktor)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.network)
          //  implementation("io.ktor:ktor-network-tls:2.3.12")
            implementation(libs.ktor.utils)
            implementation(libs.kotlin.serialization)
            implementation(libs.media.kamel)
            implementation(libs.koin.compose)
            //implementation(libs.koin.core)
           // implementation(libs.koin.core.viewmodel)
           // implementation(libs.koin.core.viewmodel.navigation)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.logging)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.mp.stools)//string formatting
            implementation(libs.androidx.navigation.compose)
            implementation(libs.navigation.common)
            implementation(libs.core.bundle)
            //Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)

            implementation(libs.logback.classic)
            implementation(libs.kotlin.logging)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
implementation(libs.testng)
    //    ksp(libs.androidx.room.compiler)
    // room
 /*  add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
  */  // Room: replaced with above ^
  //  add("kspCommonMainMetadata", libs.androidx.room.compiler)

    //test:
    listOf(
        "kspAndroid",
        "kspDesktop",
        "kspIosSimulatorArm64",
        "kspIosX64",
        "kspIosArm64",
        "kspCommonMainMetadata",
    ).forEach {
        add(it, libs.androidx.room.compiler)
    }
}

android {
    namespace = "org.omsi.demoproject"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.omsi.demoproject"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.hamcrest:hamcrest-core:1.1")).using(module("junit:junit:4.10"))
            substitute(module("com.google.guava:listenablefuture:1.0")).using(module("com.google.guava:guava:16.0.1"))
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.omsi.demoproject.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.omsi.demoproject"
            packageVersion = "1.0.0"
        }
    }
}
