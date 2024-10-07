package org.omsi.demoproject

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.*
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel
import org.omsi.demoproject.repository.NetworkRepository
import org.omsi.demoproject.ui.TestApp
import org.omsi.demoproject.ui.theme.CustomTheme
import org.omsi.demoproject.viewmodel.MainViewModel


@Composable
@Preview
fun App() {
    //KmLogging.setLoggers(PlatformLogger(FixedLogLevel(true)))
    KmLogging.setLogLevel(LogLevel.Verbose)

    KoinApplication(
        application = { modules(appModule)} ) {
        CustomTheme {

            Box(modifier = Modifier.fillMaxSize()) {
                TestApp()
            }

        }
    }

}



fun koinConfiguration() = koinApplication {
    // your configuration & modules here
    modules(appModule)
}

val appModule = module {
    viewModelOf(::MainViewModel)
    singleOf(::NetworkRepository)
}