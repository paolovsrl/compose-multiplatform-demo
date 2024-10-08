package org.omsi.demoproject

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(1024.dp, 600.dp),
        position = WindowPosition.Aligned(Alignment.Center)//WindowPosition(0.dp, 0.dp)
    )

    startKoin {
    }
    //  StatusPrinter.print(LoggerFactory.getILoggerFactory() as LoggerContext)
    Window(
        onCloseRequest = ::exitApplication,
        title = "Demo App",
        state = state
    ) {
            App()
    }
}



@Preview
@Composable
fun AppDesktopPreview() {
    App()
}