package org.omsi.demoproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

import org.koin.compose.KoinApplication
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel
import org.omsi.demoproject.di.appModule
import org.omsi.demoproject.ui.HomeScreen


@Composable
@Preview
fun App() {
    //KmLogging.setLoggers(PlatformLogger(FixedLogLevel(true)))
    KmLogging.setLogLevel(LogLevel.Verbose)
    KoinApplication(application = {
        modules(appModule())
    }){
        MaterialTheme {
            Box(modifier=Modifier.fillMaxSize()){
                HomeScreen()
            }
        }
    }
}
