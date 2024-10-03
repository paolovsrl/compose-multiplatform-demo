package org.omsi.demoproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import kotlinx.datetime.Clock
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.KoinApplication
import org.koin.dsl.module
import org.lighthousegames.logging.FixedLogLevel
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel
import org.lighthousegames.logging.PlatformLogger
import org.omsi.demoproject.di.appModule
import org.omsi.demoproject.di.provideCanRepositoryModule
import org.omsi.demoproject.di.provideviewModelModule
import org.omsi.demoproject.repository.CanRepository
import org.omsi.demoproject.ui.HomeScreen
import org.omsi.demoproject.viewmodel.MainViewModel




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
