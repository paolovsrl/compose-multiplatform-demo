package org.omsi.demoproject

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

fun MainViewController() = ComposeUIViewController {
    startKoin{

    }
        App()

}
