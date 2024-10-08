package org.omsi.demoproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.LocalKoinApplication
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel
import org.omsi.demoproject.di.getDbModule
import org.omsi.demoproject.di.repositoryModule
import org.omsi.demoproject.di.viewModelModule
import org.omsi.demoproject.ui.TestApp
import org.omsi.demoproject.ui.theme.CustomTheme


@Composable
@Preview
fun App() {
    //KmLogging.setLoggers(PlatformLogger(FixedLogLevel(true)))
    KmLogging.setLogLevel(LogLevel.Verbose)
    //Koin already started at native level
/*
    KoinApplication(
        application = { modules(
            viewModelModule,
            repositoryModule,
            getDbModule(),
        ) } ) */
    KoinContext(){
        LocalKoinApplication.current.loadModules(modules = listOf(
            viewModelModule,
            repositoryModule,
            getDbModule())
        )

        CustomTheme {

            Box(modifier = Modifier.fillMaxSize()) {
                TestApp()
            }

        }
    }

}