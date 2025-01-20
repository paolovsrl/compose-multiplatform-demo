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
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
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
            implementation(libs.ktor.client.core.jvm)
            implementation(libs.ktor.client.json.jvm)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android.ext)
            runtimeOnly(libs.koin.android)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.ui.util)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.bundles.ktor)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.network)
            implementation(libs.ktor.utils)
            implementation(libs.kotlin.serialization)
            implementation(libs.media.kamel)
            implementation(libs.koin.compose)
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
            //Settings:
            implementation(libs.multiplatform.settings.no.arg)
            //
            //File System, writing,file picker
            implementation(libs.filekit.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
            implementation(libs.kotlin.logging)
            //Using log4j2 to obtain formatted logging (log4j2.xml)
            //But this was enough:      implementation (libs.slf4j.simple)
            implementation(libs.log4j.api)
            implementation(libs.org.apache.logging.log4j.log4j.core)
            implementation(libs.log4j.slf4j2.impl)
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
       // "kspCommonMainMetadata",
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
    signingConfigs {
        create("release") {
            storeFile =
                file("D:\\AndroidStudioProjects\\ComposeMultiplatform\\OMSI_parameters\\composeApp\\demo_keystore.jks")
            storePassword = "Omsi0000"
            keyAlias = "omsi_key"
            keyPassword = "Omsi0000"
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }

        applicationVariants.all {
            this.outputs
                .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
                .forEach { output ->
                    val variant = this.buildType.name
                    var apkName = "APP_"+variant+"_$versionName"
                    //this.flavorName[0].uppercase() + this.flavorName.substring(1) + "_" + this.versionName
                    // if (variant.isNotEmpty()) apkName += "_$variant"
                    apkName += ".apk"
                    println("ApkName=$apkName ${this.buildType.name}")
                    output.outputFileName = apkName
                }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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
        jvmArgs += listOf("-Xmx2G")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.omsi.demoproject"
            packageVersion = "1.0.0"

            buildTypes.release.proguard{
                version.set("7.5.0")
                optimize.set(false)
                obfuscate.set(false)
                configurationFiles.from("proguard.pro")
            }

            windows {
               // iconFile.set(project.file("/src/desktopMain/resources/ic_launcher_round.ico"))
            }

            linux {
                modules("jdk.security.auth")
                //iconFile.set(project.file("icon.png"))
            }

        }
    }
}
